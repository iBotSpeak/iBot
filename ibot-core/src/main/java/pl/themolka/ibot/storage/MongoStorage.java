package pl.themolka.ibot.storage;

import pl.themolka.ibot.log.Logger;
import pl.themolka.ibot.storage.collection.ClientCollection;
import pl.themolka.ibot.storage.collection.ServerCollection;
import pl.themolka.ibot.storage.collection.SessionCollection;

import java.util.logging.Handler;
import java.util.logging.Level;

public class MongoStorage extends Storage {
    public static final java.util.logging.Logger MONGO_LOGGER = java.util.logging.Logger.getLogger("com.mongodb.driver");

    public MongoStorage(Logger logger) {
        super(logger);
    }

    public MongoStorage(Logger logger, Handler loggingHandler) {
        super(logger, loggingHandler);
    }

    @Override
    public void connect(String host, int port, String database, String username, char[] password) {
        java.util.logging.Logger logger = java.util.logging.Logger.getLogger("com.mongodb");
        logger.setLevel(Level.SEVERE);
        logger.setUseParentHandlers(false);

        if (this.getLoggingHandler() != null) {
            logger.addHandler(this.getLoggingHandler());
        }

        super.connect(host, port, database, username, password);
    }

    public ClientCollection getClientCollection() {
        return this.getCollection(ClientCollection.class);
    }

    public ServerCollection getServerCollection() {
        return this.getCollection(ServerCollection.class);
    }

    // server
    public SessionCollection getSessionCollection() {
        return this.getCollection(SessionCollection.class);
    }

    @Override
    protected void readCollections() {
        super.readCollections();

        try {
            this.registerDefaultCollections();
        } catch (ReflectiveOperationException ex) {
            ex.printStackTrace();
        }
    }

    private void registerDefaultCollections() throws ReflectiveOperationException {
        StorageCollection[] collectionArray = {
                new ClientCollection(this),
                new ServerCollection(this),

                new SessionCollection(this)
        };

        this.registerCollection(collectionArray);
    }
}
