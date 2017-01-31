package web;

/**
 * Created by Hroniko on 29.01.2017.
 */

import dbHelp.DBHelp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import service.UserService;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

@Controller
public class EventController {

    UserService userService = new UserService();

    @RequestMapping(value = "/addEvent", method = RequestMethod.GET)
    public String getEventPage() {
        return "addEvent";
    }

    @RequestMapping(value = "/addEvent", method = RequestMethod.POST)
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("date_begin") String date_begin,
                               @RequestParam("date_end") String date_end,
                               @RequestParam("priority") int priority,
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

    // Обход всех событий
    @Deprecated
    @RequestMapping("/allEvent")
    public String listObjects(Map<String, Object> map) throws SQLException {
        Integer idUser = new DBHelp().getObjID(userService.getCurrentUsername());
        map.put("allObject", new DBHelp().getEventsIDbyObjectID(idUser));
        return "allEvent";
    }

}
