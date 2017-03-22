package web;
/**
 * Created by Hroniko on 23.02.2017.
 * Контроллер для системы оповещения (новые события, сообщения, напоминания)
 */
import com.google.common.cache.LoadingCache;
import dbHelp.DBHelp;
import entities.DataObject;
import entities.Meeting;
import entities.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.id_filters.MessageFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
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

}