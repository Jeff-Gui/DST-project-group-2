package web;

import DST2.Group2.DAO.UserDAO;
import DST2.Group2.bean.UserBean;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.Assert.assertFalse;

public class UserControllerTest extends BaseWebTest {
    @Autowired
    UserDAO userDAO;

    @Before
    public void setUp(){
        // setup mock account
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        UserBean userBean = new UserBean("Test","test","test@test.com"); // will be tested as correct account
        UserBean userBean2 = new UserBean("Test2","test","test@test.com"); // will be tested as wrong username or register
        assertFalse(userDAO.searchUser(userBean)[0]);
        assertFalse(userDAO.searchUser(userBean2)[0]);
        userDAO.InsertUser(userBean);
    }

    @Test
    public void normalSignIn() throws Exception {
        String url = "/signin";
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,url).
                param("username","Test").
                param("password","test"));
        resultActions.andDo(MockMvcResultHandlers.print()).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    public void SignIn_WrongPw() throws Exception{
        String url = "/signin";
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,url).
                param("username","Test").
                param("password",""));
        resultActions.andDo(MockMvcResultHandlers.print()).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    public void SignIn_WrongName() throws Exception{
        String url = "/signin";
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,url).
                param("username","Test2").
                param("password","bla"));
        resultActions.andDo(MockMvcResultHandlers.print()).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
    }

    @Test
    public void Register() throws Exception{
        String url = "/signin";
        ResultActions resultActions = mockMvc.perform(MockMvcRequestBuilders.request(HttpMethod.GET,url).
                param("username","Test2").
                param("password","test").
                param("email","test@test.com"));
        resultActions.andDo(MockMvcResultHandlers.print()).
                andExpect(ResultMatcher.matchAll()).
                andExpect(MockMvcResultMatchers.status().isOk()).andReturn();
        userDAO.deleteByName("Test2");
    }

    @After
    public void clear(){
        userDAO.deleteByName("Test");
    }

}
