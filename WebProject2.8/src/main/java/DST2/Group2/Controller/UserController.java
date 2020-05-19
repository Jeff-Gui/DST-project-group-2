package DST2.Group2.Controller;

import DST2.Group2.DAO.UserDAO;
import DST2.Group2.bean.UserBean;
import DST2.Group2.filter.AuthenticationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
/**
 * @Description This is the description of class
 * Controller for handling user account information
 * Specific actions include: USECASE_USER: registration, login; USECASE_ADMINISTRATOR: reset user account
 * Some less-related URL handling is also included in this class TODO separate less-related request mapping into a single controller.
 * @Date 2020/5/15
 * @Author DST group 2
 */
@Controller
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);

    @Autowired
    private UserDAO userDAO;

    @RequestMapping(value = "/register", method = RequestMethod.GET)
    private String goToRegister(){
        return "register";
    }

    /**
     * @Description
     * According the given registration, determine if it is valid (e.g. duplicate name, email)
     * @Param username, password, email, request
     * @Return jsp
     * @Date 2020/5/16
     * @author DST group 2
    **/
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    private String doRegister(@RequestParam("username") String username, @RequestParam("password") String password, @RequestParam("email") String email, HttpServletRequest request){
        log.info(username + "\n" + password);
        UserBean user = new UserBean(username, password,email);
        boolean[] B = userDAO.searchUser(user);
        if (B[3]) {
            System.out.println("Invalid email");
            request.setAttribute("error", "invalid email address");
            return "register";
        } else if (B[2]) {
            System.out.println("duplicated email");
            request.setAttribute("error", "this email has been registered");
            return "register";
        }else if (B[0]){
            System.out.println("duplicated username");
            request.setAttribute("error", "username has already been taken");
            return "register";
        } else {
            System.out.println("Start register");
            userDAO.InsertUser(user);
            return "redirect:/signin";
        }
    }

    @RequestMapping(value = "/reset", method = RequestMethod.GET)
    private String goToReset(){
        return "reset_password";
    }

    /**
     * @Description
     * For administrator to reset user account information.
     * @Param email, username, request
     * @Return jsp
     * @Date 2020/5/16
     * @author DST group 2
    **/
    @RequestMapping(value = "/reset", method = RequestMethod.POST)
    private String doReset(@RequestParam("email") String email, @RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request){
        log.info(email + "\n" + username + "\n" + password);
        UserBean Reset = new UserBean(username, password, email);
        if (userDAO.searchUser(Reset)[2]) {
            System.out.println("reset");
            userDAO.resetUser(Reset);
            HttpSession session = request.getSession();
            session.setAttribute("ResetEmail",email);
            session.setAttribute("ResetUsername",username);
            session.setAttribute("ResetPassword",password);
            return "reset_successful";
        } else {
            request.setAttribute("error", "Invalid email");
            return "reset_password";
        }
    }

    @RequestMapping(value = "/signin",method = RequestMethod.GET) // href uses method GET
    private String dofirstSign(HttpServletRequest request){
        request.setAttribute("error","Please enter your username and password.");
        return "signin";
    }

    /**
     * @Description
     * According to provided sign in information, check whether the password or acoount is validate.
     * @Param username, password, request
     * @Return jsp
     * @Date 2020/5/16
     * @author DST group 2
    **/
    @RequestMapping(value = "/signin",method = RequestMethod.POST) // table uses method POST
    private String doSignin(@RequestParam("username") String username, @RequestParam("password") String password, HttpServletRequest request){
        UserBean user = new UserBean(username,password,"registered");
        if (userDAO.searchUser(user)[1]) {
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

    @RequestMapping("/dashboard")
    private String showDashboard(){
        return "index";
    }

    @RequestMapping("/forgetPassword")
    private String showForgetPassword(){return "forget_password";}

}
