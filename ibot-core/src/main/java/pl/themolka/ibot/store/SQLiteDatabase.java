package pl.themolka.ibot.store;

import pl.themolka.ibot.IBot;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteDatabase extends Database {
    public static final String FILE_EXTENSION = ".db";
    public static final String SQLITE_DRIVER = "org.sqlite.JDBC";

    private Connection connection;
    private final File file;

    public SQLiteDatabase(File file) {
        super(SQLITE_DRIVER);

        this.file = file;
    }

    @Override
    public void createConnection() {
        IBot.getLogger().info("Accessing the " + this.getName() + " database file at '" + this.getFile().getPath() + "'...");

        try {
            long took = System.currentTimeMillis();
            this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.getFile().getPath());

            String tookString = "took " + (System.currentTimeMillis() - took) / 1000D + " second(s).";
            IBot.getLogger().info("Accessed the database file successfully - " + tookString);
        } catch (SQLException ex) {
            IBot.getLogger().error("Could not access the database file! - " + ex.getMessage());
        }
    }

    @Override
    public Database copy() throws Throwable {
        return new SQLiteDatabase(this.getFile());
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.connection;
    }

    @Override
    public String getName() {
        return "SQLite";
    }

    public File getFile() {
        return this.file;
    }
}
