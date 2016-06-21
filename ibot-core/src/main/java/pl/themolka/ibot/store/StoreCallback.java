package pl.themolka.ibot.store;

import java.sql.ResultSet;
import java.sql.SQLException;

public interface StoreCallback {
    void done(ResultSet result) throws SQLException;
}
