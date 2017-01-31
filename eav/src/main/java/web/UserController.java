package web;

import dbHelp.DBHelp;
import entities.User;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import service.UserService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Lawrence on 20.01.2017.
 */
@Controller
public class UserController {

    UserService userService = new UserService();


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
                               @RequestParam("ageUser") String ageDate,
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
        mapAttr.put(5, ageDate);
        mapAttr.put(6, email);
        mapAttr.put(7,bcryptPass);
        mapAttr.put(8, null);
        mapAttr.put(9, null);
        mapAttr.put(10, null);
        mapAttr.put(11, null);
        mapAttr.put(12, null);
        // mapAttr.put(13, null); не нужно, иначе потом пустая ссылка на событие висит, и при добавлении новой задачи она так и остается висеть. Иначе надо будет при добавлении эту обновлять



        new DBHelp().addNewUser(1001, full_name, mapAttr);

        return "/login";
    }


    @RequestMapping(value = "/profile", method = RequestMethod.GET)
    public String getProfileUserPage(User user, ModelMap m) throws SQLException {
        Integer idUser = new DBHelp().getObjID(userService.getCurrentUsername());

        ArrayList<String> userParams = new DBHelp().getObjParamsByObjID(idUser);

        String name = userParams.get(0);
        String surname = userParams.get(1);
        String middleName = userParams.get(2);
        String login = userParams.get(3);
        String ageDate = userParams.get(4);
        String email = userParams.get(5);
        String sex = userParams.get(7);
        String country = userParams.get(8);
        String additional_field = userParams.get(9);

        user.setId(idUser);
        user.setName(name);
        user.setSurname(surname);
        user.setMiddleName(middleName);
        user.setLogin(login);
        user.setAgeDate(ageDate);
        user.setEmail(email);
        user.setSex(sex);
        user.setCountry(country);
        user.setAdditional_field(additional_field);


        m.addAttribute("userParams", user);
        return "/profile";
    }

    @RequestMapping(value = "/changeProfile/{userId}", method = RequestMethod.POST)
    public String changeUser(@PathVariable("userId") Integer userId,
                             @RequestParam("name") String name,
                             @RequestParam("surname") String surname,
                             @RequestParam("middle_name") String middle_name,
                             @RequestParam("nickname") String nickname,
                             @RequestParam("ageDate") String ageDate,
                             @RequestParam("sex") String sex,
                             @RequestParam("country") String country,
                             @RequestParam("info") String additional_field) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        String full_name = name + " " + surname + " " + middle_name;


        TreeMap<Integer, String> mapAttr = new TreeMap<>();
        mapAttr.put(1, name);
        mapAttr.put(2, surname);
        mapAttr.put(3, middle_name);
        mapAttr.put(4, nickname);
        mapAttr.put(5, ageDate);

        mapAttr.put(8, sex);
        mapAttr.put(9, country);
        mapAttr.put(10, additional_field);

        new DBHelp().updateUser(userId, full_name, mapAttr);

        return "/main-login";
    }


    @RequestMapping(value = "/accessDenied", method = RequestMethod.GET)
    public ModelAndView accesssDenied(Principal user) {

        ModelAndView model = new ModelAndView();

        if (user != null) {
            model.addObject("errorMsg", user.getName() + ", у вас нет доступа к этой странице!");
        } else {
            model.addObject("errorMsg", "У вас нет доступа к этой странице!");
        }

        model.setViewName("/accessDenied");
        return model;

    }

}
