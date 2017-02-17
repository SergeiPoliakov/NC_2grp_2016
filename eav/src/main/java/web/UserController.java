package web;

import entities.DataObject;
import exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import service.*;
import service.filters.EventFilter;
import service.filters.UserFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Lawrence on 20.01.2017.
 */
@Controller
public class UserController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private LoadingServiceImp loadingService = new LoadingServiceImp();

    private UserServiceImp userService = UserServiceImp.getInstance();

    private EventServiceImp eventService = EventServiceImp.getInstance();

    @RequestMapping(value = {"/", "main"})
    public ModelAndView index() {

        return new ModelAndView("main");
    }


    @RequestMapping(value = "/main-login", method = RequestMethod.GET)
    public String getUserPage(ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        DataObject dataObject = loadingService.getDataObjectByIdAlternative(userService.getObjID(userService.getCurrentUsername())); // Получаем Объект текущего пользователя
        Integer idUser = userService.getObjID(userService.getCurrentUsername());
        m.addAttribute(dataObject);
        ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_USER_WITH_ID, String.valueOf(idUser)));
        m.addAttribute("allEvents", loadingService.getListDataObjectByListIdAlternative(il));
        return "main-login";
    }



    @RequestMapping(value = { "/login" }, method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username or password!");
        }

        model.setViewName("main");

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

    @RequestMapping(value = "/searchUser", method = RequestMethod.POST)
    public String searchUser(@RequestParam("name") String name, Map<String, Object> map) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new UserFilter(UserFilter.SEARCH_USER, name));
        map.put("allObject", loadingService.getListDataObjectByListIdAlternative(il));
        return "/searchUser";
    }

/*
    // 2017-02-14 Анатолий, Проба работы фильтров и альтернативного лоадера
    @RequestMapping("/allUser")
    public String listObjects(Map<String, Object> map) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        map.put("allObject", loadingService.getListDataObjectFiltered(Filter.OBJECT_TYPE, Filter.OBJECT_TYPE_USER)); // loadingService.getListDataObjectFiltered(Filter.OBJECT_TYPE, Filter.OBJECT_TYPE_USER));
        // map.put("allObject",userService.getUserList());
        return "allUser";
    }
*/

    // 2017-02-16 Анатолий, Проба работы новых фильтров и альтернативного лоадера
    @RequestMapping("/allUser")
    public String listObjects(Map<String, Object> map) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        //map.put("allObject", loadingService.getListDataObjectFiltered(Filter.OBJECT_TYPE, Filter.OBJECT_TYPE_USER)); // loadingService.getListDataObjectFiltered(Filter.OBJECT_TYPE, Filter.OBJECT_TYPE_USER));
        // map.put("allObject",userService.getUserList());
        ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new UserFilter(UserFilter.ALL));
        map.put("allObject", loadingService.getListDataObjectByListIdAlternative(il));
        return "allUser";
    }

    @RequestMapping("/delete/{objectId}")
    public String deleteObject(@PathVariable("objectId") Integer objectId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        userService.deleteObject(objectId);
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
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, CustomException {

        String full_name = name + " " + surname + " " + middle_name;

        String bcryptPass = new BCryptPasswordEncoder().encode(password);

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();
        mapAttr.put(1, name);
        mapAttr.put(2, surname);
        mapAttr.put(3, middle_name);
        mapAttr.put(4, nickname);
        mapAttr.put(5, ageDate);
        mapAttr.put(6, email);
        mapAttr.put(7, bcryptPass);
        mapAttr.put(8, null);
        mapAttr.put(9, null);
        mapAttr.put(10, null);
        mapAttr.put(11, null);
        // mapAttr.put(12, null);
        // mapAttr.put(13, null); не нужно, иначе потом пустая ссылка на событие висит, и при добавлении новой задачи она так и остается висеть. Иначе надо будет при добавлении эту обновлять


        DataObject dataObject = loadingService.createDataObject(full_name, 1001, mapAttr);

        userService.setNewUser(dataObject);

        //userService.setNewUser(1001, full_name, mapAttr);

        return "/main";
    }

    // Выводим данные о пользователе на форму редактирования профиля
    @RequestMapping("/profile")
    public String getProfileUserPage(ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {

        DataObject dataObject = loadingService.getDataObjectByIdAlternative(userService.getObjID(userService.getCurrentUsername()));
        // DataObject dataObject = loadingService.getDataObjectById(userService.getObjID(userService.getCurrentUsername()));
        m.addAttribute(dataObject);
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
                             @RequestParam("city") String city,
                             @RequestParam("info") String additional_field) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        String full_name = name + " " + surname + " " + middle_name;

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();
        mapAttr.put(1, name);
        mapAttr.put(2, surname);
        mapAttr.put(3, middle_name);
        mapAttr.put(4, nickname);
        mapAttr.put(5, ageDate);

        mapAttr.put(8, sex);
        mapAttr.put(9, city);
        mapAttr.put(10, additional_field);

        DataObject dataObject = new DataObject(userId, full_name, 1001, mapAttr);

        userService.updateUser(dataObject);

        return "redirect:/main-login";
    }

    @RequestMapping("/allFriends")
    public String friendList(Map<String, Object> map) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Integer idUser = userService.getObjID(userService.getCurrentUsername());
        ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new UserFilter(UserFilter.ALL_FRIENDS_FOR_USER_WITH_ID, String.valueOf(idUser)));
        map.put("allObject", loadingService.getListDataObjectByListIdAlternative(il)); //new DBHelp().getUserList()); //
        return "allFriends";
    }

    // Добавление пользователя в друзья (по его ID)
    @RequestMapping("/addFriend/{objectId}")
    public String addFriend(@PathVariable("objectId") Integer objectId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        userService.setFriend(objectId);
        return "/addFriend";
    }

    // Удаление пользователя из друзей (по его ID)
    @RequestMapping("/deleteFriend/{objectId}")
    public String deleteFriend(@PathVariable("objectId") Integer objectId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        userService.deleteFriend(objectId);
        return "/deleteFriend";
    }


    @RequestMapping(value = "/viewProfile/{id}")
    public String viewUser(@PathVariable("id") int userId,
                           ModelMap m) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        DataObject user = loadingService.getDataObjectByIdAlternative(userId);
        m.addAttribute(user);
        ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_USER_WITH_ID, String.valueOf(userId)));
        m.addAttribute("allObject", loadingService.getListDataObjectByListIdAlternative(il));
        return "/viewProfile";
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

    @RequestMapping(value = "/meeting", method = RequestMethod.GET)
    public String getMeeting() throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        return "meeting";
    }


}
