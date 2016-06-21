package pl.themolka.ibot.storage;

import com.mongodb.MongoCredential;
import com.mongodb.ServerAddress;
import com.mongodb.async.client.MongoClient;
import com.mongodb.async.client.MongoClientSettings;
import com.mongodb.async.client.MongoClients;
import com.mongodb.async.client.MongoDatabase;
import com.mongodb.connection.ClusterSettings;
import pl.themolka.ibot.log.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Handler;

public class Storage {
    private final Logger logger;
    private final Handler loggingHandler;

    private MongoClient mongo;
    private MongoDatabase database;

    private final Map<Class<?>, StorageCollection> collections = new HashMap<>();
    private MongoConnector connector = new MongoConnector();

    public Storage(Logger logger) {
        this(logger, new MongoLoggerHandler(logger));
    }

    public Storage(Logger logger, Handler loggingHandler) {
        this.logger = logger;
        this.loggingHandler = loggingHandler;
    }

    public void connect() {
        this.connect(
                this.getConnector().getHost(),
                this.getConnector().getPort(),
                this.getConnector().getDatabase(),
                this.getConnector().getUsername(),
                this.getConnector().getPassword()
        );
    }

    public void connect(String host, int port, String database, String username, char[] password) {
        List<ServerAddress> hosts = new ArrayList<>();
        hosts.add(new ServerAddress(host, port));

        List<MongoCredential> credentials = new ArrayList<>();
        credentials.add(MongoCredential.createCredential(username, database, password));

        this.mongo = MongoClients.create(MongoClientSettings.builder()
                .clusterSettings(ClusterSettings.builder()
                        .hosts(hosts)
                        .build())
//                .credentialList(credentials)
                .build());
        this.database = this.getMongo().getDatabase(database);

        this.readCollections();
    }

    public void disconnect() {
        this.getMongo().close();
    }

    public <T extends StorageCollection> T getCollection(Class<T> clazz) {
        for (StorageCollection collection : this.getCollections().values()) {
            if (collection.getClass().equals(clazz)) {
                return (T) collection;
            }
        }

        return null;
    }

    public StorageCollection getCollection(String name) {
        for (StorageCollection collection : this.getCollections().values()) {
            if (collection.getName().equals(name)) {
                return collection;
            }
        }

        return null;
    }

    public Map<Class<?>, StorageCollection> getCollections() {
        return this.collections;
    }

    public MongoConnector getConnector() {
        return this.connector;
    }

    public Logger getLogger() {
        return this.logger;
    }

    public Handler getLoggingHandler() {
        return this.loggingHandler;
    }

    public MongoClient getMongo() {
        return this.mongo;
    }

    public MongoDatabase getDatabase() {
        return this.database;
    }

    public void registerCollection(StorageCollection... collections) {
        for (StorageCollection collection : collections) {
            if (collection.getClass().equals(StorageCollection.class)) {
                continue;
            } else if (this.getCollections().values().contains(collection)) {
                continue;
            }

            this.collections.put(collection.getClass(), collection);
        }
    }

    public void setConnector(MongoConnector connector) {
        this.connector = connector;
    }

    protected void readCollections() {
        this.collections.clear();
    }
}
