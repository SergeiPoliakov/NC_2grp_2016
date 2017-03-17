package web;

import com.google.common.cache.LoadingCache;
import entities.DataObject;
import entities.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.statistics.StaticticLogger;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Костя on 02.02.2017.
 */
@Controller
public class KKUserController {
    // Собственный внутренний логгер для контроллера
    private StaticticLogger logger = new StaticticLogger();

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();
    private UserServiceImp userService = new UserServiceImp();
    private LoadingServiceImp loadingService = new LoadingServiceImp();


    @RequestMapping(value = "/userAddEvent", method = RequestMethod.POST)
    public String addEventToCurrentUser(@ModelAttribute("name") String name,
                           @ModelAttribute("priority") String priority,
                           @ModelAttribute("date_begin") String date_begin,
                           @ModelAttribute("date_end") String date_end,
                           @ModelAttribute("info") String info
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();

        mapAttr.put(101, date_begin);
        mapAttr.put(102, date_end);
        mapAttr.put(103, "");
        mapAttr.put(104, info);
        mapAttr.put(105, priority);
        Integer host_id =  userService.getObjID(userService.getCurrentUsername());
        mapAttr.put(141, host_id); // Ссылка на юзера, создавшего событие

        DataObject dataObject = loadingService.createDataObject(name, 1002, mapAttr);

        int id = loadingService.setDataObjectToDB(dataObject);

        // Логируем:
        logger.add(Log.ADD_EVENT, id);
        return "redirect:/main-login";
    }

    // 2017-03-02 Добавление события к встрече с заданным айди встречи (пока не прикрутил к странице!!! ну там просто ссылку приписать и все)
    @RequestMapping(value = "/addEvent/{meeting_id}", method = RequestMethod.POST)
    public String addEventToMeeting(@PathVariable("meeting_id") Integer meeting_id,
                                    @ModelAttribute("name") String name,
                                    @ModelAttribute("priority") String priority,
                                    @ModelAttribute("date_begin") String date_begin,
                                    @ModelAttribute("date_end") String date_end,
                                    @ModelAttribute("info") String info
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();

        mapAttr.put(101, date_begin);
        mapAttr.put(102, date_end);
        mapAttr.put(103, "");
        mapAttr.put(104, info);
        mapAttr.put(105, priority);
        mapAttr.put(141, meeting_id); // Ссылка на встречу, к которой прикреплено событие
        DataObject dataObject = loadingService.createDataObject(name, 1002, mapAttr);

        // Работаем с кэшем:
        // предварительно удаляем
        doCache.invalidate(dataObject.getId());

        System.out.println("Добавляем в кэш событие " + dataObject.getName());

        // и только потом обновляем объект в базе
        int id = loadingService.setDataObjectToDB(dataObject);
        // Логируем:
        logger.add(Log.ADD_EVENT, id);
        return "redirect:/main-login";
    }

    // Редактирование события
    @RequestMapping(value = "/userChangeEvent/{eventId}", method = RequestMethod.POST)
    public String changeEvent(@PathVariable("eventId") Integer eventId,
                              @ModelAttribute("name") String name,
                              @ModelAttribute("priority") String priority,
                              @ModelAttribute("date_begin") String date_begin,
                              @ModelAttribute("date_end") String date_end,
                              @ModelAttribute("info") String info) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();

        mapAttr.put(101, date_begin);
        mapAttr.put(102, date_end);
        mapAttr.put(103, "");
        mapAttr.put(104, info);
        mapAttr.put(105, priority);

        DataObject dataObject = new DataObject(eventId, name, 1002, mapAttr);

        int id = loadingService.updateDataObject(dataObject);

        doCache.refresh(eventId);

        // Логируем:
        logger.add(Log.EDIT_EVENT, id);
        return "redirect:/main-login";
    }

    @RequestMapping(value = "/userRemoveEvent/{eventId}", method = RequestMethod.POST)
    public String removeEvent(@PathVariable("eventId") Integer eventId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        loadingService.deleteDataObjectById(eventId);

        // Логируем:
        logger.add(Log.DEL_EVENT, "userRemoveEvent");
        return "redirect:/main-login";
    }

}