package web;

import dbHelp.DBHelp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;

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

        String[] massAttr = new String[7];
        massAttr[0] = name;
        massAttr[1] = surname;
        massAttr[2] = middle_name;
        massAttr[3] = nickname;
        massAttr[4] = ageUser;
        massAttr[5] = email;
        massAttr[6] = password;

        new DBHelp().addNewUser(1001, full_name, massAttr);

        return "/test";
    }

}
