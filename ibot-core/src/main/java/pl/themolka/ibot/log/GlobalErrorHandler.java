package pl.themolka.ibot.log;

import pl.themolka.ibot.IBot;
import pl.themolka.ibot.log.file.ErrorFileWriter;

import java.io.File;

public class GlobalErrorHandler extends ErrorHandler {
    private final IBot iBot;

    private final File directory;

    public GlobalErrorHandler(IBot iBot, String message, Throwable throwable) {
        super(message, throwable);
        this.iBot = iBot;

        this.directory = new File("errors");
        this.directory.mkdirs();
    }

    @Override
    public File getDirectory() {
        return this.directory;
    }

    @Override
    public boolean publish() {
        try {
            ErrorFileWriter writer = new ErrorFileWriter(this.iBot, this);
            writer.writeXML();

            return writer.writeFile() == null;
        } catch (Throwable ex) {
            return false;
        }
    }
}
