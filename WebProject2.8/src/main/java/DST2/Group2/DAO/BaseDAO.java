package DST2.Group2.DAO;

import DST2.Group2.Database.DBmethods;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;

@Repository
public abstract class BaseDAO {

    private static final Logger log = LoggerFactory.getLogger(BaseDAO.class);

    public boolean existsById(String id, String tableName) {
        AtomicBoolean exists = new AtomicBoolean(false);
        DBmethods.execSQL(connection -> {
            try {
                PreparedStatement preparedStatement = connection.prepareStatement(String.format("select 1 from %s where id =?", tableName));
                preparedStatement.setString(1, id);
                ResultSet resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    exists.set(true);
                }
            } catch (SQLException e) {
                log.info("", e);
            }

        });
        return exists.get();
    }
}
