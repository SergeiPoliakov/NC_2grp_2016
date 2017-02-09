package web;

import dbHelp.DBHelp;
import entities.Meeting;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import service.MeetingServiceImp;
import service.UserServiceImp;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Костя on 07.02.2017.
 */
@Controller
public class MeetingController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private UserServiceImp userService = UserServiceImp.getInstance();

    private MeetingServiceImp meetingService = MeetingServiceImp.getInstance();

    // Список встреч пользователя
    @RequestMapping(value = "/meetings", method = RequestMethod.GET)
    public String getUserPage(User user, ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {

        user = userService.getCurrentUser(); // Получаем Объект текущего пользователя
        Integer idUser = userService.getObjID(userService.getCurrentUsername());
        m.addAttribute("meetings", meetingService.getUserMeetingsList(idUser)); // m.addAttribute("meetings", meetingService.getUserMeetingsList(idUser));
        return "meetings";
    }

    // Просмотр встречи
    @RequestMapping(value = "/meeting{meetingID}", method = RequestMethod.GET)
    public String getMeetingPage( ModelMap m, @PathVariable("meetingID") Integer meetingID) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        Meeting meeting = meetingService.getMeetingWithUsers(meetingID);
        m.addAttribute("meeting", meeting); // Добавление информации о событии на страницу

        return "/meeting";
    }

    //Добавление встречи
    @RequestMapping(value = "/addMeeting", method = RequestMethod.POST)
    public String addMeeting(ModelMap m,
                             @RequestParam("title") String title,
                             @RequestParam("tag") String tag,
                             @RequestParam("date_start") String date_start,
                             @RequestParam("date_end") String date_end,
                             @RequestParam("info") String info) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        Meeting meeting = new Meeting(title, date_start, date_end, info, userService.getCurrentUser(), tag, "");
        meetingService.setMeeting(meeting);

        return "redirect:/meetings";
    }
}
