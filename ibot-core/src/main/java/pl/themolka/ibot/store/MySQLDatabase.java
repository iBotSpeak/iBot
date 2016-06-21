package pl.themolka.ibot.store;

public class MySQLDatabase extends CustomDatabase {
    public static final String MYSQL_DRIVER = "com.mysql.jdbc.Driver";
    public static final int MYSQL_PORT = 3306;

    private final String host;
    private final int port;
    private final String database;

    public MySQLDatabase(String host, int port, String database, String username, String password) {
        super(MYSQL_DRIVER, "jdbc:mysql://" + host + ":" + port + "/" + database, username, password);

        this.host = host;
        this.port = port;
        this.database = database;
    }

    @Override
    public Database copy() throws Throwable {
        return new MySQLDatabase(
                this.getHost(),
                this.getPort(),
                this.getDatabaseName(),
                this.getDataSource().getUsername(),
                this.getDataSource().getPassword()
        );
    }

    @Override
    public String getName() {
        return "MySQL";
    }

    public String getDatabaseName() {
        return this.database;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }
}
