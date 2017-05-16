package web;

import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import entities.*;
import exception.CustomException;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.converter.Converter;
import service.converter.DateConverter;
import service.id_filters.UserFilter;
import service.meetings.NewMeetingManager;
import service.meetings.NewMeetingRequest;
import service.meetings.NewMeetingResponce;
import service.notifications.NotificationService;
import service.optimizer.SlotManager;
import service.optimizer.SlotRequest;
import service.statistics.StatisticLogger;

import javax.mail.MessagingException;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 09.04.2017.
 */
// Класс-контроллер для работы со встречами (для новых встреч с плавающими границами и пр)
@Controller
public class NewMeetingController {

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();
    private UserServiceImp userService = new UserServiceImp();
    private Converter converter = new Converter();
    private LoadingServiceImp loadingService = new LoadingServiceImp();
    private StatisticLogger loggerLog = new StatisticLogger();

    public NewMeetingController() throws IOException {
    }

    private ArrayList<DataObject> getListDataObject(Map<Integer, DataObject> map) {
        ArrayList<DataObject> list = new ArrayList<>();
        for(Map.Entry<Integer, DataObject> e : map.entrySet()) {
            list.add(e.getValue());
        }
        return list;
    }

    // 1) На подгрузку страницы добавления новой встречи:
    @RequestMapping(value = "/newMeeting", method = RequestMethod.GET)
    public String newMeetingPage() throws SQLException {
        return "newMeeting";
    }

    // 2) К запросу на формирование новой встречи
    @RequestMapping(value = "/addNewMeeting", method = RequestMethod.POST)
    public String addNewMeeting(@RequestParam("title") String title,
                                @RequestParam("date_start") String date_start,
                                @RequestParam("date_end") String date_end,
                                @RequestParam("info") String info,
                                @RequestParam("tag") String tag) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {
        Integer id = new NewMeetingManager().setNewMeeting(title, date_start, date_end, null, info, tag, null);
        // И надо организатору добавить копию встречи - событие в расписание

        // добавляю в NewMeetingManager при создании, чтобы не обращаться лишний раз к базе


        return "redirect:/meetings";
    }

    // 3) К запросу на формирование новой встречи с плавающими границами
    @RequestMapping(value = "/addNewFloatingMeeting", method = RequestMethod.POST)
    public String addNewFloatingMeeting(@RequestParam("title") String title,
                                        @RequestParam("date_start") String date_start,
                                        @RequestParam("date_end") String date_end,
                                        @RequestParam("date_edit") String date_edit,
                                        @RequestParam("info") String info,
                                        @RequestParam("tag") String tag,
                                        @RequestParam("duration") String duration) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {
        Integer id = new NewMeetingManager().setNewMeeting(title, date_start, date_end, date_edit, info, tag, duration);
        // И надо организатору добавить копию встречи - событие в расписание // 2017-04-15 А, может, и не надо))

        // добавляю в NewMeetingManager при создании, чтобы не обращаться лишний раз к базе


        return "redirect:/meetings";
    }


    // 4) Обработка нажатия кнопки Согласиться на встречу в окне уведомления приглашения на встречу НАДО ТУТ ДОДЕЛАТЬ!!!
    @RequestMapping(value = "/acceptNewMeeting", method = RequestMethod.POST)
    public String acceptNewMeeting(@RequestParam("meeting_id") String meeting_id) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {
        // Нужно удалить уведомление у текущего пользователя (из памяти и из базы),
        // ... пока пропустил этот момент

        // затем создать уведомление для администратора (а, возможно, и для всех участников встречи) о том, что данный пользователь присоединился к встрече,

        Integer idMeeting = Integer.parseInt(meeting_id); // Айди текущей встречи
        Integer idSender = userService.getCurrentUser().getId(); // получаем айди текущего юзера (нового участника встречи)
        String userName = userService.getCurrentUsername(); // Получаем имя текущего юзера
        Meeting meeting = new Meeting(doCache.get(idMeeting));

        ArrayList<User> userList = meeting.getMemberUsers(); // Обходим всех участников встречи, кроме текущего юзера, и отправляем им уведомление о том, что данный пользователь присоединился к встрече:


        for (User user : userList) {
            Integer idReceiver = user.getId(); // получатель уведомления
            if (!idSender.equals(idReceiver)) { // всем, кроме текущего юзера

                // Формируем уведомление
                Notification notification = new Notification("Пользователь " + userName + " присоединился к встрече " + meeting.getTitle(), idSender, idReceiver, Notification.MEETING_ACCEPT);
                // и прикрепляем его к пользователю (или, если он оффлайн, просто автоматом переносится в базу)
                //NotificationService.sendNotification(notification);
            }
        }


        // а затем подписать юзера на встречу (но хотя сейчас сделано так, что он уже подписан) и сделать копию встречи как отображение новой задачей в базу пользователю и посмотреть, нет ли пересечений с расписанием, и если есть, вывести новое уведомление
        meeting.createDuplicate(idSender);

        // И заодно проверяем, не перекрывается ли его расписание новой встречей
        ArrayList<Event> overlapEvents = new SlotManager().getOverlapEvents(meeting);
        if (overlapEvents != null){
            // Если перекрывается, выводим уведомление пользователю:
            // Формируем уведомление // Тут бы в качетсве отправителя выставить систе юзера, а его еще надо создать!!
            Notification notification = new Notification("Встреча " + meeting.getTitle() + " совпадает по времени с задачами в Вашем расписании. Нажмите \"Продолжить\" для оптимизации", idSender, idSender, Notification.MEETING_OVERLAP);
            // и прикрепляем его к пользователю (или, если он оффлайн, просто автоматом переносится в базу)
            //NotificationService.sendNotification(notification);
        }

        return "redirect:/meetings";
    }


