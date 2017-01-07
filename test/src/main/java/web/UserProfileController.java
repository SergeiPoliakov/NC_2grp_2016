package web;

import domain.UserProfile;
import exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.SessionStatus;
import service.UserProfileService;
import validation.Validation;

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
    public String registerUser(@ModelAttribute("userProfile") UserProfile userProfile) throws CustomException
    {

                        userProfileService.registerUser(userProfile);

        return "/main";
    }



    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginUser(@RequestParam("nickname") String nickname,
                            @RequestParam("password") String password,
                            ModelMap m) throws CustomException
    {
        UserProfile user = userProfileService.loginUser(nickname, password);
        m.addAttribute("user", user);
        return "redirect:/mainLogin";
    }


    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String getUserPage(HttpSession session)
    {
        UserProfile userProfile = (UserProfile) session.getAttribute("user");
        if (userProfile == null) {
            return "/notLogIn";
        }

        return "/profile";
    }


    @RequestMapping(value = "/changeProfile", method = RequestMethod.GET)
    public String getChangeProfileUserPage(ModelMap m, HttpSession session) {
        UserProfile user = (UserProfile) session.getAttribute("user");
        if (user == null) {
            return "/notLogIn";
        } else {
            m.addAttribute("user", user);
            return "/changeProfile";
        }
    }

    @RequestMapping(value = "/changeProfile/{userId}", method = RequestMethod.POST)
    public String changeUser(@PathVariable("userId") Integer userId, @ModelAttribute("userProfile") UserProfile userProfile)
    {

                        userProfile.setId(userId);
                        userProfileService.updateUserProfile(userProfile);


        return "redirect:/profile";
    }


    @RequestMapping(value = "/logout")
    public String logOut(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "/main";
    }


}
