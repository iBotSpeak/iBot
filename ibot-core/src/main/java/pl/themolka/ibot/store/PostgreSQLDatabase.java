package pl.themolka.ibot.store;

public class PostgreSQLDatabase extends CustomDatabase {
    public static final String POSTGRESQL_DRIVER = "org.postgresql.Driver";
    public static final int POSTGRESQL_PORT = 5432;

    private final String host;
    private final int port;
    private final String database;

    public PostgreSQLDatabase(String host, int port, String database, String username, String password) {
        super(POSTGRESQL_DRIVER, "jdbc:postgresql://" + host + ":" + port + "/" + database, username, password);

        this.host = host;
        this.port = port;
        this.database = database;
    }

    @Override
    public Database copy() throws Throwable {
        return new PostgreSQLDatabase(
                this.getHost(),
                this.getPort(),
                this.getDatabaseName(),
                this.getDataSource().getUsername(),
                this.getDataSource().getPassword()
        );
    }

    @Override
    public String getName() {
        return "PostgreSQL";
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
