package pl.themolka.ibot.storage;

import com.mongodb.async.client.MongoCollection;
import com.mongodb.async.client.MongoIterable;
import org.bson.Document;
import org.bson.types.ObjectId;

public class StorageCollection {
    protected final MongoStorage storage;
    private MongoCollection<Document> mongo;
    private final String name;

    public StorageCollection(MongoStorage storage, String name) {
        this.storage = storage;
        this.name = name;
        this.mongo = storage.getDatabase().getCollection(name);
    }

    public MongoCollection<Document> getMongo() {
        return this.mongo;
    }

    public String getName() {
        return this.name;
    }

    @Override
    public String toString() {
        return this.getName();
    }

    // querying
    public MongoIterable<Document> queryById(ObjectId id) {
        return this.getMongo().find(RootStorageDocument.idFilter(id)).limit(1);
    }

    public static String getCollectionName() {
        return null;
    }
}
