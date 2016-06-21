package pl.themolka.ibot.stats;

import com.google.gson.annotations.SerializedName;
import pl.themolka.ibot.json.JsonSerializable;

public class StatisticPart extends JsonSerializable {
    @SerializedName(value = "_id")
    private String id;

    private StatisticPart() {
    }

    public StatisticPart(String id) {
        this.id = id;
    }

    public String getId() {
        return this.id;
    }
}
