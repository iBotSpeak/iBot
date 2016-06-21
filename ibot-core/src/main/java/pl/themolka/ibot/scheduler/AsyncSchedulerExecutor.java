package pl.themolka.ibot.scheduler;

import pl.themolka.ibot.bot.BotQuery;
import pl.themolka.ibot.plugin.Plugin;

public class AsyncSchedulerExecutor extends SchedulerExecutor {
    private final AsyncTaskThread thread;

    public AsyncSchedulerExecutor(BotQuery botQuery, int id, SchedulerListener listener, Plugin owner) {
        super(botQuery, id, listener, owner);

        this.thread = new AsyncTaskThread(botQuery, this);
    }

    @Override
    public boolean isAsync() {
        return true;
    }

    public AsyncTaskThread getThread() {
        return this.thread;
    }
}
