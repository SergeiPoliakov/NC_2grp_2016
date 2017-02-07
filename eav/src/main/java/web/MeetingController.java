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
 * Created by Костя on 07.02.2017.
 */
@Controller
public class MeetingController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);
    UserService userService = new UserService();

    // Список встреч пользователя
    @RequestMapping(value = "/meetings", method = RequestMethod.GET)
    public String getUserPage(User user, ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {

        user = new DBHelp().getCurrentUser(); // Получаем Объект текущего пользователя
        Integer idUser = new DBHelp().getObjID(userService.getCurrentUsername());
        m.addAttribute(user);
        m.addAttribute("meetings", new DBHelp().getUserMeetingsList(idUser));
        return "meetings";
    }

    // Просмотр встречи
    @RequestMapping(value = "/meeting{meetingID}", method = RequestMethod.GET)
    public String getMeetingPage( ModelMap m, @PathVariable("meetingID") Integer meetingID) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        // Тут нужно подгрузить данные о пользователях, их расписании, а так же информацию о встрече
        ArrayList<User> users = new DBHelp().getUsersAtMeeting("28"); // ID, name, surname, middlename
        m.addAttribute(users); // Добавление пользователей
        m.addAttribute(new DBHelp().getMeeting(meetingID)); // Информация о событии
        //m.addAttribute("allEvents", new DBHelp().getEventList(users));
        return "/meeting";
    }
}
