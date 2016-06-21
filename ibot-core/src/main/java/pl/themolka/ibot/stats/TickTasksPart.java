package pl.themolka.ibot.stats;

import pl.themolka.ibot.json.JsonSerializable;
import pl.themolka.ibot.timings.Timing;

import java.util.ArrayList;
import java.util.List;

public class TickTasksPart extends StatisticPart {
    public static final String PART_ID = "tick-tasks";

    private final List<TickTask> tasks = new ArrayList<>();

    public TickTasksPart() {
        super(PART_ID);
    }

    public boolean addTask(TickTask task) {
        return this.tasks.add(task);
    }

    public List<TickTask> getTasks() {
        return this.tasks;
    }

    public static class TickTask extends JsonSerializable {
        private final List<Timing> timings = new ArrayList<>();

        public boolean addTiming(Timing timing) {
            return this.timings.add(timing);
        }

        public List<Timing> getTimings() {
            return this.timings;
        }
    }
}
