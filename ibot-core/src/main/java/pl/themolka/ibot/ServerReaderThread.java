package pl.themolka.ibot;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.storage.document.MongoServer;

import java.util.ArrayList;
import java.util.List;

public class ServerReaderThread extends Thread implements SingleResultCallback<Void> {
    public static final long INTERVAL = 60 * 1000L; // every 1 minute

    private final IBot iBot;

    private final List<MongoServer> serverList = new ArrayList<>();

    public ServerReaderThread(IBot iBot) {
        super("server reader");
        this.iBot = iBot;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(INTERVAL);
        } catch (InterruptedException ex) {
        }

        while (!this.isInterrupted()) {
            try {
                long took = System.currentTimeMillis();

                this.iBot.getStorage().getServerCollection().findByOperator(new Block<MongoServer>() {
                    @Override
                    public void apply(MongoServer server) {
                        ServerReaderThread.this.serverList.add(server);
                    }
                }, this, this.iBot.getObjectId());

                this.serverList.clear();
                Thread.sleep(Math.max(1L, INTERVAL - (System.currentTimeMillis() - took)));
            } catch (Throwable ignored) {
            }
        }
    }

    @Override
    public void onResult(Void result, Throwable throwable) {
        List<BotQuery> instances = ServerReaderThread.this.iBot.getInstances();

        this.createLoop(instances);
        this.removeLoop(instances);
    }

    private void createLoop(List<BotQuery> instances) {
        for (MongoServer server : this.serverList) {
            boolean create = true;

            for (BotQuery query : instances) {
                if (query.getMongo() == null || query.getObjectId().equals(server.getFieldId())) {
                    create = false;
                    break;
                }
            }

            if (create) {
                this.createServer(server);
            }
        }
    }

    private void createServer(MongoServer server) {
        BotQuery query = ServerReaderThread.this.iBot.createServer(server);
        if (query == null) {
            return;
        }

        query.setMongo(server);

        if (ServerReaderThread.this.iBot.registerInstance(query)) {
            IBot.getLogger().info("Hooking " + query.getIdentifier() + " to " + query.getHost().getHostName() + ":" + query.getHost().getPort() + "...");

            query.asyncStart();
            query.getThread().start();
        }
    }

    private void removeLoop(List<BotQuery> instances) {
        for (BotQuery query : instances) {
            if (query.getMongo() == null) {
                continue;
            }

            boolean remove = true;
            for (MongoServer server : this.serverList) {
                if (query.getObjectId().equals(server.getFieldId())) {
                    remove = false;
                    break;
                }
            }

            if (remove) {
                this.removeQuery(query);
            }
        }
    }

    private void removeQuery(BotQuery query) {
        query.asyncStop();
        ServerReaderThread.this.iBot.unregisterInstance(query);
    }
}
