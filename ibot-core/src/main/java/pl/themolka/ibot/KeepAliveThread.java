package pl.themolka.ibot;

import pl.themolka.ibot.bot.BotQuery;

import java.util.Iterator;

public class KeepAliveThread extends Thread {
    private final IBot bot;
    private final long timeout;

    public KeepAliveThread(IBot bot, long timeout) {
        super("keep alive");

        this.bot = bot;
        this.timeout = timeout * 1000L;
    }

    @Override
    public void run() {
        while (!this.isInterrupted()) {
            try {
                Thread.sleep(this.timeout);
            } catch (InterruptedException ex) {
                break;
            }

            Iterator<BotQuery> iterator = this.bot.getInstances().iterator();
            while (iterator.hasNext()) {
                BotQuery query = iterator.next();
                try {
                    query.getOutputHandler().version();
                } catch (Throwable ex) {
                    IBot.getLogger().trace("Could not keep alive " + query.getIdentifier() + ".", ex);
                }
            }
        }
    }
}
