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
import service.notifications.UsersNotifications;


/**
 * @author Hroniko
 */
@Controller
public class NotificationController { // Тут вроде логировать не нужно, тут только подсвечиваются уведомления 2017-03-17
    private LoadingServiceImp loadingService = new LoadingServiceImp();
    private UserServiceImp userService = new UserServiceImp();
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

        UsersNotifications usersNotifications = UsersNotifications.getInstance();
        Integer userID =  userService.getObjID(userService.getCurrentUsername());
        ArrayList<Notification> notifications = usersNotifications.getNotifications(userID);
        ArrayList<Notification> oldNotifications = (ArrayList<Notification>) usersNotifications.getNotifications(userID).clone();
        Integer oldSize = notifications.size();

        // Ожидание новых уведомлений
        while ( oldSize == notifications.size()){
            Thread.yield();
        }

        // Получение НОВЫХ уведомлений

        ArrayList<Notification> newNotifications = (ArrayList<Notification>) notifications.clone();
        newNotifications.removeAll(oldNotifications);

        String json = new Gson().toJson(newNotifications);

        Response response = new Response();
        response.setText(json);
        response.setCount(newNotifications.size());
        return response;
    }

    /*
    public static void main(String[] args) {

        ArrayList<Integer> a = new ArrayList<>();
        ArrayList<Integer> b = new ArrayList<>();
        ArrayList<Integer> c = new ArrayList<>();


        a.add(1);a.add(2);a.add(3);a.add(4);
        b.add(1);b.add(2);b.add(3);b.add(4);b.add(5);b.add(6);

        print(a);
        print(b);

        c = (ArrayList<Integer>) b.clone();
        c.removeAll(a);

        System.out.println("Result");
        print(c);
    }

    public static void print(ArrayList<Integer> list){
        for (Integer i : list){
            System.out.print(i + " ");
        }
        System.out.println();
    }
    */

}