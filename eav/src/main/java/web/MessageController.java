package web;

/**
 * Created by Hroniko on 03.02.2017.
 */

import dbHelp.DBHelp;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import service.MessageServiceImp;
import service.UserServiceImp;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


@Controller
public class MessageController {
    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private UserServiceImp userService = UserServiceImp.getInstance();

    private MessageServiceImp messageService = MessageServiceImp.getInstance();



    // Отправка сообщения по нажатию кнопки
    @RequestMapping(value = "/sendMessage1/{to_id}", method = RequestMethod.POST)
    public String sendNewMessage(@PathVariable("to_id") int to_id, @RequestParam("text") String text) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        int from_id = userService.getObjID(userService.getCurrentUsername());

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = null;
        dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss", Locale.ENGLISH);

        String date_send = dateFormat.format( currentDate ).toString();

        int read_status = 0; // старус прочтения - ноль, не прочитано еще

        User user_from = userService.getCurrentUser();
        User user_to = userService.getUserByUserID(to_id);

        String from_name = user_from.getName() + " " + user_from.getSurname() + " " + user_from.getMiddleName();

        String to_name = user_to.getName() + " " + user_to.getSurname() + " " + user_to.getMiddleName();


        TreeMap<Integer, Object> mapAttr = new TreeMap<>();
        mapAttr.put(201, from_id); ////
        mapAttr.put(202, to_id); ////
        mapAttr.put(203, date_send); ////
        mapAttr.put(204, read_status); /////
        mapAttr.put(205, text); // Сам текст сообщения
        mapAttr.put(206, from_name);
        mapAttr.put(207, to_name);

        messageService.setNewMessage(1003, mapAttr); // Передаем в хелпер ообщение со всеми атрибутами // 1003 - тип Сообщение
        return  "redirect: /sendMessage/{to_id}";
    }


    // Отрисовка истории сообщения для текущего пользователя и выбранного
    @RequestMapping("/sendMessage/{objectId}") //передаем все сообщения на страницу
    public String listObjects(@PathVariable("objectId") Integer to_id, Map<String, Object> map) throws SQLException {
        int from_id = userService.getObjID(userService.getCurrentUsername());
        map.put("to_id", to_id);
        map.put("allObject", messageService.getMessageList(from_id, to_id));
        return "sendMessage";
    }

    // Удаление сообщения по его id с перенаправлением обратно на историю сообщений
    @RequestMapping("/deleteMessage/{to_id}/{objectId}") // @RequestMapping(value = "/deleteMessage/{to_id}/{objectId}", method = RequestMethod.POST)
    public String deleteMessage(@PathVariable("to_id") int to_id, @PathVariable("objectId") int objectId) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        messageService.deleteMessage(objectId);
        return "redirect: /sendMessage/{to_id}";
    }

}
