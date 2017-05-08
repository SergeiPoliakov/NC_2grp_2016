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
import service.converter.DateConverter;
import service.id_filters.MeetingFilter;
import service.id_filters.UserFilter;
import service.notifications.NotificationService;
import service.notifications.UsersNotifications;
import service.search.FinderLogic;
import service.search.FinderTagRequest;
import service.search.FinderTagResponse;
import service.search.SearchParser;
import service.statistics.StatisticLogger;
import service.tags.TagNodeTree;
import service.tags.TagTreeManager;

import javax.mail.MessagingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

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
                ArrayList<Integer> deletedMeeting = loadingService.getListIdFilteredAlternative(new MeetingFilter(MeetingFilter.DELETED_MEETING_FOR_USER, dataObjectUser.getName()));

                il.removeAll(deletedMeeting);

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
    public String getMeetingPage( ModelMap m, @PathVariable("meetingID") Integer meetingID) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException, ParseException, CustomException {
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

        ArrayList<Integer> listIds = new ArrayList<>();
        for (User user_id: meetingUsers
             ) {
            listIds.add(user_id.getId());
        }
        m.addAttribute("ids", listIds);


        if (meeting.getOrganizer().getId() == userService.getObjID(userService.getCurrentUsername())) // Страницу запрашивает создатель встречи
            return "/meetingAdmin";
        else if (meeting.getUsers().contains(user)) { // Страницу запрашивает участник встречи
            return "/meetingMember";
        } else {
            throw new CustomException("Вы не можете просмотреть эту встречу, так как не являетесь ее участником. Попроситесь или напишите организатору");
        }

    }

    //Добавление встречи DO
   /* @RequestMapping(value = "/addMeeting", method = RequestMethod.POST)
    public String addMeeting(ModelMap m,
                             @RequestParam("title") String title,
                             @RequestParam("tag") String tag,
                             @RequestParam("date_start") String date_start,
                             @RequestParam("date_end") String date_end,
                             @RequestParam("info") String info) throws InvocationTargetException, SQLException, IllegalAccessException,
            NoSuchMethodException, ParseException {

        int id = userService.generationID(1004);

        StringBuilder worlds = new StringBuilder();

        if (!Objects.equals(tag, "")) {
            ArrayList<String> tags = SearchParser.parse(tag);
            assert tags != null;
            for (String value : tags
                    ) {
                tagNodeTree.insertForMeeting(value, id);
                System.out.println("КИНУЛ ID" + id);
                worlds.append(value).append(" ");
            }
        } else worlds.append("встреча");

        long duration = DateConverter.duration(date_start, date_end);

        Meeting meeting = new Meeting(id, title, date_start, date_end, info, userService.getCurrentUser(), worlds, "", String.valueOf(duration));

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
    }  */

    // Добавить пользователя на встречу DO, 2017-04-11 добавил создание уведомления о добавлении ко стрече для юзеров
    @RequestMapping(value = "/inviteUserAtMeeting{meetingID}", method = RequestMethod.POST)
    public String inviteUserAtMeeting(@RequestParam("userIDs") String userIDs,
                                      @PathVariable("meetingID") Integer meetingID) throws SQLException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException, ExecutionException, ParseException, IOException, MessagingException {

        String[] users = userIDs.split(",");
        Meeting meeting = new Meeting();
        try {
            meeting = new Meeting(doCache.get(meetingID));
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        int idSender = userService.getCurrentUser().getId(); // получаем айди текущего юзера (он создатель встречи и отправитель приглашения на встречу)
        ArrayList<User> userList = meeting.getUsers();   //исправлено




        for (String userID: users) {
            Integer idReceiver = Integer.parseInt(userID); // Приглашаемый юзер (и он же получатель уведомления)
            User user = new User(doCache.get(idReceiver));

            String flagMeeting = "false";
            // проверяем, есть ли текущий пользователь в друзьях, чтобы послать приглашение
            ArrayList<Integer> ilFriend = loadingService.getListIdFilteredAlternative(new UserFilter(UserFilter.ALL_FRIENDS_FOR_USER_WITH_ID, String.valueOf(idReceiver)));
            try {
                Map<Integer, DataObject> map = doCache.getAll(ilFriend);
                ArrayList<DataObject> list = getListDataObject(map);
                for (DataObject dataObjectFriend : list) {
                    User userFriend = converter.ToUser(dataObjectFriend);
                    if (idSender == userFriend.getId()) {
                        flagMeeting = "true";
                    }
                }
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            // если приватность позволяет, то пользователю придет приглашение на встречу
            DataObject dataObjectSettings = doCache.get(user.getSettingsID());
            Settings settings = converter.ToSettings(dataObjectSettings);

            if (("onlyFriend".equals(settings.getPrivateMeetingInvite()) && flagMeeting.equals("true")) || ("any".equals(settings.getPrivateMeetingInvite()))) {
                userList.add(user);  // добавлять нужно, если пользователь примет приглашение на встречу, а не как сейчас. Потом переделаю
                //добавляю дубликат
                meeting.createDuplicate(user.getId());
                // Формируем уведомление
                Notification notification = new Notification("Приглашение на встречу", idSender, idReceiver, Notification.MEETING_INVITE);
                // и прикрепляем его к пользователю (или, если он оффлайн, просто автоматом переносится в базу)
                //NotificationService.sendNotification(notification);


                if ("true".equals(settings.getEmailMeetingInvite())) {
                    userService.fittingEmail("meetingInvite", idSender, idReceiver);
                }
                if ("true".equals(settings.getPhoneMeetingInvite())) {
                    // userService.sendSmS("meetingInvite" ,idSender, idReceiver);  //отправка смс
                }
            }

            DataObject dataObject = meeting.toDataObject();
            loadingService.updateDataObject(dataObject);
            doCache.invalidate(meetingID);



        }
        meeting.setUsers(userList);
        int id = loadingService.updateDataObject(meeting.toDataObject());
        doCache.invalidate(meetingID);

        //

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

            ArrayList<Integer> ids_duplicates = meeting.getDuplicateIDs();

            for (Integer i: ids_duplicates
                    ) {
                DataObject dataObjectDuplicate = doCache.get(i);
                if (dataObjectDuplicate.getReference(141).get(0).equals(user.getId())) {  //если это наш дубликат

                    //удаляем юзера из встречи
                    meeting.getUsers().remove(user);
                    meeting.setOrganizer(meeting.getUsers().get(0)); // следующий участник становится организатором

                    //удаляем ссылку на дубликат из встречи
                    meeting.getDuplicates().remove(dataObjectDuplicate);
                }
            }

            loadingService.updateDataObject(meeting.toDataObject());
            doCache.invalidate(meetingID);

            //удаляем дубликаты
            for (Integer i: ids_duplicates
                    ) {
                DataObject dataObjectDuplicate = doCache.get(i);
                if (dataObjectDuplicate.getReference(141).get(0).equals(user.getId())) {
                    System.out.println("Дубликат встречи удален");
                    loadingService.deleteDataObjectById(dataObjectDuplicate.getId());
                }
            }

        }

        // Логирвоание:
        loggerLog.add(Log.LEAVED_MEETING, meetingID);
        return "redirect:/meetings";
    }

    // Редактирование встречи DO
    @RequestMapping(value = "/updateMeeting{meetingID}", method = RequestMethod.POST)
    public String inviteUserAtMeeting(@PathVariable("meetingID") Integer meetingID,
                                      @RequestParam("title") String title,
                                      @RequestParam("tag") StringBuilder tag,
                                      @RequestParam("date_start") String date_start,
                                      @RequestParam("date_end") String date_end,
                                      @RequestParam("info") String info) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ParseException {

        Meeting meeting = meetingService.getMeeting(meetingID);
        meeting.setTitle(title);
        meeting.setTag(tag);
        meeting.setDate_start(date_start);
        meeting.setDate_end(date_end);
        meeting.setDuration(String.valueOf(DateConverter.duration(date_start, date_end)));
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
            @RequestParam("info") String info) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ParseException, ExecutionException {


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
        meeting.setDuration(String.valueOf(DateConverter.duration(date_start, date_end)));
        meeting.setInfo(info);

        ArrayList<Integer> ids_duplicates = meeting.getDuplicateIDs();
        ArrayList<User> users = meeting.getUsers();


        DataObject dataObject = meeting.toDataObject();
        loadingService.updateDataObject(dataObject);
        doCache.refresh(meetingID);

        for (User user: users
                ) {
            for (Integer i: ids_duplicates
                    ) {
                DataObject dataObjectDuplicate = doCache.get(i);
                if (dataObjectDuplicate.getReference(141).get(0).equals(user.getId())) {
                    loadingService.deleteDataObjectById(dataObjectDuplicate.getId());
                }
            }
        }

        //удаляем ссылки на дубликаты
        for (Integer i: ids_duplicates
                ) {
            DataObject dataObjectDuplicate = doCache.get(i);
            meeting.getDuplicates().remove(dataObjectDuplicate);
            doCache.invalidate(i);
        }

        for (User user: users
                ) {
            meeting.createDuplicate(user.getId());

        }

        dataObject = meeting.toDataObject();
        int id = loadingService.updateDataObject(dataObject);
        doCache.refresh(meetingID);

        // Логирование:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.EDIT_MEETING, id, idUser);
        response.setText(new Gson().toJson(meeting));
        return response;
    }

    //удалить встречу
    @RequestMapping(value = "/deleteMeeting{meetingID}", method = RequestMethod.GET)
    public String deleteMeeting(@PathVariable("meetingID") Integer meetingID) throws InvocationTargetException,
            SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException {

        DataObject currentUser = loadingService.getDataObjectByIdAlternative(userService.getObjID(userService.getCurrentUsername()));
        User user = converter.ToUser(currentUser);
        new DBHelp().setDeletedMeeting(user.getId(), meetingID);


        // Логирование:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.DELETED_MEETING, meetingID, idUser);
        return "redirect:/meetings";
    }

    //закрыть встречу
    @RequestMapping(value = "/closeMeeting{meetingID}", method = RequestMethod.GET)
    public String closeMeeting(@PathVariable("meetingID") Integer meetingID) throws InvocationTargetException,
            SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException {

        DataObject dataObject = doCache.get(meetingID);
        Meeting meeting = new Meeting(dataObject);
        meeting.setStatus("closed");

       //удаляю ссылки на дубликаты из встречи
        ArrayList<Integer> ids_duplicates = meeting.getDuplicateIDs();
        for (Integer i: ids_duplicates
                ) {
            DataObject dataObjectDuplicate = doCache.get(i);
            meeting.getDuplicates().remove(dataObjectDuplicate);
        }

        //обновляю встерче перед непосредственным удалением дубликатов
        int id = loadingService.updateDataObject(meeting.toDataObject());
        doCache.invalidate(meetingID);

        //удаляем дубликаты
        for (Integer i: ids_duplicates
                ) {
            DataObject dataObjectDuplicate = doCache.get(i);
            System.out.println("Дубликат встречи удален");
            loadingService.deleteDataObjectById(dataObjectDuplicate.getId());
        }

        // Логирование:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.CLOSED_MEETING, id, idUser);
        return "redirect:/meetings";
    }
}
