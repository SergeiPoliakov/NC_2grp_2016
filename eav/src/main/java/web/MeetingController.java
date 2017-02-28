package web;

import dbHelp.DBHelp;
import entities.DataObject;
import entities.Event;
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
import service.LoadingServiceImp;
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
    private LoadingServiceImp loadingService = new LoadingServiceImp();

    // Список встреч пользователя
    @RequestMapping(value = "/meetings", method = RequestMethod.GET)
    public String getUserPage(User user, ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {

        user = userService.getCurrentUser(); // Получаем Объект текущего пользователя
        Integer idUser = userService.getObjID(userService.getCurrentUsername());
        m.addAttribute("meetings", meetingService.getUserMeetingsList(idUser)); // m.addAttribute("meetings", meetingService.getUserMeetingsList(idUser));
        return "meetings";
    }

    // Просмотр встречи DO
    @RequestMapping(value = "/meeting{meetingID}", method = RequestMethod.GET)
    public String getMeetingPage( ModelMap m, @PathVariable("meetingID") Integer meetingID) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        Meeting meeting = new Meeting(new DBHelp().getObjectsByIdAlternative(meetingID));
        // Выпиливаются приглашённые друзья
        ArrayList<User> meetingUsers = meeting.getUsers();
        ArrayList<User> organizerFriends = meeting.getOrganizer().getFriends();
        organizerFriends.removeAll(meetingUsers);
        m.addAttribute("meeting", meeting); // Добавление информации о событии на страницу
        if (meeting.getOrganizer().getId() == userService.getObjID(userService.getCurrentUsername())) // Страницу запрашивает создатель встречи
            return "/meetingAdmin";
        else
            if (meetingService.isMeetingMember(userService.getObjID(userService.getCurrentUsername()), meeting)) // Страницу запрашивает участник встречи
                return "/meetingMember";
        return "/main-login";
    }

    //Добавление встречи DO
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

    // Добавить пользователя на встречу DO
    @RequestMapping(value = "/inviteUserAtMeeting{meetingID}", method = RequestMethod.POST)
    public String inviteUserAtMeeting(@RequestParam("userIDs") String userIDs,
                                      @PathVariable("meetingID") Integer meetingID) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        String[] users = userIDs.split(",");
        Meeting meeting = new Meeting(loadingService.getDataObjectByIdAlternative(meetingID));
        ArrayList<User> userList = new ArrayList<>();
        for (String userID: users) {
            User user = new User(loadingService.getDataObjectByIdAlternative(Integer.parseInt(userID)));
            userList.add(user);
        }
        meeting.setUsers(userList);
        loadingService.updateDataObject(meeting.toDataObject());
        return "redirect:/meeting{meetingID}";
    }

    // Редактирование встречи DO
    @RequestMapping(value = "/updateMeeting{meetingID}", method = RequestMethod.POST)
    public String inviteUserAtMeeting(@PathVariable("meetingID") Integer meetingID,
                                      @RequestParam("title") String title,
                                      @RequestParam("tag") String tag,
                                      @RequestParam("date_start") String date_start,
                                      @RequestParam("date_end") String date_end,
                                      @RequestParam("info") String info) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        Meeting meeting = meetingService.getMeeting(meetingID);
        meeting.setTitle(title);
        meeting.setTag(tag);
        meeting.setDate_start(date_start);
        meeting.setDate_end(date_end);
        meeting.setInfo(info);
        DataObject dataObject = meeting.toDataObject();
        loadingService.updateDataObject(dataObject);

        return "redirect:/meeting{meetingID}";
    }
}
