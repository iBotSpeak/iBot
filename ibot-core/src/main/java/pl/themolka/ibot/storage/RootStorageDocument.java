package pl.themolka.ibot.storage;

import com.mongodb.async.client.MongoCollection;
import org.bson.Document;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;

import static com.mongodb.client.model.Filters.*;

public class RootStorageDocument extends StorageDocument {
    /** stores ObjectId */
    public static final String FIELD_ID = "_id";
    /** stores Integer */
    public static final String FIELD_CREATOR_TYPE = "_creator_type";
    /** stores ObjectId */
    public static final String FIELD_CREATOR_ID = "_creator_id";

    private StorageCollection collection;

    public RootStorageDocument(StorageCollection collection) {
        this.collection = collection;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof RootStorageDocument) {
            return ((RootStorageDocument) obj).getFieldId().equals(this.getFieldId());
        }

        return false;
    }

    public StorageCollection getCollection() {
        return this.collection;
    }

    public MongoCollection<Document> getMongoCollection() {
        return this.getCollection().getMongo();
    }

    public void setCollection(StorageCollection collection) {
        this.collection = collection;
    }

    // fields
    public ObjectId getFieldId() {
        return this.get(FIELD_ID, ObjectId.class);
    }

    public int getFieldCreatorType() {
        return this.get(FIELD_CREATOR_TYPE, Integer.class);
    }

    public ObjectId getFieldCreatorId() {
        return this.get(FIELD_CREATOR_ID, ObjectId.class);
    }

    public Document setFieldId(ObjectId field) {
        return this.append(FIELD_ID, field);
    }

    @Helper
    public void setFieldCreator(int type, ObjectId id) {
        this.setFieldCreatorType(type);
        this.setFieldId(id);
    }

    public Document setFieldCreatorType(int field) {
        return this.append(FIELD_CREATOR_TYPE, field);
    }

    public Document setFieldCreatorId(ObjectId field) {
        return this.append(FIELD_CREATOR_ID, field);
    }

    // filters
    public static Bson idFilter(ObjectId id) {
        return eq(FIELD_ID, id);
    }

    public static Bson creatorTypeFilter(int type) {
        return eq(FIELD_CREATOR_TYPE, type);
    }

    public static Bson creatorIdFilter(ObjectId id) {
        return eq(FIELD_CREATOR_ID, id);
    }
}
