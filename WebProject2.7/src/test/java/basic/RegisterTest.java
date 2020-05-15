package basic;

import DST2.Group2.DAO.UserDAO;
import DST2.Group2.bean.UserBean;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertFalse;

/**
 * @Functionality_Tested
 *  1. Given user bean, search for stored information in the database via DAO.
 *  2. Given user bean, insert information into the database via DAO.
 *
 * @Date 2020/5/12
 * @author DST group 2
 **/

public class RegisterTest extends BaseTest{

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Autowired
    private UserDAO userDAO;

    @Test
    public void searchUserTest_NoUserName(){
        // except exception thrown
        thrown.expect(Exception.class);
        UserBean user = new UserBean("", "test","test@test.com");
        userDAO.searchUser(user);
    }

    @Test
    public void transactUserTest(){
        UserBean user = new UserBean("Test", "test","test@test.com");
        assertFalse(userDAO.searchUser(user)[1]); // assert test use has not been registered.
        userDAO.InsertUser(user);
        userDAO.deleteByName("Test");
    }

    @Test
    public void searchUserTest_OnlyUserName(){
        UserBean user = new UserBean("Test", null,null);
        userDAO.searchUser(user);
    }

}
