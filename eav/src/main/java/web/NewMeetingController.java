package web;

import com.google.common.cache.LoadingCache;
import entities.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.meetings.NewMeetingManager;
import service.meetings.NewMeetingRequest;
import service.meetings.NewMeetingResponce;
import service.notifications.NotificationService;
import service.optimizer.SlotManager;
import service.optimizer.SlotRequest;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 09.04.2017.
 */
// Класс-контроллер для работы со встречами (для новых встреч с плавающими границами и пр)
@Controller
public class NewMeetingController {

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();
    private UserServiceImp userService = new UserServiceImp();

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
        Integer id = new NewMeetingManager().setNewMeeting(title, date_start, date_end, null, info, tag, null); // Не знаю, нужен ли нам будет этот айдишник

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

        ArrayList<User> userList = meeting.getUsers(); // Обходим всех участников встречи, кроме текущего юзера, и отправляем им уведомление о том, что данный пользователь присоединился к встрече:


        for (User user : userList) {
            Integer idReceiver = user.getId(); // получатель уведомления
            if (!idSender.equals(idReceiver)) { // всем, кроме текущего юзера

                // Формируем уведомление
                Notification notification = new Notification("Пользователь " + userName + " присоединился к встрече " + meeting.getTitle(), idSender, idReceiver, Notification.MEETING_ACCEPT);
                // и прикрепляем его к пользователю (или, если он оффлайн, просто автоматом переносится в базу)
                NotificationService.sendNotification(notification);
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
            NotificationService.sendNotification(notification);
        }

        return "redirect:/meetings";
    }


    // 5) Обработка нажатия кнопки Отказаться от встречи в окне уведомления приглашения на встречу НАДО ДОДЕЛАТЬ!
    @RequestMapping(value = "/refuseNewMeeting", method = RequestMethod.POST)
    public String refuseNewMeeting(@RequestParam("meeting_id") String meeting_id) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {
        // Нужно удалить уведомление у текущего пользователя (из памяти и из базы),
        // затем создать уведомление для администратора о том, что данный пользователь отказался принять участие в встрече,

        return "redirect:/meetings";
    }

}
