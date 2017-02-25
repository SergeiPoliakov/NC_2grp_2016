package web;

/**
 * Created by Hroniko on 03.02.2017.
 */

import com.google.common.cache.LoadingCache;
import entities.DataObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.id_filters.MessageFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;


@Controller
public class MessageController {

    private UserServiceImp userService = UserServiceImp.getInstance();

    private LoadingServiceImp loadingService = new LoadingServiceImp();

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();

    private ArrayList<DataObject> getListDataObject(Map<Integer, DataObject> map) {
        ArrayList<DataObject> list = new ArrayList<>();

        for(Map.Entry<Integer, DataObject> e : map.entrySet()) {
            list.add(e.getValue());
        }

        return list;
    }

    // Отправка сообщения по нажатию кнопки
    @RequestMapping(value = "/sendMessage1/{to_id}", method = RequestMethod.POST)
    public String sendNewMessage(@PathVariable("to_id") Integer to_id, @RequestParam("text") String text) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        Integer from_id = userService.getObjID(userService.getCurrentUsername());

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = null;
        dateFormat = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss", Locale.ENGLISH);

        String date_send = dateFormat.format(currentDate);

        Integer read_status = 0; // старус прочтения - ноль, не прочитано еще

        DataObject data_ObjectSender = loadingService.getDataObjectByIdAlternative(from_id);
        DataObject data_ObjectRecipient = loadingService.getDataObjectByIdAlternative(to_id);

        String from_name = data_ObjectSender.getName();

        String to_name = data_ObjectRecipient.getName();

        System.out.println(from_name + " --> " + to_name);


        TreeMap<Integer, Object> mapAttr = new TreeMap<>();
        mapAttr.put(201, from_id.toString());
        mapAttr.put(202, to_id.toString());
        mapAttr.put(203, date_send);
        mapAttr.put(204, read_status.toString());
        mapAttr.put(205, text);
        mapAttr.put(206, from_name);
        mapAttr.put(207, to_name);

        String name = "Message_" + userService.generationID(1003);

        DataObject dataObject = loadingService.createDataObject(name, 1003, mapAttr);

        loadingService.setDataObjectToDB(dataObject);

        // messageService.setNewMessage(1003, mapAttr);

        return  "redirect: /sendMessage/{to_id}";
    }


    // Отрисовка истории сообщения для текущего пользователя и выбранного
    @RequestMapping("/sendMessage/{objectId}") //передаем все сообщения на страницу
    public String listObjects(@PathVariable("objectId") Integer to_id, Map<String, Object> mapAttr) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int from_id = userService.getObjID(userService.getCurrentUsername());
        try {
            ArrayList<Integer> al = loadingService.getListIdFilteredAlternative(new MessageFilter(MessageFilter.BETWEEN_TWO_USERS_WITH_IDS, String.valueOf(from_id), String.valueOf(to_id)));
            System.out.println("Ищем в кэше сообщения данного пользователя ");
            Map<Integer, DataObject> map = doCache.getAll(al);
            ArrayList<DataObject> list = getListDataObject(map);
            System.out.println("Размер кэша после добавления " + doCache.size());

            mapAttr.put("to_id", to_id);
            mapAttr.put("allObject", list);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }


        return "sendMessage";
    }

    // Удаление сообщения по его id с перенаправлением обратно на историю сообщений
    @RequestMapping("/deleteMessage/{to_id}/{objectId}") // @RequestMapping(value = "/deleteMessage/{to_id}/{objectId}", method = RequestMethod.POST)
    public String deleteMessage(@PathVariable("to_id") int to_id, @PathVariable("objectId") int objectId) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        loadingService.deleteDataObjectById(objectId);
        doCache.invalidate(objectId);
        return "redirect: /sendMessage/{to_id}";
    }

}
