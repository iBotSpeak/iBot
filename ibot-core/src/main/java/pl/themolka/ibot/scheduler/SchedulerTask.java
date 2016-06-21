package pl.themolka.ibot.scheduler;

import pl.themolka.ibot.plugin.IBotPlugin;
import pl.themolka.ibot.plugin.Plugin;

public class SchedulerTask extends SimpleSchedulerListener {
    public static final int DEFAULT_ID = -1;

    private final Plugin owner;
    private final SchedulerManager scheduler;
    private int taskId = DEFAULT_ID;

    public SchedulerTask(IBotPlugin owner) {
        this.owner = owner;
        this.scheduler = owner.getQuery().getScheduler();
    }

    public boolean cancelTask() {
        int taskId = this.getTaskId();
        this.taskId = DEFAULT_ID;

        return this.getScheduler().cancel(taskId);
    }

    public Plugin getOwner() {
        return this.owner;
    }

    public SchedulerManager getScheduler() {
        return this.scheduler;
    }

    public int getTaskId() {
        return this.taskId;
    }

    public boolean isTaskRunning() {
        return this.getTaskId() != DEFAULT_ID;
    }

    public SchedulerTask scheduleTask() {
        this.taskId = this.getScheduler().schedule(this, this.getOwner());
        return this;
    }

    public SchedulerTask scheduleAsyncTask() {
        this.taskId = this.getScheduler().scheduleAsync(this, this.getOwner());
        return this;
    }
}
