package DST2.Group2.DAO;

import DST2.Group2.Database.DBmethods;
import DST2.Group2.bean.SampleBean;
import DST2.Group2.bean.UserBean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.regex.Pattern;

@Repository
public class UserDAO {

    private static final Logger log = LoggerFactory.getLogger(UserDAO.class);

    public void InsertUser(UserBean userBean) {

        DBmethods.execSQL(connection -> {
            try {
                String statement = "INSERT INTO Users (username,passwords,email) VALUES (?,?,?);";
                PreparedStatement preparedStatement = connection.prepareStatement(statement);
                preparedStatement.setString(1,userBean.getUsername());
                preparedStatement.setString(2,userBean.getPassword());
                preparedStatement.setString(3,userBean.getEmail());
                preparedStatement.execute();
            } catch (Exception e) {
                // if user exist (PK)
                // if user email exist (should be unique)
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                System.exit(0);
            }
            log.info("registered");
        });
    }

    public void resetUser(UserBean user) {
        DBmethods.execSQL(connection -> {
            try {
                String statement = "update users set username= ?, passwords= ? where users.email= ?;";
                PreparedStatement preparedStatement = connection.prepareStatement(statement);
                preparedStatement.setString(1, user.getUsername());
                preparedStatement.setString(2, user.getPassword());
                preparedStatement.setString(3, user.getEmail());
                preparedStatement.execute();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName() + ": " + e.getMessage());
                System.exit(0);
            }
            log.info("reset");
        });
    }

    public boolean[] searchUser(UserBean user) {
        boolean[] B = new boolean[4];
        B[0] = false;
        B[1] = false;
        B[2] = false;
        B[3] = false;
        DBmethods.execSQL(connection -> {
            try {
                String email = user.getEmail();
                if (!email.equals("registered")) {
                    if (Pattern.matches("^\\w+(?:\\.?[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+$", email)) {
                        System.out.println("search email");
                        String statement1 = "select passwords from users \n" +
                                "where email = ?;";
                        PreparedStatement preparedStatement1 = connection.prepareStatement(statement1);
                        preparedStatement1.setString(1, email);
                        ResultSet rs1 = preparedStatement1.executeQuery();
                        while (rs1.next()) {
                            B[2] = true; //this email account is registered
                        }
                        rs1.close();
                    } else {
                        B[3] = true; //this email address is invalid
                    }
                }
                String statement2 = "SELECT passwords FROM users WHERE username = ?;";
                PreparedStatement preparedStatement = connection.prepareStatement(statement2);
                preparedStatement.setString(1,user.getUsername());
                ResultSet rs2 = preparedStatement.executeQuery();
                while(rs2.next()) {
                    B[0] = true; //this username exists
                    String password = rs2.getString("passwords");
                    if (password.equals(user.getPassword())) {
                        B[1] = true; //this password matches
                    }
                }
                rs2.close();
            } catch (Exception e) {
                e.printStackTrace();
                System.err.println(e.getClass().getName()+": "+e.getMessage());
                System.exit(0);
            }
        });
        log.info("searched");
        return B;
    }

    public void deleteByName(String name){
        DBmethods.execSQL(connection -> {
            try {
                connection.createStatement().execute("DELETE FROM users WHERE username='" + name +"';");
            } catch (SQLException e) {
                e.printStackTrace();
            }});
        log.info("deleted");
    }

    public List<UserBean> findAll() {
        List<UserBean> userBeans = new ArrayList<>();
        DBmethods.execSQL(connection -> {

            try {
                ResultSet resultSet = connection.createStatement().executeQuery("SELECT * FROM users;");
                while (resultSet.next()) {
                    //username,passwords,email
                    String username = resultSet.getString("username");
                    String email = resultSet.getString("email");
                    UserBean userBean = new UserBean(username, null,email);
                    userBeans.add(userBean);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }});

        return userBeans;
    }

}
