package pl.themolka.ibot.settings;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.util.Copyable;
import pl.themolka.ibot.xml.XMLException;
import pl.themolka.ibot.xml.XMLFile;
import pl.themolka.ibot.xml.XMLReadable;

import java.io.IOException;
import java.io.InputStream;

public class Settings extends XMLFile implements Copyable<Settings>, XMLReadable {
    public static final int XML_LEVEL = 0;

    private int xmlLevel;
    private DatabaseSettings database;
    private InstancesSettings instances;
    private PluginsSettings plugins;
    private QuerySettings query;

    public Settings() {
    }

    public Settings(Document document) {
        this.document = document;
    }

    public Settings(InputStream input) throws IOException, JDOMException {
        super(input);
    }

    @Override
    public Settings copy() throws Throwable {
        return new Settings(this.getDocument().clone());
    }

    @Override
    public void read() throws XMLException {
        Element root = this.getDocument().getRootElement();

        String level = root.getAttributeValue("level");
        if (level == null) {
            throw new XMLException("Could not read XML level.");
        }

        try {
            int xmlLevel = Integer.parseInt(level);
            if (xmlLevel > XML_LEVEL) {
                IBot.getLogger().warn("XML level is newer than the iBot version - some features might not work correctly!");
            } else if (xmlLevel < XML_LEVEL) {
                IBot.getLogger().warn("XML level is older than the iBot version - you should update it to the newest level!");
            } else {
                IBot.getLogger().info("XML level is equal to the iBot version! Perfect!");
            }

            this.xmlLevel = xmlLevel;
        } catch (NumberFormatException ex) {
            throw new XMLException("Could not read XML level.");
        }

        // database
        Element databaseElement = root.getChild("database");
        if (databaseElement == null) {
            throw new XMLException("Could not read database settings.");
        }
        this.database = new DatabaseSettings(databaseElement);
        this.database.read();

        // instances
        Element instancesElement = root.getChild("instances");
        if (instancesElement == null) {
            throw new XMLException("Could not read instances settings.");
        }
        this.instances = new InstancesSettings(instancesElement);
        this.instances.read();

        // plugins
        Element pluginsElement = root.getChild("plugins");
        if (pluginsElement == null) {
            throw new XMLException("Could not read plugins settings.");
        }
        this.plugins = new PluginsSettings(pluginsElement);
        this.plugins.read();

        // query
        Element queryElement = root.getChild("query");
        if (queryElement == null) {
            throw new XMLException("Could not read query settings.");
        }
        this.query = new QuerySettings(queryElement);
        this.query.read();
    }

    public DatabaseSettings getDatabase() {
        return this.database;
    }

    public int getDocumentLevel() {
        return this.xmlLevel;
    }

    public InstancesSettings getInstances() {
        return this.instances;
    }

    public PluginsSettings getPlugins() {
        return this.plugins;
    }

    public QuerySettings getQuery() {
        return this.query;
    }
}
