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
        if (Validation.isValidName(userProfile.getName())) {
            if (Validation.isValidName(userProfile.getSurname())) {
                if (Validation.isValidEmail(userProfile.getEmail())) {
                    if (Validation.isValidCity(userProfile.getCity())) {
                        userProfileService.registerUser(userProfile);
                    }
                }
            }
        }

        return "/main";
    }



    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String getLoginPage() {
        return "/login";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginUser(@RequestParam("email") String email,
                            @RequestParam("password") String password,
                            ModelMap m) throws CustomException
    {
        UserProfile user = userProfileService.loginUser(email, password);
        m.addAttribute("user", user);
        return "redirect:/mainLogin";
    }

    @RequestMapping(value = "/logout")
    public String logOut(SessionStatus sessionStatus) {
        sessionStatus.setComplete();
        return "/main";
    }


}
