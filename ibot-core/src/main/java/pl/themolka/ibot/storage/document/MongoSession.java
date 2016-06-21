package pl.themolka.ibot.storage.document;

import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import pl.themolka.ibot.storage.Helper;
import pl.themolka.ibot.storage.MongoStorage;
import pl.themolka.ibot.storage.collection.SessionCollection;

import java.util.Arrays;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoSession extends ServerRootStorageDocument {
    /** stores ObjectId */
    public static final String FIELD_CLIENT = "client";
    /** stores String */
    public static final String FIELD_IP = "ip";
    /** stores String */
    public static final String FIELD_COUNTRY = "country";
    /** stores Array => String */
    public static final String FIELD_USERNAME = "username";
    /** stores ObjectId */
    public static final String FIELD_DESTROY = "destroy";

    public MongoSession(MongoStorage storage) {
        super(storage.getCollection(SessionCollection.getCollectionName()));
    }

    // fields
    public ObjectId getFieldClient() {
        return this.get(FIELD_CLIENT, ObjectId.class);
    }

    public String getFieldIp() {
        return this.get(FIELD_IP, String.class);
    }

    public String getFieldCountry() {
        return this.get(FIELD_COUNTRY, String.class);
    }

    public List<String> getFieldUsername() {
        String[] results = this.get(FIELD_USERNAME, String[].class);
        if (results == null) {
            results = new String[0];
        }

        return Arrays.asList(results);
    }

    public ObjectId getFieldDestroy() {
        return this.get(FIELD_DESTROY, ObjectId.class);
    }

    @Helper
    public boolean isDestroyed() {
        return this.getFieldDestroy() != null;
    }

    public void setFieldClient(ObjectId field) {
        this.append(FIELD_CLIENT, field);
    }

    public void setFieldIp(String field) {
        this.append(FIELD_IP, field);
    }

    public void setFieldCountry(String field) {
        this.append(FIELD_COUNTRY, field);
    }

    public void setFieldUsername(List<String> field) {
        this.append(FIELD_USERNAME, field.toArray(new String[field.size()]));
    }

    public void setFieldDestroy(ObjectId field) {
        this.append(FIELD_DESTROY, field);
    }

    // filters
    public static Bson clientFilter(ObjectId value) {
        return eq(FIELD_CLIENT, value);
    }

    public static Bson ipFilter(String value) {
        return eq(FIELD_IP, value);
    }

    public static Bson countryFilter(String value) {
        return eq(FIELD_COUNTRY, value);
    }

    public static Bson usernameFilter(String[] value) {
        return eq(FIELD_USERNAME, value);
    }

    public static Bson destroyFilter(ObjectId value) {
        return eq(FIELD_DESTROY, value);
    }
}
