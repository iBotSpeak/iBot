package pl.themolka.ibot.scheduler;

import pl.themolka.ibot.bot.BotQuery;

public class AsyncTaskThread extends Thread {
    private final BotQuery botQuery;
    private final AsyncSchedulerExecutor executor;

    public AsyncTaskThread(BotQuery botQuery, AsyncSchedulerExecutor executor) {
        super("#" + executor.getId() + " async task");
        this.botQuery = botQuery;

        this.executor = executor;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            long took = System.currentTimeMillis();

            try {
                this.getExecutor().run();
            } catch (Throwable throwable) {
                this.botQuery.getLogger().error("Exception in the async tick loop: " + throwable.getMessage(), throwable);
            }

            // TODO timings

            try {
                long lastTick = System.currentTimeMillis() - took;
                Thread.sleep(Math.max(1L, this.getTick() - lastTick));
            } catch (InterruptedException ignored) {
            }
        }
    }

    public AsyncSchedulerExecutor getExecutor() {
        return this.executor;
    }

    public long getTick() {
        return this.botQuery.getThread().getTick();
    }
}
