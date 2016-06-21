package pl.themolka.ibot.json;

import com.google.gson.JsonElement;

import java.io.Serializable;

public interface IJsonSerializable extends Serializable {
    JsonElement serialize();

    void deserialize(JsonElement json);
}
