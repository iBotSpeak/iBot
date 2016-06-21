package pl.themolka.ibot.bot;

import pl.themolka.ibot.IBot;
import pl.themolka.ibot.scheduler.SchedulerExecutor;
import pl.themolka.ibot.stats.BotStatistics;
import pl.themolka.ibot.stats.FullTicksPart;
import pl.themolka.ibot.stats.StatisticPart;
import pl.themolka.ibot.stats.TickTasksPart;
import pl.themolka.ibot.timings.Timing;

import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class BotQueryThread extends Thread {
    public static final long DEFAULT_TICK = 1000L;

    private final BotQuery botQuery;

    private boolean disable;
    private boolean enable;
    private long lastTick = -1;
    private final Map<Runnable, Long> lastTickMap = new WeakHashMap<>();
    private long tick;
    private final List<Runnable> handlerList = new CopyOnWriteArrayList<>();

    public BotQueryThread(BotQuery botQuery) {
        super(botQuery.getIdentifier() + " server");
        this.botQuery = botQuery;

        this.setTick(DEFAULT_TICK);
    }

    @Override
    public void run() {
        String exceptionMessage = "Got exception in the server tick loop - %s: %s";

        while (!this.isInterrupted()) {
            try {
                long took = System.currentTimeMillis();

                try {
                    this.checkOverload();
                    this.updateStatistics();

                    this.onTick();
                } catch (Throwable ex) {
                    this.botQuery.getLogger().trace(String.format(exceptionMessage,
                            ex.getClass().getSimpleName(),
                            ex.getMessage()
                    ), ex);
                }

                try {
                    this.lastTick = System.currentTimeMillis() - took;
                    Thread.sleep(Math.max(1L, this.getTick() - this.getLastTick()));
                } catch (InterruptedException ignored) {}
            } catch (Throwable ex) {
                this.botQuery.getLogger().trace(String.format(exceptionMessage,
                        ex.getClass().getSimpleName(),
                        ex.getMessage()
                ), ex);
            }
        }
    }

    public void checkOverload() {
        if (!this.isInterrupted() && this.getLastTick() >= this.getTick()) {
            this.botQuery.getLogger().warn("Hey! The server is overloaded! Took " + this.getLastTick() + " ms to handle.");
        }
    }

    public void disable(boolean disable) {
        this.disable = disable;
    }

    public void enable(boolean enable) {
        this.enable = enable;
    }

    public void execute(Runnable handler) {
        this.execute(handler, true);
    }

    public void execute(Runnable handler, boolean log) {
        long took = System.currentTimeMillis();
        handler.run();

        if (log) {
            this.lastTickMap.put(handler, System.currentTimeMillis() - took);
        }
    }

    public List<Runnable> getHandlers() {
        return this.handlerList;
    }

    public long getLastTick() {
        return this.lastTick;
    }

    public Map<Runnable, Long> getLastTickMap() {
        return this.lastTickMap;
    }

    public long getTick() {
        return this.tick;
    }

    public void onTick() throws Throwable {
        if (this.disable) {
            this.disable = false;

            IBot.getLogger().info("Shutting down '" + this.botQuery.getIdentifier() + "'...");
            this.botQuery.stop();
            this.interrupt();
            return;
        }

        if (this.enable) {
            this.enable = false;

            this.botQuery.start();
            this.botQuery.login(this.botQuery.getQueryUsername(), this.botQuery.getQueryPassword());
            this.botQuery.selectById(1, false);
        }

        this.lastTickMap.clear();
        for (Runnable handler : this.getHandlers()) {
            this.execute(handler);
        }

        for (SchedulerExecutor handler : this.botQuery.getScheduler().getExecutors()) {
            this.execute(handler);
        }
    }

    public boolean registerHandler(Runnable handler) {
        return this.handlerList.add(handler);
    }

    public void setTick(long tick) {
        if (this.getTick() != tick) {
            this.tick = tick;

            String ticksMessage = "1 tick per second";
            if (tick < 1000L) {
                ticksMessage = 1000L / tick + " ticks per second";
            } else if (tick > 1000L) {
                ticksMessage = (1000L * 60) / tick + " ticks per minute";
            }

            this.botQuery.getLogger().info(String.format("This server is now running in %s (sleeping in %s ms).",
                    ticksMessage,
                    tick
            ));

            if (tick < 100L) { // 0.1 second
                this.botQuery.getLogger().warn(String.format("This server is faster than it should (%s)!",
                        ticksMessage
                ));
            } else if (tick > 1000L * 15) { // 15 seconds
                this.botQuery.getLogger().warn(String.format("This server is slower than it should (%s)!",
                        ticksMessage
                ));
            }
        }
    }

    @Override
    public void start() {
        super.start();
    }

    public boolean unregisterHandler(Runnable handler) {
        return this.handlerList.remove(handler);
    }

    public void updateStatistics() {
        if (this.isInterrupted()) {
            return;
        }

        long now = System.currentTimeMillis();
        BotStatistics statistics = this.botQuery.getStatistics();

        // full ticks
        StatisticPart fullTicksPart = statistics.getPartById(FullTicksPart.PART_ID);
        if (this.getLastTick() != -1 && fullTicksPart != null && fullTicksPart instanceof FullTicksPart) {
            Timing timing = new Timing();
            timing.setProvider(this);
            timing.setTime(now);
            timing.setTook(this.getLastTick());

            ((FullTicksPart) fullTicksPart).addTiming(timing);
        }

        // tick tasks
        StatisticPart tickTasksPart = statistics.getPartById(TickTasksPart.PART_ID);
        if (!this.getLastTickMap().isEmpty() && tickTasksPart != null && tickTasksPart instanceof TickTasksPart) {
            TickTasksPart.TickTask task = new TickTasksPart.TickTask();

            for (Runnable handler : this.getLastTickMap().keySet()) {
                Timing timing = new Timing();
                timing.setProvider(handler);
                timing.setTime(now);
                timing.setTook(this.getLastTickMap().get(handler));

                task.addTiming(timing);
            }

            ((TickTasksPart) tickTasksPart).addTask(task);
        }

        statistics.release();
    }
}
