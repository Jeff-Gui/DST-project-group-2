package DST2.Group2.filter;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
/**
 * @Description This is the description of class
 * The filter is not implemented.
 * TODO implement page-filter according to the login state.
 * @Date 2020/5/16
 * @Author DST group 2
 */
//@WebFilter(urlPatterns = {"/dosingGuideline"})
public class AuthenticationFilter implements Filter {

    public static final String ROLE_VIEW_DOSING_GUIDELINE = "role_view_dosing_guideline";
    public static final String USERNAME = "username";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpSession session = httpServletRequest.getSession();
        Object username = session.getAttribute(USERNAME);
        if (username != null) {
            Object roleViewDosingGuideline = session.getAttribute(ROLE_VIEW_DOSING_GUIDELINE);
            if (roleViewDosingGuideline != null && ((Integer) roleViewDosingGuideline) == 1) {
                chain.doFilter(request, response);
            }
        } else {
            response.setContentType("text/html");
            response.getWriter().write("You are not allowed to view dosing guideline, please <a href='signin'>sign in</a> first.");
        }
    }
}
