package web;

import dbHelp.DBHelp;
import entities.DataObject;
import entities.Event;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import service.EventServiceImp;
import service.LoadingServiceImp;
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

    private LoadingServiceImp loadingService = new LoadingServiceImp();

    private UserServiceImp userService = UserServiceImp.getInstance();

    private EventServiceImp eventService = EventServiceImp.getInstance();

    @RequestMapping(value = "/user{id}")
    public String viewUser(@PathVariable("id") int userId,
                           ModelMap m) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        User user = userService.getUserByUserID(userId);
        m.addAttribute(user);
        m.addAttribute("allEvents", eventService.getEventList(userId));
        return "/user";
    }

    @RequestMapping(value = "/userAddEvent", method = RequestMethod.POST)
    public String addEvent(@ModelAttribute("name") String name,
                           @ModelAttribute("priority") String priority,
                           @ModelAttribute("date_begin") String date_begin,
                           @ModelAttribute("date_end") String date_end,
                           @ModelAttribute("info") String info
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();

        mapAttr.put(101, date_begin);
        mapAttr.put(102, date_end);
        mapAttr.put(103, null);
        mapAttr.put(104, info);
        mapAttr.put(105, priority);

        DataObject dataObject = loadingService.createDataObject(name, 1002, mapAttr);

        eventService.setNewEvent(dataObject);

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