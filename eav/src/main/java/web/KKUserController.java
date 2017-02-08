package web;

import dbHelp.DBHelp;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import service.EventServiceImp;
import service.UserServiceImp;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.TreeMap;

/**
 * Created by Костя on 02.02.2017.
 */
@Controller
public class KKUserController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private UserServiceImp userService = UserServiceImp.getInstance();

    private EventServiceImp eventService = EventServiceImp.getInstance();

    @RequestMapping(value = "/user", method = RequestMethod.GET)
    public String getUserPage(User user, ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        user = userService.getCurrentUser(); // Получаем Объект текущего пользователя
        logger.info("User = " + user.toString());
        Integer idUser = userService.getObjID(userService.getCurrentUsername());
        m.addAttribute(user);
        m.addAttribute("allEvents", eventService.getEventList(idUser));
        return "user";
    }

    @RequestMapping(value = "/userAddEvent", method = RequestMethod.POST)
    public String addEvent(@RequestParam("name") String name,
                           @RequestParam("date_begin") String date_begin,
                           @RequestParam("date_end") String date_end,
                           @RequestParam("priority") String priority,
                           @RequestParam("info") String info
        ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {


        TreeMap<Integer, Object> mapAttr = new TreeMap<>();
        mapAttr.put(101, date_begin);
        mapAttr.put(102, date_end);
        mapAttr.put(103, null); // Продолжительность события. Пока что так, потом исправить, вставить расчет
        mapAttr.put(104, info);
        mapAttr.put(105, priority);


        eventService.setNewEvent(1002, name, mapAttr); // Передаем в хелпер задачу со всеми атрибутами

        return "redirect:/main-login";
    }

    // Редактирование события
    @RequestMapping(value = "/userChangeEvent/{eventId}", method = RequestMethod.POST)
    public String changeEvent(@PathVariable("eventId") Integer eventId,
                              @RequestParam("name") String name,
                              @RequestParam("date_begin") String date_begin,
                              @RequestParam("date_end") String date_end,
                              @RequestParam("priority") String priority,
                              @RequestParam("info") String info) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();
        mapAttr.put(101, date_begin);
        mapAttr.put(102, date_end);
        mapAttr.put(104, info);
        mapAttr.put(105, priority);


        // mapAttr.put(103, date_begin - date_end); // Продолжительность, потом вставить ее расчет
        eventService.updateEvent(eventId, name, mapAttr);
        return "redirect:/main-login";
    }

    @RequestMapping(value = "/userRemoveEvent/{eventId}", method = RequestMethod.POST)
    public String removeEvent(@PathVariable("eventId") Integer eventId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        eventService.deleteEvent(eventId);
        return "redirect:/main-login";
    }

}
