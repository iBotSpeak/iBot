package pl.themolka.ibot.stats;

import pl.themolka.ibot.timings.Timing;

import java.util.ArrayList;
import java.util.List;

public class FullTicksPart extends StatisticPart {
    public static final String PART_ID = "full-ticks";

    private final List<Timing> timings = new ArrayList<>();

    public FullTicksPart() {
        super(PART_ID);
    }

    public boolean addTiming(Timing timing) {
        return this.timings.add(timing);
    }

    public List<Timing> getTimings() {
        return this.timings;
    }
}
