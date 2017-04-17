package web;

import com.google.common.cache.LoadingCache;
import entities.*;
import exception.CustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.converter.Converter;
import service.converter.DateConverter;
import service.id_filters.EventFilter;
import service.id_filters.NotificationFilter;
import service.notifications.UsersNotifications;
import service.optimizer.*;
import service.statistics.StatRequest;
import service.statistics.StatResponse;
import service.statistics.StatisticLogger;
import service.statistics.StatisticManager;
import service.tags.NameNodeTree;
import service.tags.TagTreeManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 08.04.2017.
 */
// Класс-контроллер для работы с оптимизатором встреч
@Controller
public class OptimizerController {

    // Собственный внутренний логгер для контроллера
    private StatisticLogger loggerLog = new StatisticLogger();

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();

    private LoadingServiceImp loadingService = new LoadingServiceImp();

    private UserServiceImp userService = new UserServiceImp();

    public OptimizerController() throws IOException {
    }

    private ArrayList<DataObject> getListDataObject(Map<Integer, DataObject> map) {
        ArrayList<DataObject> list = new ArrayList<>();

        for (Map.Entry<Integer, DataObject> e : map.entrySet()) {
            list.add(e.getValue());
        }

        return list;
    }

    // 1) На подгрузку страницы cвободных слотов:
    @RequestMapping(value = "/slots", method = RequestMethod.GET)
    public String slotsPage() throws SQLException {
        return "slots";
    }

