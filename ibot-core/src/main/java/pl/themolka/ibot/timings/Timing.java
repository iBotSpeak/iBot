package pl.themolka.ibot.timings;

import pl.themolka.ibot.json.JsonSerializable;

public class Timing extends JsonSerializable {
    private Object provider;
    private long time;
    private long took;

    public Object getProvider() {
        return this.provider;
    }

    public long getTime() {
        return this.time;
    }

    public long getTook() {
        return this.took;
    }

    public void setProvider(Object provider) {
        this.provider = provider;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public void setTook(long took) {
        this.took = took;
    }
}
