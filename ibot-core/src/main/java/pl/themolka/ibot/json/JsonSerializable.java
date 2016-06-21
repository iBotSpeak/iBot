package pl.themolka.ibot.json;

import com.google.gson.Gson;
import com.google.gson.JsonElement;

public class JsonSerializable implements IJsonSerializable {
    @Override
    public JsonElement serialize() {
        return new Gson().toJsonTree(this);
    }

    @Override
    public void deserialize(JsonElement json) {
        new Gson().fromJson(json, this.getClass());
    }
}