    // 5) Обработка нажатия кнопки Отказаться от встречи в окне уведомления приглашения на встречу НАДО ДОДЕЛАТЬ!
    @RequestMapping(value = "/refuseNewMeeting", method = RequestMethod.POST)
    public String refuseNewMeeting(@RequestParam("meeting_id") Integer meeting_id) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {
        // Нужно удалить уведомление у текущего пользователя (из памяти и из базы),
        // затем создать уведомление для администратора о том, что данный пользователь отказался принять участие в встрече,

        Meeting meeting = new Meeting(doCache.get(meeting_id));
        User user = userService.getCurrentUser(); // получаем текущего юзера (покидающего встречу)

        meeting.addExitedUsers(user); // Переносим в группу ппокинувших встречу
        meeting.deleteDuplicate(user.getId()); // И удаляем дубликат

        loadingService.updateDataObject(meeting.toDataObject());
        doCache.invalidate(meeting_id);

        // тут, может, еще что-то нужно?? Удалить из кеша дубликат??

        return "redirect:/meetings";
    }

    @RequestMapping(value = "/checkMeetingAJAX", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Response checkMeetingAJAX(
            @RequestParam("date_start") String date_start,
            @RequestParam("date_end") String date_end,
            @RequestParam("duration") String duration) throws ParseException {

        System.out.println(date_start);
        System.out.println(date_end);
        System.out.println(duration);

        Response response = new Response();
        boolean check = true;
        long durationTime = DateConverter.duration(date_start, date_end);
        if (durationTime / Long.parseLong(duration) >= 2) {
            System.out.println("Слишком большие границы!");
            check = false;
        }
        response.setText(new Gson().toJson(check));
        return response;
    }

    @RequestMapping(value = "/checkPrivacyMeetingForNotification", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Response checkMeetingAJAX(
            @RequestParam("senderID") Integer senderID,
            @RequestParam("recieverID") Integer recieverID,
            @RequestParam("additionalID") Integer additionalID
    ) throws ParseException, ExecutionException, CustomException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, IOException, MessagingException {

        System.out.println("Отправитель " + senderID);
        System.out.println("Получатель" + recieverID);

        boolean check = true;

        String flagMeeting = "false";
        // проверяем, есть ли текущий пользователь в друзьях, чтобы послать приглашение
        ArrayList<Integer> ilFriend = loadingService.getListIdFilteredAlternative(new UserFilter(UserFilter.ALL_FRIENDS_FOR_USER_WITH_ID, String.valueOf(recieverID)));
        try {
            Map<Integer, DataObject> map = doCache.getAll(ilFriend);
            ArrayList<DataObject> list = getListDataObject(map);
            for (DataObject dataObjectFriend : list) {
                User userFriend = converter.ToUser(dataObjectFriend);
                if (Objects.equals(senderID, userFriend.getId())) {
                    flagMeeting = "true";
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        Response response = new Response();

        DataObject dataObjectTo = doCache.get(recieverID);
        User user = converter.ToUser(dataObjectTo);
        Settings settings = converter.ToSettings(doCache.get(user.getSettingsID()));

        if (("onlyFriend".equals(settings.getPrivateMeetingInvite()) && flagMeeting.equals("true")) || ("any".equals(settings.getPrivateMeetingInvite()))) {

            if ("true".equals(settings.getEmailMeetingInvite())) {
                userService.fittingEmail("meetingInvite", senderID, recieverID);
            }
            if ("true".equals(settings.getPhoneMeetingInvite())) {
                // userService.sendSmS("meetingInvite" ,senderID, recieverID);  //отправка смс
            }
        } else {
            check = false;
        }

        if (check) {
            Meeting meeting = null;
            try {
                meeting = new Meeting(doCache.get(additionalID));
                meeting.addInvitedUsers(user);
                loadingService.updateDataObject(meeting.toDataObject());
                doCache.refresh(meeting.getId());
                System.out.println("Размер листа пользователей, приглашенных создателем встречи " + meeting.getInvitedUsers().size());
            } catch (ExecutionException e) {
                check = false;
                e.printStackTrace();
            }
        }

        response.setText(new Gson().toJson(check));
        // Логирвоание:
        loggerLog.add(Log.SEND_INVITE_MEETING, additionalID, senderID);
        return response;
    }

    @RequestMapping(value = "/addBeggingUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody Response addBeggingUser(
            @RequestParam("senderID") Integer senderID,
            @RequestParam("additionalID") Integer additionalID
    ) throws ParseException, ExecutionException, CustomException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, IOException, MessagingException {

        Response response = new Response();
        boolean check;
        DataObject currentUser = loadingService.getDataObjectByIdAlternative(senderID);
        User user = converter.ToUser(currentUser);
        Meeting meeting = null;
        try {
            meeting = new Meeting(doCache.get(additionalID));
            meeting.addBeggingUsers(user);
            loadingService.updateDataObject(meeting.toDataObject());
            doCache.refresh(meeting.getId());
            check = true;
        } catch (ExecutionException e) {
            check = false;
            e.printStackTrace();
        }
        System.out.println("Размер листа с пользователями желающие принять участие " + meeting.getBeggingUsers().size());
        response.setText(new Gson().toJson(check));
        return response;
    }

}
