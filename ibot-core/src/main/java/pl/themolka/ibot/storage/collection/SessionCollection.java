package pl.themolka.ibot.storage.collection;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import org.bson.types.ObjectId;
import pl.themolka.ibot.storage.DefinedTypeBlock;
import pl.themolka.ibot.storage.MongoStorage;
import pl.themolka.ibot.storage.StorageCollection;
import pl.themolka.ibot.storage.document.MongoSession;

public class SessionCollection extends StorageCollection {
    public SessionCollection(MongoStorage storage) {
        super(storage, getCollectionName());
    }

    // finding
    public void findById(Block<MongoSession> block, SingleResultCallback<Void> callback,
                         ObjectId id) {
        this.queryById(id).forEach(new DefinedTypeBlock<>(block, new MongoSession(this.storage)), callback);
    }

    public static String getCollectionName() {
        return "core_sessions";
    }
}
