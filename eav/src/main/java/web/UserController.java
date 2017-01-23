package web;

import dbHelp.DBHelp;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Lawrence on 20.01.2017.
 */
@Controller
public class UserController {

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
        return "/addUser";
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

        TreeMap<Integer, String> mapAttr = new TreeMap<>();
        mapAttr.put(1, name);
        mapAttr.put(2, surname);
        mapAttr.put(3, middle_name);
        mapAttr.put(4, nickname);
        mapAttr.put(5, ageUser);
        mapAttr.put(6, email);
        mapAttr.put(7,password);


        new DBHelp().addNewUser(1001, full_name, mapAttr);

        return "/test";
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public String loginUser(@RequestParam("nickname") String nickname,
                            @RequestParam("password") String password
                            )
    {
        return "test";
    }

}
