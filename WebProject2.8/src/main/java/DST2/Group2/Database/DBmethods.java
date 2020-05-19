package DST2.Group2.Database;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import DST2.Group2.servlet.AppConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.function.Consumer;
/**
 * @Description This is the description of class
 * Class dedicated for establishing the connection with PostgreSQL database.
 *
 * @Date 2020/5/17
 * @Author DST group 2
 */
public class DBmethods {

    private static final Logger log = LoggerFactory.getLogger(DST2.Group2.Database.DBmethods.class);

    public static Connection getConnection() {
        Connection connection = null;
        AppConfig appConfig = AppConfig.getInstance();
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            log.info("", e);
        }
        try {
            connection = DriverManager.getConnection(appConfig.getJdbcUrl()
                    , appConfig.getJdbcUsername()
                    , appConfig.getJdbcPassword());
        } catch (SQLException e) {
            log.info("", e);
        }
        return connection;
    }

    public static void execSQL(Consumer<Connection> consumer) {
        Connection connection = null;
        try {
            connection = getConnection();
            consumer.accept(connection);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    log.info("", e);
                }
            }
        }
    }
}
