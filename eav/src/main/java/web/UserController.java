package web;

import dbHelp.DBHelp;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Lawrence on 20.01.2017.
 */
@Controller
public class UserController {


    @RequestMapping(value = {"/", "main"})
    public ModelAndView index() {

            return new ModelAndView("main");
    }


    @RequestMapping(value = "/mainLogin", method = RequestMethod.GET)
    public String getMainLogin() {
        return "main-login";
    }



    @RequestMapping(value = { "/login" }, method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username or password!");
        }

        model.setViewName("login");

        return model;

    }


    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/main?logout";
    }


    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String mainPage() {

        return "main";

    }


    @RequestMapping(value = "/main-login", method = RequestMethod.GET)
    public String mainLoginPage() {

        return "main-login";

    }


    @Deprecated
    @RequestMapping("/allUser")
    public String listObjects(Map<String, Object> map) throws SQLException {
        map.put("allObject", new DBHelp().getObjectsIDbyObjectTypeID(1001));
        return "allUser";
    }


    @RequestMapping("/delete/{objectId}")
    public String deleteObject(@PathVariable("objectId") Integer objectId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        new DBHelp().deleteObject(objectId);
        return "redirect:/allUser";
    }


    @RequestMapping(value = "/addUser", method = RequestMethod.GET)
    public String getRegistrationUserPage() {
        return "addUser";
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("surname") String surname,
                               @RequestParam("middle_name") String middle_name,
                               @RequestParam("nickname") String nickname,
                               @RequestParam("ageUser") String ageUser,
                               @RequestParam("email") String email,
                               @RequestParam("password") String password
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        String full_name = name + " " + surname + " " + middle_name;

        String bcryptPass = new BCryptPasswordEncoder().encode(password);

        TreeMap<Integer, String> mapAttr = new TreeMap<>();
        mapAttr.put(1, name);
        mapAttr.put(2, surname);
        mapAttr.put(3, middle_name);
        mapAttr.put(4, nickname);
        mapAttr.put(5, ageUser);
        mapAttr.put(6, email);
        mapAttr.put(7,bcryptPass);


        new DBHelp().addNewUser(1001, full_name, mapAttr);

        return "main-login";
    }

    @RequestMapping(value = "/accessDenied", method = RequestMethod.GET)
    public ModelAndView accesssDenied(Principal user) {

        ModelAndView model = new ModelAndView();

        if (user != null) {
            model.addObject("errorMsg", user.getName() + " у вас нет доступа к этой странице!");
        } else {
            model.addObject("errorMsg", "У вас нет доступа к этой странице!");
        }

        model.setViewName("/accessDenied");
        return model;

    }

}
