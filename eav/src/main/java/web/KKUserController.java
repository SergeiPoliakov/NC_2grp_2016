package web;

import dbHelp.DBHelp;
import entities.Event;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
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
    public String addEvent(@ModelAttribute("setEvent") Event event
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {


        eventService.setNewEvent(event); // Передаем в хелпер задачу со всеми атрибутами

        return "redirect:/main-login";
    }

    // Редактирование события
    @RequestMapping(value = "/userChangeEvent/{eventId}", method = RequestMethod.POST)
    public String changeEvent(@PathVariable("eventId") Integer eventId,
                              @ModelAttribute("updateEvent") Event event) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        eventService.updateEvent(eventId, event);
        return "redirect:/main-login";
    }

    @RequestMapping(value = "/userRemoveEvent/{eventId}", method = RequestMethod.POST)
    public String removeEvent(@PathVariable("eventId") Integer eventId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        eventService.deleteEvent(eventId);
        return "redirect:/main-login";
    }

}