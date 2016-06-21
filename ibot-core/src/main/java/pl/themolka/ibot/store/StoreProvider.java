package pl.themolka.ibot.store;

import org.jdom2.Element;

import java.io.File;

public enum StoreProvider {
    CUSTOM {
        @Override
        public Database createDatabaseObject(Element element) throws Exception {
            String driver = this.getData(element, "driver", null);
            String url = this.getData(element, "url", null);
            String username = this.getData(element, "username", null);
            String password = this.getData(element, "password", null);

            return new CustomDatabase(driver, url, username, password);
        }
    },

    MYSQL {
        @Override
        public Database createDatabaseObject(Element element) throws Exception {
            String host = this.getData(element, "host", "127.0.0.1");
            String port = this.getData(element, "port", String.valueOf(MySQLDatabase.MYSQL_PORT));
            String database = this.getData(element, "database", "database");
            String username = this.getData(element, "username", "root");
            String password = this.getData(element, "password", null);

            return new MySQLDatabase(host, Integer.parseInt(port), database, username, password);
        }
    },

    POSTGRESQL {
        @Override
        public Database createDatabaseObject(Element element) throws Exception {
            String host = this.getData(element, "host", "127.0.0.1");
            String port = this.getData(element, "port", String.valueOf(PostgreSQLDatabase.POSTGRESQL_PORT));
            String database = this.getData(element, "database", "database");
            String username = this.getData(element, "username", "root");
            String password = this.getData(element, "password", null);

            return new PostgreSQLDatabase(host, Integer.parseInt(port), database, username, password);
        }
    },

    SQLITE {
        @Override
        public Database createDatabaseObject(Element element) throws Exception {
            File file = new File(this.getData(element, "file", "sqlite" + SQLiteDatabase.FILE_EXTENSION));

            return new SQLiteDatabase(file);
        }
    },
    ;

    public abstract Database createDatabaseObject(Element element) throws Exception;

    String getData(Element from, String name, String def) {
        String data = from.getChildTextTrim(name);
        if (data != null) {
            return data;
        }

        return def;
    }

    public static StoreProvider getDefaultProvider() {
        return SQLITE;
    }
}
