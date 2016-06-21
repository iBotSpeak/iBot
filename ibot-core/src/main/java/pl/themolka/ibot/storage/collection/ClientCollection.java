package pl.themolka.ibot.storage.collection;

import com.mongodb.Block;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoIterable;
import org.bson.Document;
import org.bson.types.ObjectId;
import pl.themolka.ibot.storage.DefinedTypeBlock;
import pl.themolka.ibot.storage.MongoStorage;
import pl.themolka.ibot.storage.StorageCollection;
import pl.themolka.ibot.storage.document.MongoClient;

public class ClientCollection extends StorageCollection {
    public ClientCollection(MongoStorage storage) {
        super(storage, getCollectionName());

        this.getMongo().createIndex(MongoClient.uidFilter("1"), new SingleResultCallback<String>() {
            @Override
            public void onResult(String result, Throwable throwable) {
            }
        });
    }

    // finding
    public void findById(Block<MongoClient> block, SingleResultCallback<Void> callback,
                         ObjectId id) {
        this.queryById(id).forEach(new DefinedTypeBlock<>(block, new MongoClient(this.storage)), callback);
    }

    public void findByUid(Block<MongoClient> block, SingleResultCallback<Void> callback,
                          String uid) {
        this.queryByUid(uid).forEach(new DefinedTypeBlock<>(block, new MongoClient(this.storage)), callback);
    }

    // querying
    public MongoIterable<Document> queryByUid(String uid) {
        return this.getMongo().find(MongoClient.uidFilter(uid)).limit(1);
    }

    public static String getCollectionName() {
        return "core_clients";
    }
}
