package web;

/**
 * Created by Hroniko on 29.01.2017.
 */

import dbHelp.DBHelp;
import entities.Event;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import service.UserService;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

@Controller
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    UserService userService = new UserService();

    @RequestMapping(value = "/addEvent", method = RequestMethod.GET)
    public String getEventPage() {
        return "addEvent";
    }

    // Добавление события
    @RequestMapping(value = "/addEvent", method = RequestMethod.POST)
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("date_begin") String date_begin,
                               @RequestParam("date_end") String date_end,
                               @RequestParam("priority") String priority,
                               @RequestParam("info") String info
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        // Integer idUser = new DBHelp().getObjID(userService.getCurrentUsername());

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();
        // mapAttr.put(1, idUser); // mapAttr.put(1, name); // id добавлять в список атрибутов нам не нужно
        mapAttr.put(101, date_begin);
        mapAttr.put(102, date_end);
        mapAttr.put(103, null); // Продолжительность события. Пока что так, потом исправить, вставить расчет
        mapAttr.put(104, info);
        mapAttr.put(105, priority);


        new DBHelp().addNewEvent(1002, name, mapAttr); // Передаем в хелпер задачу со всеми атрибутами

        return "main-login";
    }

    // Вытаскивание событий
    @Deprecated
    @RequestMapping("/allEvent")
    public String listObjects(Map<String, Object> map) throws SQLException {
        Integer idUser = new DBHelp().getObjID(userService.getCurrentUsername());
        map.put("allObject", new DBHelp().getEventList(idUser));
        return "allEvent";
    }

    // Удаление события по его id
    @RequestMapping("/deleteEvent/{objectId}")
    public String deleteEvent(@PathVariable("objectId") Integer objectId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        new DBHelp().deleteEvent(objectId);
        return "redirect:/allEvent";
    }

    // Выводим данные о событии на форму редактирования
    @RequestMapping("/editEvent/{objectId}")
    public String editEvent(@PathVariable("objectId") Integer eventId,
                            Event event, ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        event = new DBHelp().getEventByEventID(eventId);
        logger.info("Event = " + event.toString());
        m.addAttribute(event);
        return "/editEvent";
    }

    // Редактирование событияsdasdasd
    @RequestMapping(value = "/changeEvent/{eventId}", method = RequestMethod.POST)
    public String changeEvent(@PathVariable("eventId") Integer eventId,
                              @RequestParam("name") String name,
                              @RequestParam("date_begin") String date_begin,
                              @RequestParam("date_end") String date_end,
                              @RequestParam("priority") String priority,
                              @RequestParam("info") String info) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();
        mapAttr.put(101, date_begin);
        mapAttr.put(102, date_end);
        mapAttr.put(105, priority);
        mapAttr.put(104, info);

        // mapAttr.put(103, date_begin - date_end); // Продолжительность, потом вставить ее расчет

        new DBHelp().updateEvent(eventId, name, mapAttr);

        return "/allEvent";
    }


}
