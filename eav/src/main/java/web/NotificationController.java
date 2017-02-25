package web;
/**
 * Created by Hroniko on 23.02.2017.
 * Контролле для системы оповещения (новые события, сообщения, напоминания)
 */
import com.google.common.cache.LoadingCache;
import entities.DataObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import service.id_filters.MessageFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;

import service.LoadingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;


/**
 * @author Hroniko
 */
@Controller
public class NotificationController {
    private UserServiceImp userService = UserServiceImp.getInstance();
    private LoadingServiceImp loadingService = new LoadingServiceImp();
    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();


    @RequestMapping(value = "/getCharNum", method = RequestMethod.GET)
    public @ResponseBody
    Response getCharNum(@RequestParam String text) throws SQLException { // text для проверки тут, какую именно инфу вернуть. Потом сделаю ифы и ветвление по запросам ajax
        int count = 0;
        // Сначала получим все новые сообщения для пользователя:
        try {
            // Вытаскиваем все непрочитанные сообщения для данного пользователя:
            ArrayList<Integer> al = loadingService.getListIdFilteredAlternative(new MessageFilter(MessageFilter.TO_CURRENT_USER, MessageFilter.UNREAD));
            // Нам даже обходить их не надо, достаточно знать количество новых:
            count = al.size();

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        Response result = new Response();
        result.setText("Сообщения");
        result.setCount(count);
        return result;
    }
}