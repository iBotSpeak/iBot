package pl.themolka.ibot.storage.document;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import pl.themolka.ibot.storage.Helper;
import pl.themolka.ibot.storage.RootStorageDocument;
import pl.themolka.ibot.storage.StorageCollection;

import static com.mongodb.client.model.Filters.*;

public class ServerRootStorageDocument extends RootStorageDocument {
    /** stores ObjectId */
    public static final String FIELD_SERVER = "server";

    public ServerRootStorageDocument(StorageCollection collection) {
        super(collection);
    }

    // fields
    public ObjectId getFieldServer() {
        return this.get(FIELD_SERVER, ObjectId.class);
    }

    public void setFieldServer(String field) {
        this.append(FIELD_SERVER, field);
    }

    // filters
    public static Bson serverFilter(ObjectId value) {
        return eq(FIELD_SERVER, value);
    }

    @Helper
    public static Bson serverFilterAnd(ObjectId value, Bson and) {
        return and(serverFilter(value), and);
    }
}
