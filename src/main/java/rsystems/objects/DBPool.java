package rsystems.objects;

import org.mariadb.jdbc.MariaDbPoolDataSource;

import java.sql.SQLException;

public class DBPool {

    public static MariaDbPoolDataSource pool = null;

    public DBPool(String URL, String user, String pass) {

        try {
            pool = new MariaDbPoolDataSource(URL);
            pool.setUser(user);
            pool.setPassword(pass);
            pool.setMaxPoolSize(14);
            pool.setMinPoolSize(4);

            pool.initialize();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    public MariaDbPoolDataSource getPool() {
        return pool;
    }
}
