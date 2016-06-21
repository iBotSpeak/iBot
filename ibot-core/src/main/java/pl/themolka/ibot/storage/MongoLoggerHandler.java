package pl.themolka.ibot.storage;

import pl.themolka.ibot.log.Logger;
import pl.themolka.ibot.log.LoggingHandler;

import java.util.logging.LogRecord;

public class MongoLoggerHandler extends LoggingHandler {
    private final Logger logger;

    public MongoLoggerHandler(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void publish(LogRecord record) {
        this.getLogger().log(this.transformLevel(record.getLevel()), record.getMessage());
    }

    @Override
    public void flush() {
    }

    @Override
    public void close() throws SecurityException {
    }

    public Logger getLogger() {
        return this.logger;
    }
}
