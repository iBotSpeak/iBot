package pl.themolka.ibot.storage.document;

import com.google.common.base.Charsets;
import org.bson.conversions.Bson;
import org.bson.types.ObjectId;
import pl.themolka.ibot.storage.Helper;
import pl.themolka.ibot.storage.MongoStorage;
import pl.themolka.ibot.storage.RootStorageDocument;
import pl.themolka.ibot.storage.collection.ServerCollection;

import java.nio.charset.Charset;

import static com.mongodb.client.model.Filters.eq;

public class MongoServer extends RootStorageDocument {
    /** stores String */
    public static final String FIELD_ENCODING = "encoding";
    /** stores ObjectId */
    public static final String FIELD_OPERATOR = "operator";
    /** stores String */
    public static final String FIELD_HOST = "host";
    /** stores Integer */
    public static final String FIELD_PORT = "port";
    /** stores String */
    public static final String FIELD_USERNAME = "username";
    /** stores String */
    public static final String FIELD_PASSWORD = "password";

    public MongoServer(MongoStorage storage) {
        super(storage.getCollection(ServerCollection.getCollectionName()));
    }

    // fields
    public String getFieldEncoding() {
        return this.get(FIELD_ENCODING, String.class);
    }

    @Helper
    public Charset getFieldEncodingCharset() {
        String encoding = this.getFieldEncoding();

        try {
            return Charset.forName(encoding);
        } catch (Exception ex) {
            return Charsets.UTF_8;
        }
    }

    public ObjectId getFieldOperator() {
        return this.get(FIELD_OPERATOR, ObjectId.class);
    }

    public String getFieldHost() {
        return this.get(FIELD_HOST, String.class);
    }

    public int getFieldPort() {
        return this.get(FIELD_PORT, Integer.class);
    }

    public String getFieldUsername() {
        return this.get(FIELD_USERNAME, String.class);
    }

    public String getFieldPassword() {
        return this.get(FIELD_PASSWORD, String.class);
    }

    public void setFieldEncoding(String field) {
        this.append(FIELD_ENCODING, field);
    }

    public void setFieldOperator(ObjectId field) {
        this.append(FIELD_OPERATOR, field);
    }

    public void setFieldHost(String field) {
        this.append(FIELD_HOST, field);
    }

    public void setFieldPort(int field) {
        this.append(FIELD_PORT, field);
    }

    public void setFieldUsername(String field) {
        this.append(FIELD_USERNAME, field);
    }

    public void setFieldPassword(String field) {
        this.append(FIELD_PASSWORD, field);
    }

    // filters
    public static Bson operatorFilter(ObjectId value) {
        return eq(FIELD_OPERATOR, value);
    }
}
