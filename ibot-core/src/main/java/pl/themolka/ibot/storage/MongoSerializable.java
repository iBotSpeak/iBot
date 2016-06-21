package pl.themolka.ibot.storage;

import java.io.Serializable;
import java.util.Map;

public interface MongoSerializable extends IObjectId, Serializable {
    Map<String, Object> serialize(Map<String, Object> data);
}
