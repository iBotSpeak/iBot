package pl.themolka.ibot.stats;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import pl.themolka.ibot.json.JsonSerializable;

import java.util.ArrayList;
import java.util.List;

public class Statistics extends JsonSerializable {
    private final List<StatisticPart> parts = new ArrayList<>();

    @Override
    public JsonElement serialize() {
        return new Gson().toJsonTree(this.getParts());
    }

    @Override
    public void deserialize(JsonElement json) {
        new Gson().fromJson(json, this.getParts().getClass());
    }

    public boolean addPart(StatisticPart part) {
        if (this.getPartById(part.getId()) != null) {
            return false;
        }

        return this.parts.add(part);
    }

    public StatisticPart getPartById(String id) {
        for (StatisticPart part : this.getParts()) {
            if (part.getId().equals(id)) {
                return part;
            }
        }

        return null;
    }

    public List<StatisticPart> getParts() {
        return this.parts;
    }

    public void release() {
    }
}
