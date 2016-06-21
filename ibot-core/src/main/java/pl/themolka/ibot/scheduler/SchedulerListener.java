package pl.themolka.ibot.scheduler;

public interface SchedulerListener {
    void onTick(long ticks);

    void onSecond(long seconds);

    void onMinute(long minutes);

    void onHour(long hours);
}
