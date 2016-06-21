package pl.themolka.ibot.settings;

import org.jdom2.Element;
import pl.themolka.ibot.IBot;
import pl.themolka.ibot.store.Database;
import pl.themolka.ibot.store.StoreProvider;
import pl.themolka.ibot.xml.XMLException;
import pl.themolka.ibot.xml.XMLReadable;

import java.util.ArrayList;
import java.util.List;

public class DatabaseSettings extends SettingsElement implements XMLReadable {
    private final List<DatabaseConnection> connections = new ArrayList<>();

    public DatabaseSettings(Element element) {
        super(element);
    }

    @Override
    public void read() throws XMLException {
        List<Element> availableConnections = this.getXML().getChildren("connection");
        DatabaseConnection connection;

        if (availableConnections.isEmpty()) {
            IBot.getLogger().error("No connections were found in the settings. Using the default one...");
            connection = new DatabaseConnection(null);
        } else {
            connection = new DatabaseConnection(availableConnections.get(0));
        }

        try {
            connection.read();
            this.connections.add(connection);
        } catch (XMLException ex) {
            IBot.getLogger().trace("Could not read connection " + connection, ex);
        }

        if (availableConnections.size() > 1) {
            IBot.getLogger().warn("Only one database connection can be established (using " + connection.getDatabase().getName() + ")...");
        }
    }

    public List<DatabaseConnection> getConnections() {
        return this.connections;
    }

    public static class DatabaseConnection extends SettingsElement implements XMLReadable {
        private Database database;

        public DatabaseConnection(Element element) {
            super(element);
        }

        @Override
        public void read() throws XMLException {
            String provider = null;
            if (this.getXML() != null) {
                provider = this.getXML().getAttributeValue("provider");
            }

            if (provider == null) {
                provider = StoreProvider.getDefaultProvider().name();
            }

            StoreProvider store = StoreProvider.valueOf(provider.trim().toUpperCase());
            if (store == null) {
                throw new XMLException("The database provider was not found.");
            }

            try {
                this.database = store.createDatabaseObject(this.getXML());
            } catch (Throwable ex) {
                throw new XMLException("Could not create the database object: " + ex.getMessage(), ex);
            }
        }

        public Database getDatabase() {
            return this.database;
        }
    }
}
