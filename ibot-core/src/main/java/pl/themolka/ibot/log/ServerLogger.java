package pl.themolka.ibot.log;

import org.apache.log4j.spi.LoggerFactory;
import pl.themolka.ibot.bot.BotQuery;

public class ServerLogger extends Logger {
    private final BotQuery botQuery;

    protected ServerLogger(BotQuery botQuery, String name) {
        super(name);
        this.botQuery = botQuery;
    }

    @Override
    protected ErrorHandler createNewHandler(String message, Throwable throwable) {
        return new ServerErrorHandler(this.botQuery, message, throwable);
    }

    public static ServerLogger newInstance(BotQuery botQuery) {
        String name = botQuery.getClass().getName() + "-" + botQuery.getIdentifier();
        return (ServerLogger) Logger.getLogger(name, new ServerLoggerFactory(botQuery));
    }

    private static class ServerLoggerFactory implements LoggerFactory {
        private final BotQuery botQuery;

        public ServerLoggerFactory(BotQuery botQuery) {
            this.botQuery = botQuery;
        }

        @Override
        public org.apache.log4j.Logger makeNewLoggerInstance(String name) {
            return new ServerLogger(this.botQuery, name);
        }
    }
}
