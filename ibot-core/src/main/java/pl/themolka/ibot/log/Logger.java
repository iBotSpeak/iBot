package pl.themolka.ibot.log;

import org.apache.log4j.Level;
import org.apache.log4j.Priority;
import org.apache.log4j.spi.LoggerFactory;
import pl.themolka.ibot.IBot;

public class Logger extends org.apache.log4j.Logger {
    private IBot iBot;

    private ConsoleWriter writer;

    protected Logger(String name) {
        super(name);

        this.setLevel(Level.ALL);
    }

    @Override
    protected void forcedLog(String fqcn, Priority level, Object message, Throwable t) {
        super.forcedLog(fqcn, level, message, t);

        if (this.iBot != null && (level.toInt() == Level.TRACE.toInt() || level.toInt() == Level.ERROR.toInt())) {
            String messageString = null;
            if (message != null) {
                messageString = message.toString();
            }

            this.publishError(messageString, t);
        }
    }

    public ConsoleWriter getWriter() {
        return this.writer;
    }

    public boolean publishError(String message, Throwable throwable) {
        ErrorHandler handler = this.createNewHandler(message, throwable);
        if (handler.publish()) {
            this.info(String.format("Error file saved as '%s' in '%s'.",
                    handler.getFilename(),
                    handler.getDirectory().getPath()
            ));
            return true;
        }

        this.info("Could not publish the error file.");
        return false;
    }

    public void setBotInstance(IBot iBot) {
        this.iBot = iBot;
    }

    public void setWriter(ConsoleWriter writer) {
        this.writer = writer;
    }

    protected ErrorHandler createNewHandler(String message, Throwable throwable) {
        return new GlobalErrorHandler(this.iBot, message, throwable);
    }

    public static Logger newBotInstance() {
        return (Logger) Logger.getLogger(IBot.class.getName(), new SimpleLoggerFactory());
    }

    private static class SimpleLoggerFactory implements LoggerFactory {
        @Override
        public org.apache.log4j.Logger makeNewLoggerInstance(String name) {
            return new Logger(name);
        }
    }
}
