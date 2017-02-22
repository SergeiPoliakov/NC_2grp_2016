package web;

import com.google.common.cache.LoadingCache;
import entities.DataObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.LoadingServiceImp;
import service.cache.DataObjectCache;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.TreeMap;

/**
 * Created by Костя on 02.02.2017.
 */
@Controller
public class KKUserController {

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();

    private LoadingServiceImp loadingService = new LoadingServiceImp();



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

        loadingService.setDataObjectToDB(dataObject);

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
        mapAttr.put(103, null);
        mapAttr.put(104, info);
        mapAttr.put(105, priority);

        DataObject dataObject = new DataObject(eventId, name, 1002, mapAttr);

        loadingService.updateDataObject(dataObject);

        doCache.refresh(eventId);

        return "redirect:/main-login";
    }

    @RequestMapping(value = "/userRemoveEvent/{eventId}", method = RequestMethod.POST)
    public String removeEvent(@PathVariable("eventId") Integer eventId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        loadingService.deleteDataObjectById(eventId);
        return "redirect:/main-login";
    }

}