package pl.themolka.ibot.scheduler;

import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.bot.BotQueryThread;
import pl.themolka.ibot.plugin.Plugin;

public class SchedulerExecutor implements Comparable<SchedulerExecutor>, Runnable {
    private final BotQueryThread botQueryThread;
    private final int id;
    private final SchedulerListener listener;
    private final Plugin owner;
    private long ticks, seconds, minutes, hours;

    public SchedulerExecutor(BotQuery botQuery, int id, SchedulerListener listener, Plugin owner) {
        this.botQueryThread = botQuery.getThread();
        this.id = id;
        this.listener = listener;
        this.owner = owner;
    }

    @Override
    public int compareTo(SchedulerExecutor compare) {
        return new Integer(compare.getId()).compareTo(this.getId());
    }

    @Override
    public void run() {
        // ticks
        this.onTick();

        // seconds
        boolean zero = 1000L / this.botQueryThread.getTick() <= 0;
        if (zero || this.ticks % (1000L / this.botQueryThread.getTick()) == 0) {
            this.onSecond();

            // minutes
            if (this.seconds % 60 == 0) {
                this.onMinute();

                // hours
                if (this.minutes % 60 == 0) {
                    this.onHour();
                }
            }
        }
    }

    public int getId() {
        return this.id;
    }

    public SchedulerListener getListener() {
        return this.listener;
    }

    public Plugin getOwner() {
        return this.owner;
    }

    public boolean isAsync() {
        return false;
    }

    public void onCreate() {
    }

    public void onDestroy() {
    }

    public void onTick() {
        this.getListener().onTick(this.ticks);
        this.ticks++;
    }

    public void onSecond() {
        this.getListener().onSecond(this.seconds);
        this.seconds++;
    }

    public void onMinute() {
        this.getListener().onMinute(this.minutes);
        this.minutes++;
    }

    public void onHour() {
        this.getListener().onHour(this.hours);
        this.hours++;
    }
}
