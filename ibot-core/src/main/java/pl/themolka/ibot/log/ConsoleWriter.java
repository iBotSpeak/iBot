package pl.themolka.ibot.log;

import org.apache.log4j.helpers.QuietWriter;

import java.io.Writer;

public class ConsoleWriter extends QuietWriter {
    private boolean writable;

    public ConsoleWriter(Writer writer, org.apache.log4j.spi.ErrorHandler errorHandler) {
        super(writer, errorHandler);
    }

    @Override
    public void write(String string) {
        if (this.isWritable()) {
            super.write(string);
        }
    }

    public boolean isWritable() {
        return this.writable;
    }

    public void setWritable(boolean writable) {
        this.writable = writable;
    }
}
