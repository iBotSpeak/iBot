package pl.themolka.ibot.storage;

import org.bson.Document;
import org.bson.types.ObjectId;
import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.storage.document.ServerRootStorageDocument;
import pl.themolka.ibot.storage.var.DBCreatorType;

import java.util.LinkedHashMap;
import java.util.Map;

public class StorageDocument extends Document {
    public StorageDocument() {
    }

    public StorageDocument(String key, Object value) {
        super(key, value);
    }

    public StorageDocument(Map<String, Object> map) {
        super(map);
    }

    // serialization
    public static Document createGlobal(MongoSerializable object) {
        ObjectId objectId = object.getObjectId();
        if (objectId == null) {
            objectId = generateObjectId();
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put(RootStorageDocument.FIELD_ID, objectId);
        data.put(RootStorageDocument.FIELD_CREATOR_TYPE, DBCreatorType.GLOBAL.intValue());
        data.put(RootStorageDocument.FIELD_CREATOR_ID, DBCreatorType.VALUE_GLOBAL_ID);

        return new Document(object.serialize(data));
    }

    public static Document createServer(BotQuery botQuery, MongoSerializable object) {
        ObjectId objectId = object.getObjectId();
        if (objectId == null) {
            objectId = generateObjectId();
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put(ServerRootStorageDocument.FIELD_ID, objectId);
        data.put(ServerRootStorageDocument.FIELD_CREATOR_TYPE, DBCreatorType.SERVER.intValue());
        data.put(ServerRootStorageDocument.FIELD_CREATOR_ID, botQuery.getObjectId());
        data.put(ServerRootStorageDocument.FIELD_SERVER, botQuery.getObjectId());

        return new Document(object.serialize(data));
    }

    private static ObjectId generateObjectId() {
        return new ObjectId();
    }
}
