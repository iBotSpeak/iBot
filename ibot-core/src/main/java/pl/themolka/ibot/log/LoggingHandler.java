package pl.themolka.ibot.log;

import java.util.logging.Handler;
import java.util.logging.Level;

public abstract class LoggingHandler extends Handler {
    public org.apache.log4j.Level transformLevel(Level level) {
        if (level.equals(Level.ALL)) {
            return org.apache.log4j.Level.ALL;
        } else if (level.equals(Level.FINEST)) {
            return org.apache.log4j.Level.DEBUG;
        } else if (level.equals(Level.FINER)) {
            return org.apache.log4j.Level.DEBUG;
        } else if (level.equals(Level.FINE)) {
            return org.apache.log4j.Level.DEBUG;
        } else if (level.equals(Level.CONFIG)) {
            return org.apache.log4j.Level.INFO;
        } else if (level.equals(Level.INFO)) {
            return org.apache.log4j.Level.INFO;
        } else if (level.equals(Level.WARNING)) {
            return org.apache.log4j.Level.WARN;
        } else if (level.equals(Level.SEVERE)) {
            return org.apache.log4j.Level.FATAL;
        } else if (level.equals(Level.OFF)) {
            return org.apache.log4j.Level.OFF;
        } else {
            return org.apache.log4j.Level.INFO;
        }
    }
}
