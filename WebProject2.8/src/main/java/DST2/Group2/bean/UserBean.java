package DST2.Group2.bean;

/**
 * @Description This is the description of class
 * Bean for user account information.
 *
 * @Date 2020/5/16
 * @Author DST group 2
 */
public class UserBean {

    private String username;
    private String password;
    private String email;

    public UserBean(String UN, String PW, String email) {
        this.username = UN;
        this.password = PW;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
