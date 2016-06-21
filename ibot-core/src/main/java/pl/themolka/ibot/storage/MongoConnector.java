package pl.themolka.ibot.storage;

import pl.themolka.ibot.util.Copyable;

public class MongoConnector implements Copyable<MongoConnector> {
    public static final String DEFAULT_HOST = "127.0.0.1";
    public static final int DEFAULT_PORT = 27017;
    public static final String DEFAULT_DATABASE = "development";
    public static final String DEFAULT_USERNAME = "root";
    public static final char[] DEFAULT_PASSWORD = "password".toCharArray();

    private String host = DEFAULT_HOST;
    private int port = DEFAULT_PORT;
    private String database = DEFAULT_DATABASE;
    private String username = DEFAULT_USERNAME;
    private char[] password = DEFAULT_PASSWORD;

    @Override
    public MongoConnector copy() {
        MongoConnector connector = new MongoConnector();
        connector.setHost(this.getHost());
        connector.setPort(this.getPort());
        connector.setDatabase(this.getDatabase());
        connector.setUsername(this.getUsername());
        connector.setPassword(this.getPassword());

        return connector;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUsername() {
        return this.username;
    }

    public char[] getPassword() {
        return this.password;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public void setDatabase(String database) {
        this.database = database;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(char[] password) {
        this.password = password;
    }
}
