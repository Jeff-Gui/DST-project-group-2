package DST2.Group2.Controller;

import DST2.Group2.DAO.UserDAO;
import DST2.Group2.bean.UserBean;
import DST2.Group2.filter.AuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDAO userDAO;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    private String goToRegister(){
        return "register";
    }

    @RequestMapping(value = "/register", method = RequestMethod.POST)
    private String doRegister(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request){
        log.info(username + "\n" + password);
        UserBean user = new UserBean(username, password);
//        System.out.println(searchUser(user));
        if (!userDAO.searchUser(user)[0]) {
            System.out.println("1");
            userDAO.InsertUser(user);
            return "redirect:/signin";
        } else {
            System.out.println("2");
            request.setAttribute("error", "username has already been taken");
            return "register";
        }
    }

    @RequestMapping(value = "/signin",method = RequestMethod.GET) // href uses method GET
    private String dofirstSign(HttpServletRequest request){
        request.setAttribute("error","Please enter your username and password.");
        return "signin";
    }

    @RequestMapping(value = "/signin",method = RequestMethod.POST) // table uses method POST
    private String doSignin(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request){
        UserBean user = new UserBean(username,password);
        boolean b = userDAO.searchUser(user)[1];
        if (b) {
            HttpSession session = request.getSession();
            session.setAttribute(AuthenticationFilter.ROLE_VIEW_DOSING_GUIDELINE, 1);
            session.setAttribute(AuthenticationFilter.USERNAME, username);
            return "index";
        } else {
            request.setAttribute("error", "username or password error");
            return "signin";
        }
    }

    @RequestMapping("/logout")
    private String doLogout(HttpServletRequest request){
        request.getSession().invalidate();
        return "index";
    }

    @RequestMapping("/aboutUs")
    private String showAboutUs(){
        return "about_us";
    }

}
