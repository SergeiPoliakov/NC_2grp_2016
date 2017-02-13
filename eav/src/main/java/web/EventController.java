package web;

/**
 * Created by Hroniko on 29.01.2017.
 */

import entities.DataObject;
import entities.Event;
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
import java.util.Map;
import java.util.TreeMap;

@Controller
public class EventController {

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    private LoadingServiceImp loadingService = new LoadingServiceImp();

    private UserServiceImp userService = UserServiceImp.getInstance();

    private EventServiceImp eventService = EventServiceImp.getInstance();

    @RequestMapping(value = "/addEvent", method = RequestMethod.GET)
    public String getEventPage() {
        return "addEvent";
    }

    // Добавление события
    @RequestMapping(value = "/addEvent", method = RequestMethod.POST)
    public String registerUser(@ModelAttribute("name") String name,
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

    // Вытаскивание событий
    @RequestMapping("/allEvent")
    public String listObjects(Map<String, Object> map) throws SQLException {
        Integer idUser = userService.getObjID(userService.getCurrentUsername());
        map.put("allObject", eventService.getEventList(idUser));
        return "allEvent";
    }

    // Удаление события по его id
    @RequestMapping("/deleteEvent/{objectId}")
    public String deleteEvent(@PathVariable("objectId") Integer objectId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        eventService.deleteEvent(objectId);
        return "redirect:/allEvent";
    }

    // Выводим данные о событии на форму редактирования
    @RequestMapping("/editEvent/{objectId}")
    public String editEvent(@PathVariable("objectId") Integer eventId,
                            Event event, ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        event = eventService.getEventByEventID(eventId);
        logger.info("Event = " + event.toString());
        m.addAttribute(event);
        return "/editEvent";
    }

    // Редактирование события
    @RequestMapping(value = "/changeEvent/{eventId}", method = RequestMethod.POST)
    public String changeEvent(@PathVariable("eventId") Integer eventId,
                              @ModelAttribute("setEvent") Event event
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {


        eventService.updateEvent(eventId, event);

        return "redirect:/allEvent";
    }


}
