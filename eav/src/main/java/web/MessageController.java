package web;

/**
 * Created by Hroniko on 03.02.2017.
 */

import com.google.common.cache.LoadingCache;
import dbHelp.DBHelp;
import entities.DataObject;
import entities.Message;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.converter.Converter;
import service.id_filters.MessageFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.*;
import java.text.SimpleDateFormat;
import java.util.concurrent.ExecutionException;
import org.springframework.ui.Model;
import service.id_filters.UserFilter;


@Controller
public class MessageController {

    private UserServiceImp userService = UserServiceImp.getInstance();

    private LoadingServiceImp loadingService = new LoadingServiceImp();

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();
    private Converter converter = new Converter();

    private ArrayList<DataObject> getListDataObject(Map<Integer, DataObject> map) {
        ArrayList<DataObject> list = new ArrayList<>();

        for(Map.Entry<Integer, DataObject> e : map.entrySet()) {
            list.add(e.getValue());
        }

        return list;
    }


    // 2017-02-25 Отправка сообщения по нажатию кнопки
    @RequestMapping(value = "/sendMessage3", method = RequestMethod.GET)
    @ResponseBody
    public Message sendNewMessage3(@RequestParam("text") String text) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        String[] msg = text.split("~"); // Разбиваем на массив слов

        String to_id = msg[0];
        text = msg[1];

        Integer from_id = userService.getObjID(userService.getCurrentUsername());
        System.out.println("Передано новое сообщение: \'" + text + "\' , отправитель: " + from_id +" , получатель: " + to_id);

        Date currentDate = new Date();
        SimpleDateFormat dateFormat = null;
        dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss", Locale.ENGLISH);

        String date_send = dateFormat.format(currentDate);

        Integer read_status = 0; // старус прочтения - ноль, не прочитано еще

        DataObject data_ObjectSender = loadingService.getDataObjectByIdAlternative(from_id);
        DataObject data_ObjectRecipient = loadingService.getDataObjectByIdAlternative(Integer.parseInt(to_id));

        String from_name = data_ObjectSender.getName();

        String to_name = data_ObjectRecipient.getName();

        System.out.println(from_name + " --> " + to_name);


        TreeMap<Integer, Object> mapAttr = new TreeMap<>();
        mapAttr.put(201, from_id.toString());
        mapAttr.put(202, to_id);
        mapAttr.put(203, date_send);
        mapAttr.put(204, read_status.toString());
        mapAttr.put(205, text);
        mapAttr.put(206, from_name);
        mapAttr.put(207, to_name);

        String name = "Message_" + userService.generationID(1003);

        DataObject dataObject = loadingService.createDataObject(name, 1003, mapAttr);



        loadingService.setDataObjectToDB(dataObject);


        Message message = new Message();
        message.setFrom_name(from_name);
        message.setDate_send(date_send);
        message.setText(text);

        return  message;
    }


    // 2017-02-26 Отдаем на страницу сообщений айди нужного пользователя, чтобы на странице была внедрена информация об id нужного для переписки юзера
    @RequestMapping("/sendMessage/{objectId}") //передаем все сообщения на страницу
    public String getIdForMessagePage(@PathVariable("objectId") Integer to_id, Map<String, Object> mapAttr) {
        mapAttr.put("to_id", to_id);
        return "sendMessage";
    }

    // 2017-02-26 Отрисовка истории сообщения для текущего пользователя и выбранного
    @RequestMapping(value = "/getArray", method = RequestMethod.GET)
    @ResponseBody
    public ArrayList<Message> getArray(@RequestParam String text) throws SQLException { // text для проверки тут, какую именно инфу вернуть. Потом сделаю ифы и ветвление по запросам ajax

        Integer to_id = Integer.parseInt(text); // ID выбранного пользователя
        Integer from_id = userService.getObjID(userService.getCurrentUsername()); // ID второго пользователя (текущего)
        ArrayList<Message> AR = new ArrayList<>();
        ArrayList<Integer> flagUpRead = new ArrayList<>();

        try {
            ArrayList<Integer> al = loadingService.getListIdFilteredAlternative(new MessageFilter(MessageFilter.BETWEEN_TWO_USERS_WITH_IDS, String.valueOf(from_id), String.valueOf(to_id)));
            System.out.println("Ищем в кэше сообщения данного пользователя ");
            Map<Integer, DataObject> map = doCache.getAll(al);
            ArrayList<DataObject> list = getListDataObject(map);
            System.out.println("Размер кэша после добавления " + doCache.size());

            for (DataObject DO : list) {
                Message message = converter.ToMessage(DO);
                AR.add(message);
                // И проверяем, если это сообщение текущему пользователю, а не от текущего, можно выставить флаг о прочтении
                if (message.getFrom_id() != from_id ){
                    flagUpRead.add(message.getId());
                }
            }
            // И выставляем флаги о прочтении:
            new DBHelp().updateMessageReadStatus(flagUpRead);

        } catch (ExecutionException | NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }

        return AR;
    }


    // Удаление сообщения по его id с перенаправлением обратно на историю сообщений
    @RequestMapping("/deleteMessage/{to_id}/{objectId}") // @RequestMapping(value = "/deleteMessage/{to_id}/{objectId}", method = RequestMethod.POST)
    public String deleteMessage(@PathVariable("to_id") int to_id, @PathVariable("objectId") int objectId) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        loadingService.deleteDataObjectById(objectId);
        doCache.invalidate(objectId);
        return "redirect: /sendMessage/{to_id}";
    }

    @RequestMapping("/sendMessage") // @RequestMapping(value = "/deleteMessage/{to_id}/{objectId}", method = RequestMethod.POST)
    public String sendMess(){
        return "sendMessage";
    }


}
