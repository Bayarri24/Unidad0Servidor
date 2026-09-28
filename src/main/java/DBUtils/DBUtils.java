package DBUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DBUtils {

    public static Connection getConnection() throws SQLException {
        final String name = "root";
        final String pswd = "123456";
        final String db_name = "gestion_nomina";
        final String url = "jdbc:mariadb://localhost:3306/" + db_name;

        Connection conn = null;

        conn = DriverManager.getConnection(url, name, pswd);

        return conn;
    }

    public static void close(Connection conn) throws SQLException {
        if (conn != null) {
            conn.close();
        }
    }

    public static void close(Statement st) throws SQLException {
        if (st != null) {
            st.close();
        }
    }

}
