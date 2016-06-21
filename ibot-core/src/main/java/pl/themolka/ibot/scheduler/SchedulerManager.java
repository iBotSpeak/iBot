package pl.themolka.ibot.scheduler;

import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.plugin.Plugin;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SchedulerManager {
    private final BotQuery botQuery;

    private final List<SchedulerExecutor> executorList = new CopyOnWriteArrayList<>();

    public SchedulerManager(BotQuery botQuery) {
        this.botQuery = botQuery;
    }

    public boolean cancel(SchedulerExecutor executor) {
        if (executor instanceof AsyncSchedulerExecutor) {
            ((AsyncSchedulerExecutor) executor).getThread().interrupt();
        }

        executor.onDestroy();
        return this.executorList.remove(executor);
    }

    public boolean cancel(int id) {
        SchedulerExecutor executor = this.getExecutor(id);
        if (executor != null) {
            return this.cancel(executor);
        }
        return false;
    }

    public int cancelAll() {
        int done = 0;
        for (SchedulerExecutor executor : this.getExecutors()) {
            this.cancel(executor);
            done++;
        }
        return done;
    }

    public int cancelAll(Plugin owner) {
        int done = 0;
        for (SchedulerExecutor executor : this.getExecutors()) {
            if (executor.getOwner().equals(owner)) {
                this.cancel(executor);
                done++;
            }
        }
        return done;
    }

    public SchedulerExecutor getExecutor(int id) {
        for (SchedulerExecutor executor : this.getExecutors()) {
            if (executor.getId() == id) {
                return executor;
            }
        }
        return null;
    }

    public List<SchedulerExecutor> getExecutors() {
        return this.executorList;
    }

    public int schedule(SchedulerListener scheduler, Plugin owner) {
        SchedulerExecutor executor = new SchedulerExecutor(this.botQuery, this.getNextUniqueId(), scheduler, owner);
        this.executorList.add(executor);

        executor.onCreate();
        return executor.getId();
    }

    public int schedule(final Runnable scheduler, Plugin owner) {
        return this.schedule(new SimpleSchedulerListener() {
            @Override
            public void onTick(long ticks) {
                scheduler.run();
            }
        }, owner);
    }

    public int scheduleAsync(SchedulerListener scheduler, Plugin owner) {
        AsyncSchedulerExecutor executor = new AsyncSchedulerExecutor(this.botQuery, this.getNextUniqueId(), scheduler, owner);
        this.executorList.add(executor);

        executor.getThread().start();
        executor.onCreate();
        return executor.getId();
    }

    public int scheduleAsync(final Runnable scheduler, Plugin owner) {
        return this.schedule(new SimpleSchedulerListener() {
            @Override
            public void onTick(long ticks) {
                scheduler.run();
            }
        }, owner);
    }

    private synchronized int getNextUniqueId() {
        int id = 0;
        while (true) {
            if (this.getExecutor(id) == null) {
                return id;
            }
            id++;
        }
    }
}
