package web;

import domain.UserProfile;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;

@Controller
public class MainController {

    @RequestMapping(value = {"/", "main"})
    public ModelAndView index(HttpSession httpSession) {
        UserProfile client = (UserProfile) httpSession.getAttribute("user");
        if (client == null) {
            return new ModelAndView("main");
        }
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("main-login");
        return modelAndView;
    }


    @RequestMapping(value = "/mainLogin", method = RequestMethod.GET)
    public String getMainLogin() {
        return "main-login";
    }


}
