package DST2.Group2.Controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller

public class IndexController {
    private static final Logger log = LoggerFactory.getLogger(MatchController.class);

    @RequestMapping(path="/*")
    public String index() {
        log.info("index");
        ModelAndView in=new ModelAndView();
        in.setViewName("index");
        return "index";
    }
}
