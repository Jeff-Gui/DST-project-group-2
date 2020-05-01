package DST2.Group2.DAO;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.bean.UserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

@Repository
public class UserDAO {

    private static final Logger log = LoggerFactory.getLogger(UserDAO.class);

    public void InsertUser(UserBean userBean) {

        DBmethods.execSQL(connection -> {
            try {
                String statement = "INSERT INTO Users (username,passwords) VALUES (?,?);";
                PreparedStatement preparedStatement = connection.prepareStatement(statement);
                preparedStatement.setString(1,userBean.getUsername());
                preparedStatement.setString(2,userBean.getPassword());
                preparedStatement.execute();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                System.exit(0);
            }
            log.info("registered");
        });
    }

    public boolean[] searchUser(UserBean user) {
        boolean[] B = new boolean[2];
        B[0] = false;
        B[1] = false;
        DBmethods.execSQL(connection -> {
            try {
                String statement = "SELECT passwords FROM Users WHERE username = ?;";
                PreparedStatement preparedStatement = connection.prepareStatement(statement);
                preparedStatement.setString(1,user.getUsername());
                ResultSet rs = preparedStatement.executeQuery();
                while(rs.next()) {
                    B[0] = true;
                    String password = rs.getString("passwords");
                    if (password.equals(user.getPassword())) {
                        B[1] = true;
                    }
                }
                rs.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                System.exit(0);
            }
        });
        log.info("searched");
        return B;
    }

}
