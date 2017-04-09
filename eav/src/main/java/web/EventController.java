package web;

/**
 * Created by Hroniko on 29.01.2017.
 */

import com.google.common.cache.LoadingCache;
import entities.DataObject;
import entities.Event;
import entities.Log;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import service.LoadingServiceImp;
import service.cache.DataObjectCache;
import service.converter.DateConverter;
import service.id_filters.EventFilter;
import service.UserServiceImp;
import service.statistics.StatisticLogger;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@Controller
public class EventController {
    // Собственный логгер для контроллера
    private StatisticLogger loggerLog = new StatisticLogger();
    private UserServiceImp userService = new UserServiceImp();

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();
    private LoadingServiceImp loadingService = new LoadingServiceImp();

    public EventController() throws IOException {
    }

    private ArrayList<DataObject> getListDataObject(Map<Integer, DataObject> map) {
        ArrayList<DataObject> list = new ArrayList<>();

        for(Map.Entry<Integer, DataObject> e : map.entrySet()) {
            list.add(e.getValue());
        }

        return list;
    }

    @RequestMapping(value = "/addEvent", method = RequestMethod.GET)
    public String getEventPage() {
        return "addEvent";
    }

    // Добавление события к расписанию текущего юзера
    @RequestMapping(value = "/addEvent", method = RequestMethod.POST)
    public String addEventOld(@ModelAttribute("name") String name,
                               @ModelAttribute("priority") String priority,
                               @ModelAttribute("date_begin") String date_begin,
                               @ModelAttribute("date_end") String date_end,
                               @ModelAttribute("info") String info
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ParseException {

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();

        mapAttr.put(101, date_begin);
        mapAttr.put(102, date_end);
        mapAttr.put(103, String.valueOf(DateConverter.duration(date_begin, date_end)));
        mapAttr.put(104, info);
        mapAttr.put(105, priority);
        Integer host_id =  userService.getObjID(userService.getCurrentUsername());
        mapAttr.put(141, host_id); // Ссылка на юзера, создавшего событие

        DataObject dataObject = loadingService.createDataObject(name, 1002, mapAttr);

        // Работаем с кэшем:
        // предварительно удаляем
        doCache.invalidate(dataObject.getId());

        System.out.println("Добавляем в кэш событие " + dataObject.getName());

        // и только потом обновляем объект в базе
        int id = loadingService.setDataObjectToDB(dataObject);

        // Логируем:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.ADD_EVENT, id, idUser); // Добавление события и айди события

        return "redirect:/main-login";
    }


    // Вытаскивание событий
    @RequestMapping("/allEvent")
    public String listObjects(Map<String, Object> mapObjects) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_CURRENT_USER));
        ArrayList<DataObject> aldo = new ArrayList<>();
        // Работа с кэшем
        System.out.println("Размер кэша до обновления страницы " + doCache.size());
        try {
            System.out.println("Ищем в кэше список событий");
            Map<Integer, DataObject> map = doCache.getAll(il);
            aldo = getListDataObject(map);
            System.out.println("Размер кэша после добавления " + doCache.size());
            ArrayList<Event> events = new ArrayList<>(aldo.size());
            for (DataObject dataObject: aldo
                    ) {
                Event event = new Event(dataObject);
                events.add(event);
            }
            mapObjects.put("allObject", events);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Логируем:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "allEvent", idUser); // Посещение страницы просмотра списка событий

        return "allEvent";
    }

    // Удаление события по его id
    @RequestMapping("/deleteEvent/{objectId}")
    public String deleteEvent(@PathVariable("objectId") Integer objectId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        // Работаем с кэшем:
        // предварительно удаляем
        doCache.invalidate(objectId);

        loadingService.deleteDataObjectById(objectId);

        // Логируем:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.DEL_EVENT, "deleteEvent", idUser); // Удаление события (ВОТ ТУТ НАДО БЫ НЕ УДАЛЯТЬ СОБЫТИЯ, А МЕНЯТЬ ИМ СТАТУС НА НЕАКТИВНЫЙ, И ТУТ ПЕРЕДАВАТЬ ССЫЛКУ НА СОБЫТИЯ - его айди)
        return "redirect:/allEvent";
    }

    // Выводим данные о событии на форму редактирования
    @RequestMapping("/editEvent/{objectId}")
    public String editEvent(@PathVariable("objectId") Integer eventId,
                            ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        //DataObject dataObject = loadingService.getDataObjectByIdAlternative(eventId);
        //m.addAttribute(dataObject);

        // Работа с кэшем
        System.out.println("Размер кэша до обновления страницы " + doCache.size());
        try {
            System.out.println("Ищем в кэше текущее событие");
            DataObject dataObject = doCache.get(eventId);
            System.out.println("Размер кэша после добавления " + doCache.size());
            Event event = new Event(dataObject);
            m.addAttribute(event);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Логируем:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "editEvent", idUser); // Посещение страницы редактирования события
        return "/editEvent";
    }


    // Редактирование события
    @RequestMapping(value = "/changeEvent/{eventId}", method = RequestMethod.POST)
    public String changeEvent(@PathVariable("eventId") Integer eventId,
                              @ModelAttribute("name") String name,
                              @ModelAttribute("priority") String priority,
                              @ModelAttribute("date_begin") String date_begin,
                              @ModelAttribute("date_end") String date_end,
                              @ModelAttribute("info") String info
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ParseException {

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();

        mapAttr.put(101, date_begin);
        mapAttr.put(102, date_end);
        mapAttr.put(103, String.valueOf(DateConverter.duration(date_begin, date_end)));
        mapAttr.put(104, info);
        mapAttr.put(105, priority);

        DataObject dataObject = new DataObject(eventId, name, 1002, mapAttr);

        System.out.println("Обновляем в кэше событие " + dataObject.getName());

        // и только потом обновляем объект в базе
        int id = loadingService.updateDataObject(dataObject);
        // Работаем с кэшем:
        // обновляем
        doCache.refresh(eventId);

        // Логируем:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.EDIT_EVENT, id, idUser); // Посещение страницы редактирования события айди события
        return "redirect:/allEvent";
    }

}

