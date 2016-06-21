package pl.themolka.ibot.store;

import org.apache.commons.dbcp2.BasicDataSource;
import pl.themolka.ibot.IBot;

import java.sql.Connection;
import java.sql.SQLException;

public class CustomDatabase extends Database {
    private final BasicDataSource dataSource = new BasicDataSource();

    public CustomDatabase(String driver, String url, String username, String password) {
        super(driver);

        this.dataSource.setDriverClassName(this.getDriver());
        this.dataSource.setUrl(url);

        if (username != null) {
            this.dataSource.setUsername(username);

            if (password != null) {
                this.dataSource.setPassword(password);
            }
        }
    }

    @Override
    public void closeConnection() {
        try {
            this.dataSource.close();
        } catch (SQLException ex) {
            IBot.getLogger().trace("Could not abort the database connection - " + ex.getMessage(), ex);
        }
    }

    @Override
    public Database copy() throws Throwable {
        return new CustomDatabase(
                this.getDriver(),
                this.getDataSource().getUrl(),
                this.getDataSource().getUsername(),
                this.getDataSource().getPassword()
        );
    }

    @Override
    public void createConnection() {
        IBot.getLogger().info("Connecting to the " + this.getName() + " database with '" + this.getDataSource().getUrl() + "'...");

        try {
            long took = System.currentTimeMillis();
            this.getConnection();

            String tookString = "took " + (System.currentTimeMillis() - took) / 1000D + " second(s).";
            IBot.getLogger().info("Connected to the database - " + tookString);
        } catch (SQLException ex) {
            IBot.getLogger().error("Could not connect to the database! - " + ex.getMessage());
        }
    }

    @Override
    public Connection getConnection() throws SQLException {
        return this.dataSource.getConnection();
    }

    @Override
    public String getName() {
        return this.getDataSource().getDriverClassName();
    }

    public BasicDataSource getDataSource() {
        return this.dataSource;
    }
}
