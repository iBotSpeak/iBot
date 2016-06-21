package pl.themolka.ibot.bot;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import pl.themolka.ibot.storage.StorageDocument;
import pl.themolka.ibot.storage.collection.ServerCollection;
import pl.themolka.ibot.storage.document.MongoServer;

public class QueryServerIdentifier implements Runnable {
    private final BotQuery botQuery;

    private final MongoServer server;
    private final ServerCollection servers;

    public QueryServerIdentifier(BotQuery botQuery) {
        this.botQuery = botQuery;

        this.server = new MongoServer(botQuery.getStorage());
        this.server.putAll(StorageDocument.createGlobal(botQuery));

        this.servers = botQuery.getStorage().getServerCollection();
    }

    @Override
    public void run() {
        this.findQuery();
    }

    public void awake() {
        synchronized (this.botQuery) {
            this.botQuery.notify();
        }
    }

    public void findQuery() {
        final boolean[] exists = {false}; // hacky

        this.servers.findById(new Block<MongoServer>() {
            @Override
            public void apply(MongoServer server) {
                exists[0] = true;
                QueryServerIdentifier.this.updateQuery(server.getFieldId());
            }
        }, new SingleResultCallback<Void>() {
            @Override
            public void onResult(Void result, Throwable throwable) {
                if (!exists[0]) {
                    QueryServerIdentifier.this.insertQuery();
                }
            }
        }, this.botQuery.getObjectId());
    }

    public void insertQuery() {
        this.servers.insert(this.server, new SingleResultCallback<Void>() {
            @Override
            public void onResult(Void result, Throwable throwable) {
                QueryServerIdentifier.this.awake();
            }
        });
    }

    public void updateQuery(ObjectId objectId) {
        Bson whereClause = MongoServer.idFilter(objectId);
        this.servers.getMongo()
                .findOneAndUpdate(whereClause, new Document(this.server), new SingleResultCallback<Document>() {
                    @Override
                    public void onResult(Document result, Throwable throwable) {
                        QueryServerIdentifier.this.botQuery.setObjectId(result.get(MongoServer.FIELD_ID, ObjectId.class));
                        QueryServerIdentifier.this.awake();
                    }
                });
    }
}
