package web;

import domain.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import service.UserProfileService;

import javax.servlet.http.HttpSession;
import java.util.List;

@Controller
@SessionAttributes("user")
public class UserProfileController {


    @Autowired
    UserProfileService userProfileService;

    @RequestMapping(value = "/registration", method = RequestMethod.GET)
    public String getRegistrationUserPage() {
        return "/registration";
    }

    @RequestMapping(value = "/registration", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("userProfile") UserProfile userProfile)  {
                        userProfileService.registerUser(userProfile);

        return "/main";
    }


}
