package web;

import dbHelp.DBHelp;
import entities.Event;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import service.UserService;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * Created by Костя on 02.02.2017.
 */
@Controller
public class KKUserController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    UserService userService = new UserService();

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String getUserPage(User user, ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        user = new DBHelp().getCurrentUser(); // Получаем Объект текущего пользователя
        logger.info("User = " + user.toString());
        Integer idUser = new DBHelp().getObjID(userService.getCurrentUsername());
        m.addAttribute(user);
        m.addAttribute("allEvents", new DBHelp().getEventList(idUser));
        return "user";
    }
}
