package pl.themolka.ibot.storage.document;

import org.bson.conversions.Bson;
import pl.themolka.ibot.storage.MongoStorage;
import pl.themolka.ibot.storage.RootStorageDocument;
import pl.themolka.ibot.storage.collection.ClientCollection;

import static com.mongodb.client.model.Filters.*;

public class MongoClient extends RootStorageDocument {
    /** stores String */
    public static final String FIELD_UID = "uid";

    public MongoClient(MongoStorage storage) {
        super(storage.getCollection(ClientCollection.getCollectionName()));
    }

    public String getFieldUid() {
        return this.get(FIELD_UID, String.class);
    }

    public void setFieldUid(String field) {
        this.append(FIELD_UID, field);
    }

    // static data
    public static Bson uidFilter(String value) {
        return eq(FIELD_UID, value);
    }
}
