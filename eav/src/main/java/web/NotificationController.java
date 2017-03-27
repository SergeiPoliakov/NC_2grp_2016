package web;
/**
 * Created by Hroniko on 23.02.2017.
 * Контроллер для системы оповещения (новые события, сообщения, напоминания)
 */
import com.google.common.cache.LoadingCache;
import com.google.gson.Gson;
import dbHelp.DBHelp;
import entities.DataObject;
import entities.Notification;
import entities.User;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.converter.Converter;
import service.id_filters.MessageFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;


import service.LoadingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.id_filters.NotificationFilter;
import service.id_filters.UserFilter;


/**
 * @author Hroniko
 */
@Controller
public class NotificationController { // Тут вроде логировать не нужно, тут только подсвечиваются уведомления 2017-03-17
    private LoadingServiceImp loadingService = new LoadingServiceImp();
    private Converter converter = new Converter();

    // 2017-02-24 Уведомления о новых сообщениях (вывод в хедер) // Старый метод, используйте универсальный getNewNotification
    @RequestMapping(value = "/getNewMessage", method = RequestMethod.GET)
    public @ResponseBody
    Response getCharNum(@RequestParam String text) throws SQLException { // text для проверки тут, какую именно инфу вернуть. Потом сделаю ифы и ветвление по запросам ajax
        int count = 0;
        // Сначала получим все новые сообщения для пользователя:
        try {
            // Вытаскиваем все непрочитанные сообщения для данного пользователя:
            ArrayList<Integer> al = loadingService.getListIdFilteredAlternative(new MessageFilter(MessageFilter.TO_CURRENT_USER, MessageFilter.UNREAD));
            // Нам даже обходить их не надо, достаточно знать количество новых:
            count = al.size();

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        Response result = new Response();
        result.setText("Сообщения");
        result.setCount(count);
        return result;
    }

    // 2017-03-04 Уведомления о новых событиях (новых сообщениях, новых заявках в друзья и пр) (вывод в хедер)
    @RequestMapping(value = "/getNewNotification", method = RequestMethod.GET)
    public @ResponseBody
    Response getNewNotification(@RequestParam String text) throws SQLException { // text для проверки тут, какую именно инфу вернуть. Начал делать ифы и ветвление по запросам ajax
        int count = 0;
        Response result = new Response();
        // Сначала получим все новые сообщения для пользователя:
        try {
            ArrayList<Integer> al = null;
            // 2017-03-12 Добавил вытаскивание вообще всех непросмотренных уведомлений
            if (text.equals("all")){ // Если нам надо узнать, есть вообще любые новые уведомления (и о сообщениях, и о друзьях, и о приглашениях и пр.)
                // Вытаскиваем все уведомления для данного пользователя:
                al = loadingService.getListIdFilteredAlternative(new NotificationFilter(NotificationFilter.FOR_CURRENT_USER, NotificationFilter.UNSEEN));
                result.setText("Уведомления");
            }
            else if (text.equals("new_message")){ // Если нам надо узнать только, есть ли новые сообщения
                // Вытаскиваем все непрочитанные сообщения для данного пользователя:
                al = loadingService.getListIdFilteredAlternative(new MessageFilter(MessageFilter.TO_CURRENT_USER, MessageFilter.UNREAD));
                result.setText("Сообщения");
            }
            else if (text.equals("new_friend")) { // Если нам надо узнать только, есть ли новые заявки в друзья
                // Вытаскиваем айди всех неподтвержденных текущим пользователем друзей:
                al = loadingService.getListIdFilteredAlternative(new UserFilter(UserFilter.ALL_FRIENDS_UNCONFIRMED_FRIENDSHIP));
                result.setText("Заявки в друзья");
            }
            // Нам даже обходить их не надо, достаточно знать количество новых: // НО ПОТОМ ПРИ НЕОБХОДИМОСТИ ВООБЩЕ МОЖНО ОБОЙТИ И ВЫТАЩИТЬ ВСЕ ИЗ СПИСКА
            count = al.size();

        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        result.setCount(count);
        return result;
    }

    // Обработка запроса из центра уведомлений
    @RequestMapping(value = "/getNotification", method = RequestMethod.GET)
    public @ResponseBody
    Response getNotification() throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        LoggerFactory.getLogger(NotificationController.class).info("asdasd");
        /*
        ArrayList<Notification> notifications = new ArrayList<>();

        Notification not1 = new Notification("10001", "10003", "", "infoFriendAccept", "23.03.2017 11:00");
        Notification not2 = new Notification("10001", "10003", "", "infoFriendAccept", "24.03.2017 11:00");
        Notification not3 = new Notification("10001", "10003", "", "infoFriendAccept", "25.03.2017 11:00");

        not1.setSender(new User(new DBHelp().getObjectsByIdAlternative(Integer.parseInt(not1.getSenderID()))));
        not2.setSender(new User(new DBHelp().getObjectsByIdAlternative(Integer.parseInt(not2.getSenderID()))));
        not3.setSender(new User(new DBHelp().getObjectsByIdAlternative(Integer.parseInt(not3.getSenderID()))));

        notifications.add(not1);
        notifications.add(not2);
        notifications.add(not3);
        */
        // Получаем айдищники всех непрочитанных сообщений (приглашения на встречи, новые сообщения, добавленяи в друзья и пр.)
        //ArrayList<Integer> al = loadingService.getListIdFilteredAlternative(new NotificationFilter(NotificationFilter.FOR_CURRENT_USER, NotificationFilter.UNSEEN));
        ArrayList<Integer> al = loadingService.getListIdFilteredAlternative(new NotificationFilter(NotificationFilter.FOR_USER_WITH_ID, "10003", NotificationFilter.UNSEEN));
        ArrayList<Notification> notifications = new ArrayList<>();
        for(Integer id : al){
            Notification notification = converter.ToNotification(loadingService.getDataObjectByIdAlternative(id));
        }
        String json = new Gson().toJson(notifications);

        Response response = new Response();
        response.setText(json);
        response.setCount(notifications.size());
        return response;
    }

}