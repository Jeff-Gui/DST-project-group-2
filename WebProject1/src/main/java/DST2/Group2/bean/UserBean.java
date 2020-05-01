package DST2.Group2.bean;

public class UserBean {

    public String username;
    public String password;

    public UserBean(String UN, String PW) {
        this.username = UN;
        this.password = PW;
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
}
