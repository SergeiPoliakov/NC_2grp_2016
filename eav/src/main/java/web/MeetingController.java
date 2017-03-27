package web;

import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import service.converter.Converter;
import entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import service.LoadingServiceImp;
import service.MeetingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.id_filters.MeetingFilter;
import service.statistics.StaticticLogger;

import javax.xml.crypto.Data;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Костя on 07.02.2017.
 */
@Controller
public class MeetingController {
    // Собственный внутренний логгер для контроллера
    private StaticticLogger loggerLog = new StaticticLogger();
    private Converter converter = new Converter();
    private UserServiceImp userService = new UserServiceImp();

    Logger logger = LoggerFactory.getLogger(MeetingController.class);

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();
    private MeetingServiceImp meetingService = MeetingServiceImp.getInstance();
    private LoadingServiceImp loadingService = new LoadingServiceImp();

    public MeetingController() throws IOException {
    }

    private ArrayList<DataObject> getListDataObject(Map<Integer, DataObject> map) {
        ArrayList<DataObject> list = new ArrayList<>();
        for(Map.Entry<Integer, DataObject> e : map.entrySet()) {
            list.add(e.getValue());
        }
        return list;
    }

    // TEST
    @RequestMapping(value = "/notificationSendTo{recieverID}", method = RequestMethod.GET)
    public String notificationTestGet(@PathVariable("recieverID") String recieverID) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ExecutionException {

        logger.info("SD");

        Integer host_id =  userService.getObjID(userService.getCurrentUsername());
        String currentDate =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));


        // Это можно использовать в дальнейшем (наверное)
        //Notification notification = new Notification(host_id.toString(), recieverID,"0","4",currentDate);

        // Добавить для себя, посмотреть чё там как
        Notification notification = new Notification(123321, "wsa", 10003, 10003, 10003, "friendRequest",currentDate);

        DataObject dataObject = notification.toDataObject();
        loadingService.setDataObjectToDB(dataObject);

        /*DataObject dataObject2 = loadingService.getDataObjectByIdAlternative(60009);
        Notification notification2 = new Notification(dataObject2);*/

        return "/main-login";
    }
    //END TEST


    // Список встреч пользователя
    @RequestMapping(value = "/meetings", method = RequestMethod.GET)
    public String getUserPage(User user, ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        try {
            ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new MeetingFilter(MeetingFilter.FOR_CURRENT_USER));
            Map<Integer, DataObject> map = doCache.getAll(il);
            ArrayList<DataObject> list = getListDataObject(map);
            ArrayList<Meeting> meetings = new ArrayList<>(list.size());
            for (DataObject dataObject : list) {
                Meeting meeting = new Meeting(dataObject);
                meetings.add(meeting);
            }
            m.addAttribute("meetings", meetings); // m.addAttribute("meetings", meetingService.getUserMeetingsList(idUser));

        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        /*
        user = userService.getCurrentUser(); // Получаем Объект текущего пользователя
        Integer idUser = userService.getObjID(userService.getCurrentUsername());
        m.addAttribute("meetings", meetingService.getUserMeetingsList(idUser)); // m.addAttribute("meetings", meetingService.getUserMeetingsList(idUser));
        */

        // Логируем:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "meetings", idUser);
        return "meetings";
    }

    // Просмотр встречи DO
    @RequestMapping(value = "/meeting{meetingID}", method = RequestMethod.GET)
    public String getMeetingPage( ModelMap m, @PathVariable("meetingID") Integer meetingID) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException {
        Meeting meeting = new Meeting();
        try {
            meeting = new Meeting(doCache.get(meetingID));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        // Выпиливаются приглашённые друзья
        ArrayList<User> meetingUsers = meeting.getUsers();
        ArrayList<User> organizerFriends = meeting.getOrganizer().getFriends();
        organizerFriends.removeAll(meetingUsers);
        m.addAttribute("meeting", meeting); // Добавление информации о событии на страницу

        // Логируем:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "meeting", idUser);

        DataObject dataObject = doCache.get(userService.getObjID(userService.getCurrentUsername()));
        User user = converter.ToUser(dataObject);

        if (meeting.getOrganizer().getId() == userService.getObjID(userService.getCurrentUsername())) // Страницу запрашивает создатель встречи
            return "/meetingAdmin";
        else if (meeting.getUsers().contains(user)) // Страницу запрашивает участник встречи
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

        ArrayList<User> users = new ArrayList<>();
        User user = new User();
        user.setId(meeting.getOrganizer().getId());
        users.add(user);
        meeting.setUsers(users);

        DataObject dataObject = meeting.toDataObject();
        int id = loadingService.setDataObjectToDB(dataObject);
        doCache.invalidate(dataObject.getId());

        // Логирование:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.ADD_MEETING, id, idUser);
        return "redirect:/meetings";
    }

    // Добавить пользователя на встречу DO
    @RequestMapping(value = "/inviteUserAtMeeting{meetingID}", method = RequestMethod.POST)
    public String inviteUserAtMeeting(@RequestParam("userIDs") String userIDs,
                                      @PathVariable("meetingID") Integer meetingID) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ExecutionException {

        String[] users = userIDs.split(",");
        Meeting meeting = new Meeting();
        try {
            meeting = new Meeting(doCache.get(meetingID));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        ArrayList<User> userList = meeting.getUsers();   //исправлено
        for (String userID: users) {
            User user = new User(doCache.get(Integer.parseInt(userID)));
            userList.add(user);
        }
        meeting.setUsers(userList);
        int id = loadingService.updateDataObject(meeting.toDataObject());
        doCache.refresh(meetingID);

        // Логирвоание:
        loggerLog.add(Log.SEND_INVITE_MEETING, id);
        return "redirect:/meeting{meetingID}";
    }

    // Покинуть встречу
    @RequestMapping(value = "/leaveMeeting{meetingID}", method = RequestMethod.GET)
    public String leaveMeeting( @PathVariable("meetingID") Integer meetingID) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ExecutionException {

        Meeting meeting = new Meeting();
        try {
            meeting = new Meeting(doCache.get(meetingID));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        DataObject dataObject = doCache.get(userService.getObjID(userService.getCurrentUsername()));
        User user = converter.ToUser(dataObject);
        meeting.getUsers().remove(user);
        if (meeting.getOrganizer().equals(user)) { // покидает организатор встречи
            meeting.setOrganizer(meeting.getUsers().get(0)); // следующий участник становится организатором
        }

        int id = loadingService.updateDataObject(meeting.toDataObject());
        doCache.refresh(meetingID);

        // Логирвоание:
        loggerLog.add(Log.SEND_INVITE_MEETING, id);
        return "redirect:/meetings";
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
        int id = loadingService.updateDataObject(dataObject);
        doCache.refresh(meetingID);

        // Логирование:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.EDIT_MEETING, id, idUser);
        return "redirect:/meeting{meetingID}";
    }

    // Редактирование встречи Ajax
    @RequestMapping(value = "/updateMeetingAJAX{meetingID}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Response inviteUserAtMeetingWithAJAX(
                                      @PathVariable("meetingID") Integer meetingID,
                                      @RequestParam("title") String title,
                                      @RequestParam("tag") String tag,
                                      @RequestParam("date_start") String date_start,
                                      @RequestParam("date_end") String date_end,
                                      @RequestParam("info") String info) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        Response response = new Response();
        logger.info("asdasd");
        Meeting meeting = meetingService.getMeeting(meetingID);
        meeting.setTitle(title);
        meeting.setTag(tag);
        meeting.setDate_start(date_start);
        meeting.setDate_end(date_end);
        meeting.setInfo(info);
        DataObject dataObject = meeting.toDataObject();
        int id = loadingService.updateDataObject(dataObject);
        doCache.refresh(meetingID);

        // Логирование:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.EDIT_MEETING, id, idUser);
        response.setText(new Gson().toJson(meeting));
        return response;
    }
}
