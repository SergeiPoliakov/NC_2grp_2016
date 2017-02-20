package web;

/**
 * Created by Hroniko on 29.01.2017.
 */

import com.google.common.cache.LoadingCache;
import entities.DataObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import service.EventServiceImp;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.id_filters.EventFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

@Controller
public class EventController {

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();

    private LoadingServiceImp loadingService = new LoadingServiceImp();

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

        // Работаем с кэшем:
        // предварительно удаляем
        doCache.invalidate(dataObject.getId());
        // и заносим
        doCache.put(dataObject.getId(), dataObject);
        System.out.println("Добавляем в кэш событие " + dataObject.getName());

        // и только потом обновляем объект в базе
        loadingService.setDataObjectToDB(dataObject);

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
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        mapObjects.put("allObject", aldo);

        return "allEvent";
    }

    // Удаление события по его id
    @RequestMapping("/deleteEvent/{objectId}")
    public String deleteEvent(@PathVariable("objectId") Integer objectId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        // Работаем с кэшем:
        // предварительно удаляем
        doCache.invalidate(objectId);

        loadingService.deleteDataObjectById(objectId);
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
            m.addAttribute(dataObject);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

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
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();

        mapAttr.put(101, date_begin);
        mapAttr.put(102, date_end);
        mapAttr.put(103, null);
        mapAttr.put(104, info);
        mapAttr.put(105, priority);

        DataObject dataObject = new DataObject(eventId, name, 1002, mapAttr);

        System.out.println("Обновляем в кэше событие " + dataObject.getName());

        // и только потом обновляем объект в базе
        loadingService.updateDataObject(dataObject);
        // Работаем с кэшем:
        // обновляем
        doCache.refresh(eventId);

        return "redirect:/allEvent";
    }

}

