package pl.themolka.ibot.log;

import org.apache.commons.lang3.time.DateFormatUtils;
import org.jdom2.output.Format;

import java.io.File;

public abstract class ErrorHandler {
    private String filename;
    private Format format;
    private final String message;
    private final Throwable throwable;

    public ErrorHandler(String message, Throwable throwable) {
        long now = System.currentTimeMillis();
        this.filename = String.format("error--%s--%s--%d.xml",
                DateFormatUtils.ISO_DATE_FORMAT.format(now),
                DateFormatUtils.ISO_TIME_NO_T_FORMAT.format(now).replace(":", "-"),
                System.nanoTime()
        );

        this.format = Format.getPrettyFormat();
        this.format.setIndent("    ");

        if (throwable == null) {
            throwable = new Throwable();
        }

        if (message == null) {
            message = throwable.getMessage();
        }

        this.message = message;
        this.throwable = throwable;
    }

    public abstract File getDirectory();

    public File getFile() {
        return new File(this.getDirectory(), this.getFilename());
    }

    public String getFilename() {
        return this.filename;
    }

    public Format getFormat() {
        return this.format;
    }

    public String getMessage() {
        return this.message;
    }

    public Throwable getThrowable() {
        return this.throwable;
    }

    public abstract boolean publish();

    public void setFormat(Format format) {
        this.format = format;
    }
}
