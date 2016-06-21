package pl.themolka.ibot.storage.collection;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.FindIterable;
import org.bson.Document;
import org.bson.types.ObjectId;
import pl.themolka.ibot.storage.DefinedTypeBlock;
import pl.themolka.ibot.storage.MongoStorage;
import pl.themolka.ibot.storage.StorageCollection;
import pl.themolka.ibot.storage.document.MongoServer;

public class ServerCollection extends StorageCollection {
    public ServerCollection(MongoStorage storage) {
        super(storage, getCollectionName());
    }

    // inserting
    public void insert(MongoServer server, SingleResultCallback<Void> callback) {
        this.getMongo().insertOne(new Document(server), callback);
    }

    // finding
    public void findById(Block<MongoServer> block, SingleResultCallback<Void> callback,
                         ObjectId id) {
        this.queryById(id).forEach(new DefinedTypeBlock<>(block, new MongoServer(this.storage)), callback);
    }

    public void findByOperator(Block<MongoServer> block, SingleResultCallback<Void> callback,
                               ObjectId operator) {
        this.queryByOperator(operator).forEach(new DefinedTypeBlock<>(block, new MongoServer(this.storage)), callback);
    }

    public FindIterable<Document> queryByOperator(ObjectId operator) {
        return this.getMongo().find(MongoServer.operatorFilter(operator));
    }

    public static String getCollectionName() {
        return "core_servers";
    }
}
