package web;

import com.google.common.cache.LoadingCache;
import entities.*;
import exception.CustomException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.converter.Converter;
import service.converter.DateConverter;
import service.id_filters.EventFilter;
import service.optimizer.*;
import service.statistics.StatisticLogger;


import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
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

    private Converter converter = new Converter();

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
    public
    @ResponseBody
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
        ArrayList<Event> events = SlotSaverUser.getEventFinalPoint(currentUser.getId(), meet_id, date_start, date_end);
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

                SlotSaverUser.add(user_id, meet_id, events, date_start, date_end); // и заносим точку сохранения в слот-сейвере, а там автоматом создастся для нее еще и вторая редактируемая копия

                // 2017-04-17 Добавляем сообщение о том, что слоты успешно подгружены
                slot_message = "События за выбранный период успешно загружены. Вы можете приступить к редактированию и оптимизации расписания";

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        // Если не сформировали сообщение, то уже редактировали раньше слоты:

        if (slot_message == null) slot_message = SlotSaverUser.getMessage(user_id, meet_id, date_start, date_end);
        // Сохраняем сообщение
        String message = "Загружена финальная точка сохранения расписания. Вы можете продолжить редактирование и выполнить оптимизацию";
        SlotSaverUser.addMessage(user_id, meet_id, message, date_start, date_end);

        User user = userService.getCurrentUser();
        m.addAttribute(user);

        m.addAttribute("allEvents", events);

        m.addAttribute("meeting_id", meeting_id);
        m.addAttribute("meeting_date_start", date_start);
        m.addAttribute("meeting_date_end", date_end);
        m.addAttribute("slot_message", slot_message); // сообщение

        // 2017-05-08 Добавляем данные о времени окончания редактирования и времени начала встречи для таймеров обратного отсчета на странице:
        m.addAttribute("timer_001", getDateEditCountdown(meet_id));
        m.addAttribute("timer_002", getDateStartCountdown(meet_id));


        /*
        // 1 Получаем сначала саму встречу по ее айди:
        Meeting meeting = new Meeting(doCache.get(meet_id));
        String date_edit_countdown = meeting.getDate_edit();
        if (date_edit_countdown == null) { // если это обычная встреча, у нее нет времени редактирования, поэтому просто подставляем текущее всремя
            date_edit_countdown = DateConverter.dateToCountdown(LocalDateTime.now());
        }
        else { // иначе переконвертируем к нужному формату
            date_edit_countdown = DateConverter.stringToCountdown(date_edit_countdown);
        }
        m.addAttribute("timer_001", date_edit_countdown);


        // 2 Получаем дубликат встречи из встречи для текущего юзера:
        Event duplicate = null;
        for (Event dupl : meeting.getDuplicates()){
            if (dupl.getHost_id().equals(user_id)){
                duplicate = dupl;
                break;
            }
        }
        String date_start_countdown = null;
        if (duplicate == null){ // если не нашли дубликата (что маловероятно), просто подставляем текущее время
            date_start_countdown = DateConverter.dateToCountdown(LocalDateTime.now());
        }
        else { // иначе все хорошо, конвертируем и подставляем время начала дубликата встречи
            date_start_countdown = DateConverter.stringToCountdown(duplicate.getDate_begin());
        }
        */


        // Логирование
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "userOptimizer", idUser); // Посещение страницы

        System.out.println("СПИСОК СОБЫТИЙ ССОСТОИТ ИЗ " + events.size());
        return "userOptimizer";
    }


    // 2017-05-08 Метод получения данных о времени окончания редактирования таймера №1 обратного отсчета на странице:
    private String getDateEditCountdown(Integer meeting_id) throws ExecutionException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ParseException {
        // 2017-05-08 Добавляем данные о времени окончания редактирования и времени начала встречи для таймеров обратного отсчета на странице:
        // 1 Получаем сначала саму встречу по ее айди:
        Meeting meeting = new Meeting(doCache.get(meeting_id));
        String date_edit_countdown = meeting.getDate_edit();
        if (date_edit_countdown == null) { // если это обычная встреча, у нее нет времени редактирования, поэтому просто подставляем текущее время
            date_edit_countdown = DateConverter.dateToCountdown(LocalDateTime.now());
        }
        else { // иначе переконвертируем к нужному формату
            date_edit_countdown = DateConverter.stringToCountdown(date_edit_countdown);
        }
        return date_edit_countdown;
    }

    // 2017-05-08 Метод получения данных о времени начала встречи таймера №2 обратного отсчета на странице:
    private String getDateStartCountdown(Integer meeting_id) throws ExecutionException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ParseException {
        // 2017-05-08 Добавляем данные о времени окончания редактирования и времени начала встречи для таймеров обратного отсчета на странице:
        // 1 Получаем сначала саму встречу по ее айди:
        Meeting meeting = new Meeting(doCache.get(meeting_id));
        int user_id = userService.getCurrentUser().getId();
        Event duplicate = null;
        for (Event dupl : meeting.getDuplicates()){
            if (dupl.getHost_id() != null && dupl.getHost_id() == user_id){
                duplicate = dupl;
                break;
            }
        }
        String date_start_countdown = null;
        if (duplicate == null){ // если не нашли дубликата (что маловероятно), просто подставляем время начала встречи
            // date_start_countdown = DateConverter.dateToCountdown(LocalDateTime.now());
            date_start_countdown = DateConverter.stringToCountdown(meeting.getDate_start());
        }
        else { // иначе все хорошо, конвертируем и подставляем время начала дубликата встречи
            date_start_countdown = DateConverter.stringToCountdown(duplicate.getDate_begin());
        }

        return date_start_countdown;
    }


    // 2017-04-13 На подгрузку страницы оптимизации расписания конкретного юзера: (все работает через сайвер, в отличие от методов в других контроллерах) Integer meeting_id
    /*@RequestMapping(value = "/userOptimizer/{meeting_id}/{date_start}/{date_end}", method = RequestMethod.GET)
    public String userOptimizerPage(@PathVariable("meeting_id") String meeting_id,
                                    @PathVariable("date_start") String date_start,
                                    @PathVariable("date_end") String date_end,

*/

    // 5) 2017-04-13 На добавление события через AJAX в сейвер
    @RequestMapping(value = "/userOptimizerAddEventAJAX", method = RequestMethod.POST)
    public
    @ResponseBody
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
        event.setId(SlotSaverUser.genereteTempId()); // Нужно дать какое-то временное значение айди событию, которое будет использоваться как идентификатор в сейвере до тех пор, пока событие не попало в базу, а там оно уже станет нормальным
        event.setHost_id(root_id);
        event.setType_event(Event.BASE_EVENT); // 106 // Тип события - базовое
        event.setEditable(Event.EDITABLE); // 107 // Свойства события: редактируемое
        event.setFloating_date_begin(null); // 108 // Плавающая граница слева - Не нужна (не обязательно выставлять, и так она null, но все же)
        event.setFloating_date_end(null); // 109 // Плавающая граница справа - Не нужна (не обязательно выставлять, и так она null, но все же)


        // и заносим в сейвер наше событие, (а там автоматом сформируются для него свободные и занятые слоты)
        SlotSaverUser.addEvent(root_id, meet_id, event, meeting_date_start, meeting_date_end);

        // response.setText("OK");
        return response;
    }


    // 6) 2017-04-13 На редактирование события через AJAX в сейвере
    @RequestMapping(value = "/userOptimizerChangeEventAJAX/{eventId}", method = RequestMethod.POST)
    public
    @ResponseBody
    Response userOptimizerChangeEventAJAX(@PathVariable("eventId") Integer event_id,
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
        SlotSaverUser.updateEvent(root_id, meet_id, event, meeting_date_start, meeting_date_end);

        //response.setText("OK");
        return response;
    }


    // 7) 2017-04-14 На удаление события через AJAX из сейвера
    @RequestMapping(value = "/userOptimizerRemoveEventAJAX/{eventId}", method = RequestMethod.POST)
    public
    @ResponseBody
    Response userOptimizerRemoveEventAJAX(@PathVariable("eventId") Integer event_id,

                                          @ModelAttribute("meeting_id") String meeting_id,
                                          @ModelAttribute("meeting_date_start") String meeting_date_start,
                                          @ModelAttribute("meeting_date_end") String meeting_date_end

    ) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ParseException, ExecutionException {

        Response response = new Response();

        Integer root_id = userService.getObjID(userService.getCurrentUsername());
        Integer meet_id = new Integer(meeting_id.trim());

        // удаляем из сейвера наше событие, (а там автоматом пересчитаются свободные и занятые слоты)
        SlotSaverUser.removeEvent(root_id, meet_id, event_id, meeting_date_start, meeting_date_end);

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
        SlotSaverUser.addMessage(root_id, meet_id, message, meeting_date_start, meeting_date_end);

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


    // 10) 2017-04-18 На формирование и загрузку страницы перекрытий встреч и событий расписания юзера
    @RequestMapping(value = "/userOptimizerProblem", method = RequestMethod.GET)
    public String userOptimizerProblemPage(ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ExecutionException, CustomException, ParseException {

        Integer user_id = userService.getObjID(userService.getCurrentUsername());

        TreeMap<Event, ArrayList<Event>> promlemDuplicates = SlotOptimizer.findUserProblemDuplicate();

        m.addAttribute("allObject", promlemDuplicates);

        System.out.println("Найдено " + promlemDuplicates.size() + " проблем (перекрытий встреч) в расписании пользователя");

        return "userOptimizerProblem";
    }

    // 11) 2017-04-19 На формирование страницы оптимизации по переданному айди дубликата встречи (со страницы списка проблем расписания userOptimizerProblem)
    @RequestMapping(value = "/userOptimizerGetOptimizerPage/{duplicate_id}", method = RequestMethod.GET)
    public String userOptimizerGetOptimizerPage(@PathVariable("duplicate_id") Integer duplicate_id
    ) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ParseException, ExecutionException, CloneNotSupportedException {

        // 1 Получаем сначала саму встречу по айди ее дубликата:
        Meeting meeting = new SlotManager().getMeetingByDuplicate(duplicate_id);

        // 2 Проверяем, нашли ли мы встречу:
        if (meeting == null) return "redirect:/userOptimizerProblem"; // Если не нашли, просто перенаправляем на страницу проблем расписания (но можно и лучше на страницу ошибки)

        // 3 Иначе все хорошо и мы можем сформировать правильный редирект на страницу оптимизации:
        // для этого нам потребуется айди встречи:
        String meeting_id = meeting.getId().toString();
        // дата начала периода оптимизации и дата конца периода оптимизации
        // выберем их таким образом, чтобы слева и справа от встречи было по нескольку дней, например, 1 день слева и пять - справа
        LocalDateTime left_date = DateConverter.stringToDate(meeting.getDate_start());
        left_date = left_date.minusDays(1);
        String st_left_date = DateConverter.dateToString(left_date);

        LocalDateTime right_date = DateConverter.stringToDate(meeting.getDate_end());
        right_date = right_date.plusDays(5);
        String st_right_date = DateConverter.dateToString(right_date);

        // 4 И делаем редирект на сраничку оптимизации
        return "redirect:/userOptimizer/" + meeting_id + "/" + st_left_date + "/" + st_right_date + "/";
    }
    // 12) 2017-04-19 На перенаправление на (пользовательскую или админскую) страницу встречи по переданному айди дубликата встречи (со страницы списка проблем расписания userOptimizerProblem)
    @RequestMapping(value = "/getMeetingPage/{duplicate_id}", method = RequestMethod.GET)
    public String getMeetingPage(@PathVariable("duplicate_id") Integer duplicate_id
    ) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ParseException, ExecutionException, CloneNotSupportedException {

        // 1 Получаем сначала саму встречу по айди ее дубликата:
        Meeting meeting = new SlotManager().getMeetingByDuplicate(duplicate_id);

        // 2 Проверяем, нашли ли мы встречу:
        if (meeting == null) return "redirect:/userOptimizerProblem"; // Если не нашли, просто перенаправляем на страницу проблем расписания (но можно и лучше на страницу ошибки)

        // 3 Иначе все хорошо и мы можем сформировать правильный редирект на страницу встречи:
        // для этого нам потребуется айди встречи:
        String meeting_id = meeting.getId().toString(); // А Проверка, является ли юзер организатором встречи, произойдет уже в другои контроллере при редиректе:

        return "redirect:/meeting" + meeting_id;
    }

    // 13) 2017-04-19 На удаление встречи (пользовательское (копии) или админское (самой встречи)) по переданному айди дубликата встречи (со страницы списка проблем расписания userOptimizerProblem)
    @RequestMapping(value = "/removeMeetingByDuplicate/{duplicate_id}", method = RequestMethod.GET)
    public String removeMeetingByDuplicate(@PathVariable("duplicate_id") Integer duplicate_id
    ) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ParseException, ExecutionException, CloneNotSupportedException {

        // 1 Получаем сначала саму встречу по айди ее дубликата:
        Meeting meeting = new SlotManager().getMeetingByDuplicate(duplicate_id);

        // 2 Проверяем, нашли ли мы встречу:
        if (meeting == null) return "redirect:/userOptimizerProblem"; // Если не нашли, просто перенаправляем на страницу проблем расписания (но можно и лучше на страницу ошибки)

        // 3 Иначе все хорошо и мы можем сформировать правильный вызов удаления встречи:
        // для этого нам потребуется айди встречи:
        Integer meeting_id = meeting.getId();
        // и айди создателя
        Integer root_id = meeting.getOrganizer().getId();
        // а также айди текущего юзера:
        User user = converter.ToUser(doCache.get(userService.getObjID(userService.getCurrentUsername())));
        Integer user_id = user.getId();

        // это для админа было, убрал за ненадобностью. Надо у него вообще эту кнопку убрать
        // 4 Проверяем на совпадение рут айди и юзер айди (является ли юзер организатором встречи):
        /*if (root_id.equals(user_id)){ // если да, то как админ удаляем саму встречу:
            new DBHelp().setDeletedMeeting(user_id, meeting_id);
        }
        else{ // иначе удаляем как пользователь (реализовал): */

            ArrayList<Integer> ids_duplicates = meeting.getDuplicateIDs(); // в этот список получаем из встречи все айдишники дубликатов
            for (Integer i: ids_duplicates
                 ) {
                DataObject dataObjectDuplicate = doCache.get(i);
                if (dataObjectDuplicate.getReference(141).get(0).equals(user_id)) {  //если это наш дубликат

                    //удаляем юзера из встречи
                    meeting.getUsers().remove(user);


                    //удаляем ссылку на дубликат из встречи
                    meeting.getDuplicates().remove(dataObjectDuplicate);

                    // Логирвоание:
                    loggerLog.add(Log.LEAVED_MEETING, meeting_id);
                }
            }

        loadingService.updateDataObject(meeting.toDataObject());
        doCache.invalidate(meeting_id);

        //удаляем дубликаты
        for (Integer i: ids_duplicates
                ) {
            DataObject dataObjectDuplicate = doCache.get(i);
            if (dataObjectDuplicate.getReference(141).get(0).equals(user_id)) {
                System.out.println("Дубликат встречи удален");
                loadingService.deleteDataObjectById(dataObjectDuplicate.getId());
            }
        }


        // 4 И делаем редирект на исходную страницу
        return "redirect:/userOptimizerProblem";
    }



    // -------------------------------------------------------------------------------------------------------------------------------------
    //
    // ДЛЯ АДМИНА ВСТРЕЧИ:



    // На подгрузку страницы оптимизации встречи для администртора встречи:
    @RequestMapping(value = "/adminOptimizer", method = RequestMethod.GET)
    public String adminOptimizerPage() throws SQLException {
        return "adminOptimizer";
    }



    // 14) 2017-05-07 На подгрузку страницы оптимизации встречи для администратора встречи: (все работает через сайвер, в отличие от методов в других контроллерах) Integer meeting_id
    @RequestMapping(value = "/adminOptimizer/{meeting_id}", method = RequestMethod.GET)
    public String adminOptimizerPage(@PathVariable("meeting_id") Integer meeting_id,
                                    ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ExecutionException, CustomException, ParseException {

        DataObject currentUser = loadingService.getDataObjectByIdAlternative(userService.getObjID(userService.getCurrentUsername()));
        int user_id = userService.getObjID(userService.getCurrentUsername());
        // 0) Подготавливаем сообщение о том, что данные успешно подгружены
        String info_message = null;

        // 1) Пытаемся получить финальную точку сохранения эвентов из сейвера по составному ключу
        Meeting meeting = SlotSaverAdmin.getDuplicateFinalPoint(currentUser.getId(), meeting_id);

        // Если она есть, то уже начинали оптимизировать, но не успели сохранить в базу, и дальше работаем с этой копией из сейвера,
        // иначе надо выбрать из кэша (или из базы) нужную встречу и положить ее в сейвер, а затем отправить на страницу:
        if (meeting == null) {
            try {
                System.out.println("Подгружаем нужную встречу");
                meeting = new SlotManager().getMeeting(meeting_id);

                SlotSaverAdmin.add(user_id, meeting); // и заносим точку сохранения в админский слот-сейвере, а там автоматом создастся для нее еще и вторая редактируемая копия

                // Добавляем сообщение о том, что данные успешно подгружены
                info_message = "Встреча и данные участников за выбранный период успешно загружены. Вы можете приступить к редактированию и оптимизации встречи";

            } catch (ExecutionException e) {
                e.printStackTrace();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }

        // Если не сформировали сообщение, то уже редактировали раньше встречу через сейвер:

        if (info_message == null) info_message = SlotSaverAdmin.getMessage(user_id, meeting_id);
        // Сохраняем сообщение
        String message = "Загружена финальная точка сохранения встречи. Вы можете продолжить редактирование и выполнить оптимизацию";
        SlotSaverAdmin.addMessage(user_id, meeting_id, message);

        m.addAttribute("meeting", meeting);

        m.addAttribute("meeting_id", meeting_id);

        m.addAttribute("info_message", info_message); // сообщение

        // 2017-05-08 Добавляем данные о времени окончания редактирования и времени начала встречи для таймеров обратного отсчета на странице:
        m.addAttribute("timer_001", getDateEditCountdown(meeting_id));
        m.addAttribute("timer_002", getDateStartCountdown(meeting_id));

        return "adminOptimizer";
    }


    // 15) 2017-05-07 На применение админского оптимизатора к выбранной встрече:
    @RequestMapping(value = "/adminOptimizerExecutor/{meeting_id}", method = RequestMethod.GET)
    public String adminOptimizerExecutor(@PathVariable("meeting_id") Integer meeting_id

    ) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ParseException, ExecutionException, CloneNotSupportedException {


        Integer root_id = userService.getObjID(userService.getCurrentUsername());

        // Вызываем метод-оптимизатор с параметрами айди пользователя и айди встречи:
        SlotOptimizer.optimizeItForAdmin(root_id, meeting_id);

        // Перегружаем страничку
        return "redirect:/adminOptimizer/" + meeting_id;
    }



    // 16) 2017-05-07 На отмену изменений последней точки сохранения в сейвере через AJAX
    @RequestMapping(value = "/adminOptimizerReset/{meeting_id}", method = RequestMethod.GET)
    public String adminOptimizerReset(@PathVariable("meeting_id") Integer meeting_id

    ) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ParseException, ExecutionException, CloneNotSupportedException {


        Integer root_id = userService.getObjID(userService.getCurrentUsername());

        // Вызываем метод сохранения в базу всех изменений из сейвера с параметрами айди пользователя, айди встречи и период оптимизации:
        SlotOptimizer.resetMeeting(root_id, meeting_id);

        // Перегружаем страничку
        return "redirect:/adminOptimizer/" + meeting_id;
    }


    // 17) 2017-05-07 На сохранение в базу последней точки сохранения (встречи) из сейвера админа (сейвер надо очистить от первоначальной копии, переписав ее финальной точкой сохранения)
    @RequestMapping(value = "/adminOptimizerSave/{meeting_id}", method = RequestMethod.GET)
    public String adminOptimizerSave(@PathVariable("meeting_id") Integer meeting_id
    ) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ParseException, ExecutionException, CloneNotSupportedException {


        Integer root_id = userService.getObjID(userService.getCurrentUsername());

        // Вызываем метод сохранения в базу всех изменений из сейвера с параметрами айди пользователя, айди встречи и период оптимизации:
        new SlotOptimizer().saveMeeting(root_id, meeting_id);

        // Добавляем сообщение
        String message = "Все изменения в данных встречи успешно сохранены!";
        SlotSaverAdmin.addMessage(root_id, meeting_id, message);

        // Перегружаем страничку
        return "redirect:/adminOptimizer/" + meeting_id;
    }


}
