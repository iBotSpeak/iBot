package pl.themolka.ibot.store;

import pl.themolka.ibot.IBot;
import pl.themolka.ibot.util.Copyable;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class Database implements Copyable<Database> {
    private final String driver;
    private final DatabaseThread thread;

    public Database(String driver) {
        this.driver = driver;
        this.thread = new DatabaseThread(this);
    }

    public void closeConnection() {
        try {
            this.getConnection().close();
        } catch (SQLException ex) {
            IBot.getLogger().trace("Could not abort the database connection - " + ex.getMessage(), ex);
        }
    }

    public abstract void createConnection();

    public abstract Connection getConnection() throws SQLException;

    public String getDriver() {
        return this.driver;
    }

    public String getName() {
        return "Unknown";
    }

    public DatabaseThread getThread() {
        return this.thread;
    }

    public void query(String query, Object... params) {
        this.query(null, query, params);
    }

    public void query(StoreCallback callback, String query, Object... params) {
        this.getThread().addQuery(new DatabaseThread.QueryQueueElement(callback, query, params));
    }
}
