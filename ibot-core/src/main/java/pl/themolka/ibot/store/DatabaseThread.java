package pl.themolka.ibot.store;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class DatabaseThread extends Thread {
    private final Database database;
    private final BlockingQueue<QueryQueueElement> queryQueue = new ArrayBlockingQueue<>(1024);

    public DatabaseThread(Database database) {
        super("database");

        this.database = database;
    }

    @Override
    public void run() {
        try {
            while (!this.isInterrupted()) {
                try {
                    QueryQueueElement element = this.queryQueue.take();
                    this.submit(this.getDatabase().getConnection(), element);
                } catch (InterruptedException ignored) {
                } catch (Throwable ex) {
                    ex.printStackTrace();
                }
            }
        } catch (Throwable ex) {

        }
    }

    public boolean addQuery(QueryQueueElement query) {
        try {
            return this.queryQueue.add(query);
        } catch (IllegalStateException ex) {
            return false;
        }
    }

    public Database getDatabase() {
        return this.database;
    }

    public void submit(Connection connection, QueryQueueElement query) throws SQLException {
        try (PreparedStatement statement = connection.prepareStatement(query.getQuery())) {
            Object[] params = query.getParams();
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }

            ResultSet result = statement.executeQuery();
            StoreCallback callback = query.getCallback();

            if (callback != null) {
                callback.done(result);
            }
        }
    }

    public static class QueryQueueElement {
        private final StoreCallback callback;
        private final String query;
        private final Object[] params;

        public QueryQueueElement(StoreCallback callback, String query, Object... params) {
            this.callback = callback;
            this.query = query;
            this.params = params;
        }

        public StoreCallback getCallback() {
            return this.callback;
        }

        public String getQuery() {
            return this.query;
        }

        public Object[] getParams() {
            return this.params;
        }
    }
}
