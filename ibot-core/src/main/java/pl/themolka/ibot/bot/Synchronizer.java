package pl.themolka.ibot.bot;

import org.bson.Document;
import org.bson.conversions.Bson;
import pl.themolka.ibot.client.TSConnectedClient;
import pl.themolka.ibot.response.ClientInfoQuery;
import pl.themolka.ibot.response.ClientListQuery;
import pl.themolka.ibot.storage.EmptyResultCallback;
import pl.themolka.ibot.storage.StorageDocument;
import pl.themolka.ibot.storage.document.MongoSession;
import pl.themolka.iserverquery.client.ClientDisconnectEvent;
import pl.themolka.iserverquery.client.ConnectedClient;
import pl.themolka.iserverquery.command.CommandContext;
import pl.themolka.itsquery.server.TSServer;

import java.util.ArrayList;
import java.util.List;

public class Synchronizer implements Runnable {
    private final BotQuery botQuery;

    private int ticks;

    public Synchronizer(BotQuery botQuery) {
        this.botQuery = botQuery;

        this.botQuery.getResponses().registerQuery(new ClientListResponse(botQuery));
        this.botQuery.getResponses().registerQuery(new ClientInfoResponse(botQuery));
    }

    @Override
    public void run() {
        this.ticks++;

        if (this.ticks >= 10) {
            if (this.botQuery.getOutputHandler().executeRaw("clientlist")) {
                this.ticks = 0;
            }
        }
    }

    private class ClientInfoResponse extends ClientInfoQuery {
        public ClientInfoResponse(BotQuery botQuery) {
            super(botQuery);
        }

        @Override
        public void onResponse(CommandContext context) {
            List<TSConnectedClient> timeout = new ArrayList<>();

            for (ConnectedClient online : this.botQuery.getServer().getConnectedClients()) {
                if (online instanceof TSConnectedClient) {
                    TSConnectedClient client = (TSConnectedClient) online;

                    if (client.timeout()) {
                        timeout.add(client);
                    }
                }
            }

            for (TSConnectedClient client : timeout) {
                client.getSession().destroy();

                this.botQuery.getEvents().post(new ClientDisconnectEvent(client));
                ((TSServer) this.botQuery.getServer()).unregisterClient(client);

                Document document = StorageDocument.createServer(botQuery, client.getSession());
                Bson whereClause = MongoSession.serverFilterAnd(this.botQuery.getObjectId(), MongoSession.clientFilter(client.getObjectId()));
                this.botQuery.getStorage().getSessionCollection().getMongo()
                        .findOneAndReplace(whereClause, document, new EmptyResultCallback<Document>());
            }
        }
    }

    private class ClientListResponse extends ClientListQuery {
        public ClientListResponse(BotQuery botQuery) {
            super(botQuery);
        }

        @Override
        public void onResponse(CommandContext context) {
            this.botQuery.getOutputHandler().executeRaw("clientinfo clid=" + context.getFlagInt("clid"));
        }
    }
}
