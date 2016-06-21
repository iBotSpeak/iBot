package pl.themolka.ibot.log;

import org.apache.log4j.Layout;

import java.io.Writer;

public class ConsoleAppender extends org.apache.log4j.ConsoleAppender {
    public ConsoleAppender() {
        super();
    }

    public ConsoleAppender(Layout layout) {
        super(layout);
    }

    public ConsoleAppender(Layout layout, String target) {
        super(layout, target);
    }

    @Override
    public synchronized void setWriter(Writer writer) {
        this.reset();
        this.qw = new ConsoleWriter(writer, this.errorHandler);
        this.writeHeader();
    }

    public ConsoleWriter getWriter() {
        return (ConsoleWriter) this.qw;
    }
}
