package pl.themolka.ibot.log.file;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.log.ErrorHandler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class ErrorFileWriter {
    private final IBot iBot;

    private final Document document;
    private final ErrorHandler handler;

    public ErrorFileWriter(IBot iBot, ErrorHandler handler) {
        this.iBot = iBot;

        this.document = new Document();
        this.handler = handler;
    }

    public Document getDocument() {
        return this.document;
    }

    public ErrorHandler getHandler() {
        return this.handler;
    }

    public IOException writeFile() {
        return this.writeFile(this.getHandler().getFile(), this.getHandler().getFormat());
    }

    public IOException writeFile(File file, Format format) {
        IOException io = null;

        FileOutputStream fileOutput = null;
        BufferedOutputStream outputStream = null;
        try {
            fileOutput = new FileOutputStream(file);
            outputStream = new BufferedOutputStream(fileOutput);

            XMLOutputter outputter = new XMLOutputter(format);
            outputter.output(this.getDocument(), outputStream);
        } catch (IOException ex) {
            io = ex;
        } finally {
            if (outputStream != null) {
                try {
                    outputStream.close();
                }
                catch (IOException ex) {
                    io = ex;
                }
            }

            if (fileOutput != null) {
                try {
                    fileOutput.close();
                } catch (IOException ex) {
                    io = ex;
                }
            }
        }

        return io;
    }

    public void writeXML() {
        Element root = ErrorFileElement.buildXMLMap(this.iBot, this.handler, new Element("error"));
        this.getDocument().setRootElement(root);
    }
}
