package pl.themolka.ibot.plugin;

import org.jdom2.Element;
import pl.themolka.ibot.xml.XMLException;
import pl.themolka.ibot.xml.XMLReadable;

import java.io.File;

public class PluginDataFile implements XMLReadable {
    public static final String DEFAULT_VERSION = "1.0-SNAPSHOT";

    private File directory;
    private String main;
    private String name;
    private final Element plugin;
    private String version;

    public PluginDataFile(Element plugin) {
        this.plugin = plugin;
    }

    @Override
    public void read() throws XMLException {
        String main = this.plugin.getAttributeValue("main");
        String name = this.plugin.getChildText("name");
        String version = this.plugin.getChildText("version");

        if (main == null || name == null) {
            throw new XMLException("Main class or plugin name was not specified.");
        }

        if (version == null) {
            version = DEFAULT_VERSION;
        }

        this.main = main;
        this.name = name;
        this.version = version;
    }

    public File getDirectory() {
        return this.directory;
    }

    public String getMain() {
        return this.main;
    }

    public String getName() {
        return this.name;
    }

    public String getVersion() {
        return this.version;
    }

    public void setDirectory(File directory) {
        this.directory = directory;
    }
}