    // 2) К запросу на получение данных по свободным слотам
    @RequestMapping(value = "/getFreeSlots", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    public @ResponseBody
    ArrayList<Slot> getStat(@RequestBody SlotRequest slotRequest) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {
        return new SlotManager().getFreeSlots(slotRequest);
    }

    // 3) На подгрузку страницы оптимизации расписания конкретного юзера:
    @RequestMapping(value = "/userOptimizer", method = RequestMethod.GET)
    public String userOptimizerPage() throws SQLException {
        return "userOptimizer";
    }

    // 4) 2017-04-13 На подгрузку страницы оптимизации расписания конкретного юзера: (все работает через сайвер, в отличие от методов в других контроллерах) Integer meeting_id
    @RequestMapping(value = "/userOptimizer/{meeting_id}/{date_start}/{date_end}", method = RequestMethod.GET)
    public String userOptimizerPage(@PathVariable("meeting_id") String meeting_id,
                                    @PathVariable("date_start") String date_start,
                                    @PathVariable("date_end") String date_end,
                                    ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ExecutionException, CustomException, ParseException {

        DataObject currentUser = loadingService.getDataObjectByIdAlternative(userService.getObjID(userService.getCurrentUsername()));

        // 2017-04-17 Подготавливаем сообщение о том, что слоты успешно подгружены
        String slot_message = null;

        Integer meet_id = new Integer(meeting_id.trim());
        // Пытаемся получить финальную точку сохранения эвентов из сейвера по составному ключу
        ArrayList<Event> events = SlotSaver.getEventFinalPoint(currentUser.getId(), meet_id, date_start, date_end);
        int user_id = userService.getObjID(userService.getCurrentUsername());
        // Если они есть, то уже начинали оптимизировать, но не успели сохранить в базу, и дальше работаем с ними, иначе надо выбрать из кэша (и из базы) нужные события и положить их в сейвер, а затем отправить на страницу
        if (events == null) {
            try {
                ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_CURRENT_USER, EventFilter.BETWEEN_TWO_DATES, date_start, date_end));
                System.out.println("Ищем в кэше список событий данного пользователя ");
                Map<Integer, DataObject> map = doCache.getAll(il);
                ArrayList<DataObject> list = getListDataObject(map);
                events = new ArrayList<>(list.size());
                for (DataObject dataObject : list) {
                    Event event = new Event(dataObject);
                    System.out.println(event);
                    events.add(event);

                }

                SlotSaver.add(user_id, meet_id, events, date_start, date_end); // и заносим точку сохранения в слот-сейвере, а там автоматом создастся для нее еще и вторая редактируемая копия

                // 2017-04-17 Добавляем сообщение о том, что слоты успешно подгружены
                slot_message = "События за выбранный период успешно загружены. Вы можете приступить к редактированию и оптимизации расписания";

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        // Если не сформировали сообщение, то уже редактировали раньше слоты:

        if (slot_message == null) slot_message = SlotSaver.getMessage(user_id, meet_id, date_start, date_end);
        // Сохраняем сообщение
        String message = "Загружена финальная точка сохранения расписания. Вы можете продолжить редактирование и выполнить оптимизацию";
        SlotSaver.addMessage(user_id, meet_id, message, date_start, date_end);

        User user = userService.getCurrentUser();
        m.addAttribute(user);

        m.addAttribute("allEvents", events);

        m.addAttribute("meeting_id", meeting_id);
        m.addAttribute("meeting_date_start", date_start);
        m.addAttribute("meeting_date_end", date_end);
        m.addAttribute("slot_message", slot_message); // сообщение


        // Логирование
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "userOptimizer", idUser); // Посещение страницы

        System.out.println("СПИСОК СОБЫТИЙ ССОСТОИТ ИЗ " + events.size());
        return "userOptimizer";
    }


    // 2017-04-13 На подгрузку страницы оптимизации расписания конкретного юзера: (все работает через сайвер, в отличие от методов в других контроллерах) Integer meeting_id
    /*@RequestMapping(value = "/userOptimizer/{meeting_id}/{date_start}/{date_end}", method = RequestMethod.GET)
    public String userOptimizerPage(@PathVariable("meeting_id") String meeting_id,
                                    @PathVariable("date_start") String date_start,
                                    @PathVariable("date_end") String date_end,

*/

    // 5) 2017-04-13 На добавление события через AJAX в сейвер
    @RequestMapping(value = "/userOptimizerAddEventAJAX", method = RequestMethod.POST)
    public @ResponseBody
    Response userOptimizerAddEventAJAX(@ModelAttribute("meeting_id") String meeting_id,
                                              @ModelAttribute("meeting_date_start") String meeting_date_start,
                                              @ModelAttribute("meeting_date_end") String meeting_date_end,

                                              @ModelAttribute("name") String name,
                                              @ModelAttribute("priority") String priority,
                                              @ModelAttribute("date_begin") String date_begin,
                                              @ModelAttribute("date_end") String date_end,
                                              @ModelAttribute("info") String info
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ParseException, ExecutionException {

        Response response = new Response();
        Integer root_id = userService.getObjID(userService.getCurrentUsername());
        Integer meet_id = new Integer(meeting_id.trim());

        // Формируем новое событие
        Long duration = DateConverter.duration(date_begin, date_end);
        Event event = new Event(name, date_begin, date_end, duration.toString(), priority, info);
        // public Event(String name, String date_begin, String date_end, String duration, String priority, String info) {
        // 2017-04-16 Выставляем значения других полей:
        event.setId(SlotSaver.genereteTempId()); // Нужно дать какое-то временное значение айди событию, которое будет использоваться как идентификатор в сейвере до тех пор, пока событие не попало в базу, а там оно уже станет нормальным
        event.setHost_id(root_id);
        event.setType_event(Event.BASE_EVENT); // 106 // Тип события - базовое
        event.setEditable(Event.EDITABLE); // 107 // Свойства события: редактируемое
        event.setFloating_date_begin(null); // 108 // Плавающая граница слева - Не нужна (не обязательно выставлять, и так она null, но все же)
        event.setFloating_date_end(null); // 109 // Плавающая граница справа - Не нужна (не обязательно выставлять, и так она null, но все же)



        // и заносим в сейвер наше событие, (а там автоматом сформируются для него свободные и занятые слоты)
        SlotSaver.addEvent(root_id, meet_id, event, meeting_date_start, meeting_date_end);

        // response.setText("OK");
        return response;
    }


    // 6) 2017-04-13 На редактирование события через AJAX в сейвере
    @RequestMapping(value = "/userOptimizerChangeEventAJAX/{eventId}", method = RequestMethod.POST)
    public @ResponseBody
    Response userOptimizerChangeEventAJAX(@PathVariable ("eventId") Integer event_id,
                                                 @ModelAttribute("meeting_id") String meeting_id,
                                                 @ModelAttribute("meeting_date_start") String meeting_date_start,
                                                 @ModelAttribute("meeting_date_end") String meeting_date_end,

                                                 @ModelAttribute("name") String name,
                                                 @ModelAttribute("priority") String priority,
                                                 @ModelAttribute("date_begin") String date_begin,
                                                 @ModelAttribute("date_end") String date_end,
                                                 @ModelAttribute("info") String info) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ParseException, ExecutionException {

        Response response = new Response();

        // Формируем новое событие
        Long duration = DateConverter.duration(date_begin, date_end);
        Event event = new Event(name, date_begin, date_end, duration.toString(), priority, info);
        event.setId(event_id);
        Integer root_id = userService.getObjID(userService.getCurrentUsername());
        Integer meet_id = new Integer(meeting_id.trim());

        // и обновляем в сейвере наше событие, (а там автоматом сформируются для него свободные и занятые слоты)
        SlotSaver.updateEvent(root_id, meet_id, event, meeting_date_start, meeting_date_end);

        //response.setText("OK");
        return response;
    }


    // 7) 2017-04-14 На удаление события через AJAX из сейвера
    @RequestMapping(value = "/userOptimizerRemoveEventAJAX/{eventId}", method = RequestMethod.POST)
    public @ResponseBody
    Response userOptimizerRemoveEventAJAX(@PathVariable ("eventId") Integer event_id,

                                                 @ModelAttribute("meeting_id") String meeting_id,
                                                 @ModelAttribute("meeting_date_start") String meeting_date_start,
                                                 @ModelAttribute("meeting_date_end") String meeting_date_end

    ) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ParseException, ExecutionException {

        Response response = new Response();

        Integer root_id = userService.getObjID(userService.getCurrentUsername());
        Integer meet_id = new Integer(meeting_id.trim());

        // удаляем из сейвера наше событие, (а там автоматом пересчитаются свободные и занятые слоты)
        SlotSaver.removeEvent(root_id, meet_id, event_id, meeting_date_start, meeting_date_end);

        //response.setText("OK");
        return response;
    }


    // 8) 2017-04-15 На применение оптимизатора к выбранной встрече:
    @RequestMapping(value = "/userOptimizerExecutorAJAX/{meeting_id}/{meeting_date_start}/{meeting_date_end}", method = RequestMethod.GET)
    public String userOptimizerExecutorAJAX(@PathVariable("meeting_id") String meeting_id,
                                            @PathVariable("meeting_date_start") String meeting_date_start,
                                            @PathVariable("meeting_date_end") String meeting_date_end

    ) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ParseException, ExecutionException, CloneNotSupportedException {


        Integer root_id = userService.getObjID(userService.getCurrentUsername());
        Integer meet_id = new Integer(meeting_id.trim());

        // Вызываем метод-оптимизатор с параметрами айди пользователя, айди встречи и период оптимизации:
        SlotOptimizer.optimizeItForUser(root_id, meet_id, meeting_date_start, meeting_date_end);

        // Перегружаем страничку
        return "redirect:/userOptimizer/" + meeting_id + "/" + meeting_date_start + "/" + meeting_date_end + "/";
    }


    // 8) 2017-04-16 На сохранение в базу последней точки сохранения из сейвера через AJAX (сейвер надо очистить от первоначальной копии, переписав ее финальной точкой сохранения)
    @RequestMapping(value = "/userOptimizerSaveAJAX/{meeting_id}/{meeting_date_start}/{meeting_date_end}", method = RequestMethod.GET)
    public String userOptimizerSaveAJAX(@PathVariable("meeting_id") String meeting_id,
                                        @PathVariable("meeting_date_start") String meeting_date_start,
                                        @PathVariable("meeting_date_end") String meeting_date_end

    ) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ParseException, ExecutionException, CloneNotSupportedException {


        Integer root_id = userService.getObjID(userService.getCurrentUsername());
        Integer meet_id = new Integer(meeting_id.trim());

        // Вызываем метод сохранения в базу всех изменений из сейвера с параметрами айди пользователя, айди встречи и период оптимизации:
        new SlotManager().saveAllEvents(root_id, meet_id, meeting_date_start, meeting_date_end);

        // Добавляем сообщение
        String message = "Ваше расписание за отмеченный период успешно сохранено!";
        SlotSaver.addMessage(root_id, meet_id, message, meeting_date_start, meeting_date_end);

        // Перегружаем страничку
        return "redirect:/userOptimizer/" + meeting_id + "/" + meeting_date_start + "/" + meeting_date_end + "/";
    }

    // 9) 2017-04-18 На отмену изменений последней точки сохранения в сейвере через AJAX
    @RequestMapping(value = "/userOptimizerResetAJAX/{meeting_id}/{meeting_date_start}/{meeting_date_end}", method = RequestMethod.GET)
    public String userOptimizerResetAJAX(@PathVariable("meeting_id") String meeting_id,
                                        @PathVariable("meeting_date_start") String meeting_date_start,
                                        @PathVariable("meeting_date_end") String meeting_date_end

    ) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ParseException, ExecutionException, CloneNotSupportedException {


        Integer root_id = userService.getObjID(userService.getCurrentUsername());
        Integer meet_id = new Integer(meeting_id.trim());

        // Вызываем метод сохранения в базу всех изменений из сейвера с параметрами айди пользователя, айди встречи и период оптимизации:
        new SlotManager().resetAllEvents(root_id, meet_id, meeting_date_start, meeting_date_end);

        // Перегружаем страничку
        return "redirect:/userOptimizer/" + meeting_id + "/" + meeting_date_start + "/" + meeting_date_end + "/";
    }


    // На подгрузку страницы оптимизации встречи для администртора встречи:
    @RequestMapping(value = "/adminOptimizer", method = RequestMethod.GET)
    public String adminOptimizerPage() throws SQLException {
        return "adminOptimizer";
    }

    // На прием измененных данных о расписании пользователя в сейвер (со страницы оптимизации расписания конкретного юзера):
    /* @RequestMapping(value = "/addToUserSlotSaver", method = RequestMethod.GET)
    public String userOptimizerPage() throws SQLException {
        return "userOptimizer";
    }
    */ // Тут еще надо продумать, как отправлять и что, например, в json кидать ил и просто через @RequestParam
}
