package web;

import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import dbHelp.DBHelp;
import exception.CustomException;
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
import service.id_filters.NotificationFilter;
import service.search.FinderLogic;
import service.search.FinderTagRequest;
import service.search.FinderTagResponse;
import service.search.SearchParser;
import service.statistics.StatisticLogger;
import service.tags.TagNodeTree;
import service.tags.TagTreeManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by Костя on 07.02.2017.
 */
@Controller
public class MeetingController {
    // Собственный внутренний логгер для контроллера
    private StatisticLogger loggerLog = new StatisticLogger();
    private Converter converter = new Converter();
    private UserServiceImp userService = new UserServiceImp();

    private TagTreeManager tagTreeManager = new TagTreeManager();

    private TagNodeTree tagNodeTree = TagNodeTree.getInstance();

    private Logger logger = LoggerFactory.getLogger(MeetingController.class);

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
    public String notificationTestGet(@PathVariable("recieverID") String recieverID) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ExecutionException, ParseException {

        Integer host_id =  userService.getObjID(userService.getCurrentUsername());
        String currentDate =  LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));


        // 2017-04-04 Тест записи и чтени я из бд уведомления
        Notification notification = new Notification("Уведомление",10003, 10003, "friendRequest", currentDate);
        DataObject dataObject = new Converter().toDO(notification);
        new DBHelp().setDataObjectToDB(dataObject);

        // Получение
        /*
        ArrayList<Integer> al = loadingService.getListIdFilteredAlternative(new NotificationFilter(NotificationFilter.FOR_CURRENT_USER));
        // Для каждого айдишника вытаскиваем уведомление, сразу конвертируем к сущности и засовываем в список сущностей
        ArrayList<Notification> notifications = new ArrayList<>();
        for(Integer id : al){
            // Notification notification2 = converter.ToNotification(loadingService.getDataObjectByIdAlternative(id));
            DataObject notification2 = loadingService.getDataObjectByIdAlternative(id);
            System.out.println("!!!!!!!!!!!!!!!!!!!!!! " + notification2);
        }
        */

        return "/main-login";
    }
    //END TEST


    // Список встреч пользователя
    @RequestMapping(value = "/meetings", method = RequestMethod.GET)
    public String getUserPage(HttpServletRequest request, ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ExecutionException, CustomException {
        HttpSession session = request.getSession();
        if (session.getAttribute("finder") != null) {
            FinderTagRequest finder = (FinderTagRequest) session.getAttribute("finder");
            System.out.println("finder пришел из сессии!!!" + finder.getText());

            if ("meeting".equals(finder.getType())) {
                ArrayList<FinderTagResponse> finderTagResponseList = FinderLogic.getWithLogic(finder);
                Set<Integer> meetingsID = new HashSet<>();
                ArrayList<Integer> meetingsListWithTag;

                assert finderTagResponseList != null;
                for (FinderTagResponse tag : finderTagResponseList
                        ) {

                    String value = tag.getText();
                    meetingsListWithTag = tagTreeManager.getMeetingListWithTag(value);
                    meetingsID.addAll(meetingsListWithTag);
                }

                for (int index : meetingsID
                        ) {
                    System.out.println("ЭТИ ОБЪЕКТЫ СЕЙЧАС БУДУТ ВЫВЕДЕНЫ!!!" + index);
                }

                try {
                    DataObject dataObjectUser = doCache.get(userService.getObjID(userService.getCurrentUsername()));
                    User user = converter.ToUser(dataObjectUser);
                    Map<Integer, DataObject> map = doCache.getAll(meetingsID);
                    ArrayList<DataObject> list = getListDataObject(map);
                    ArrayList<Meeting> meetings = new ArrayList<>(list.size());
                    for (DataObject dataObject : list) {
                        Meeting meeting = new Meeting(dataObject);
                        meetings.add(meeting);
                    }

                    for (Meeting meeting : meetings
                            ) {
                        System.out.println("В ТЕГЕ НАХОДИТСЯ ВСТРЕЧА С ID " + meeting.getId());
                    }

                    m.put("meetings", meetings);
                    m.addAttribute("user", user);

                    session.removeAttribute("finder");

                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                throw new CustomException("Неизвестная ошибка!");
            }
        } else {
            try {
                DataObject dataObjectUser = doCache.get(userService.getObjID(userService.getCurrentUsername()));
                User user = converter.ToUser(dataObjectUser);
                ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new MeetingFilter(MeetingFilter.FOR_CURRENT_USER));
                Map<Integer, DataObject> map = doCache.getAll(il);
                ArrayList<DataObject> list = getListDataObject(map);
                ArrayList<Meeting> meetings = new ArrayList<>(list.size());
                for (DataObject dataObject : list) {
                    Meeting meeting = new Meeting(dataObject);
                    meetings.add(meeting);
                }


                m.addAttribute("meetings", meetings); // m.addAttribute("meetings", meetingService.getUserMeetingsList(idUser));
                m.addAttribute("user", user);

                return "meetings";

            } catch (ExecutionException e) {
                e.printStackTrace();
            }
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
        else if (meeting.getUsers().contains(user)) { // Страницу запрашивает участник встречи
            return "/meetingMember";
        }

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

        int id = userService.generationID(1004);

        StringBuilder worlds = new StringBuilder();

        if (tag != null) {
            ArrayList<String> tags = SearchParser.parse(tag);
            assert tags != null;
            for (String value : tags
                    ) {
                tagNodeTree.insertForMeeting(value, id);
                System.out.println("КИНУЛ ID" + id);
                worlds.append(value).append(" ");
            }
        }

        Meeting meeting = new Meeting(id, title, date_start, date_end, info, userService.getCurrentUser(), worlds, "");

        ArrayList<User> users = new ArrayList<>();
        User user = new User();
        user.setId(meeting.getOrganizer().getId());
        users.add(user);
        meeting.setUsers(users);

        DataObject dataObject = meeting.toDataObject();

        loadingService.setDataObjectToDB(dataObject);
        doCache.invalidate(id);



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
        doCache.invalidate(meetingID);

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
        doCache.invalidate(meetingID);

        // Логирвоание:
        loggerLog.add(Log.SEND_INVITE_MEETING, id);
        return "redirect:/meetings";
    }

    // Редактирование встречи DO
    @RequestMapping(value = "/updateMeeting{meetingID}", method = RequestMethod.POST)
    public String inviteUserAtMeeting(@PathVariable("meetingID") Integer meetingID,
                                      @RequestParam("title") String title,
                                      @RequestParam("tag") StringBuilder tag,
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
        doCache.invalidate(meetingID);

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
            @RequestParam("tag") StringBuilder tag,
            @RequestParam("date_start") String date_start,
            @RequestParam("date_end") String date_end,
            @RequestParam("info") String info) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {


        Response response = new Response();
        logger.info("asdasd");
        Meeting meeting = meetingService.getMeeting(meetingID);

        StringBuilder worlds = new StringBuilder();

        if (tag != null) {
            ArrayList<String> tags = SearchParser.parse(new String(tag));
            assert tags != null;
            for (String value : tags
                    ) {
                tagNodeTree.insertForMeeting(value, meetingID);
                System.out.println("КИНУЛ ID" + meetingID);
                worlds.append(value).append(" ");
            }
        }

        meeting.setTitle(title);
        meeting.setTag(worlds);
        meeting.setDate_start(date_start);
        meeting.setDate_end(date_end);
        meeting.setInfo(info);
        DataObject dataObject = meeting.toDataObject();
        int id = loadingService.updateDataObject(dataObject);
        doCache.invalidate(meetingID);

        // Логирование:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.EDIT_MEETING, id, idUser);
        response.setText(new Gson().toJson(meeting));
        return response;
    }

    @RequestMapping(value = "/deleteMeeting{meetingID}", method = RequestMethod.GET)
    public String deleteMeeting(@PathVariable("meetingID") Integer meetingID) throws InvocationTargetException,
            SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException {

        DataObject dataObject = doCache.get(meetingID);
        loadingService.deleteDataObjectById(dataObject.getId());
        return "redirect:/meetings";
    }
}
