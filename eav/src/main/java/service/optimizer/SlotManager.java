package service.optimizer;

import com.google.common.cache.LoadingCache;
import entities.*;
import service.*;
import service.cache.DataObjectCache;
import service.converter.*;
import service.id_filters.EventFilter;
import service.search.SearchParser;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 08.04.2017.
 */
// Класс для работы со свободными слотами
public class SlotManager {

    private LoadingServiceImp loadingService = new LoadingServiceImp();
    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();


    private UserServiceImp userService = new UserServiceImp();

    // 0) метод для получения текущей встречи:
    public Meeting getMeeting(Integer meeting_id) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        DataObject dataObject = loadingService.getDataObjectByIdAlternative(meeting_id);
        return new Meeting(dataObject);
    }

    // 1) Метод для получения списка свободных слотов для юзера с айди user_id за период времени с date_start до date_end
    public ArrayList<Slot> getFreeSlots(SlotRequest slotRequest) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException {
        ArrayList<Integer> listIds = new ArrayList<>();
        ArrayList<String> listSting = SearchParser.parse(slotRequest.getUser());
        assert listSting != null;
        for (String str : listSting
                ) {
            listIds.add(Integer.parseInt(str));
        }
        return getFreeSlots(listIds, new Integer(slotRequest.getMeeting().trim()), slotRequest.getStart().trim(), slotRequest.getEnd().trim());
    }

    // 2) Метод для получения списка свободных слотов для всех юзеров встречи за период времени с date_start до date_end
    public ArrayList<Slot> getFreeSlots(ArrayList<Integer> users, Integer meeting_id, String date_start, String date_end) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException {
        ArrayList<Slot> freeSlots = new ArrayList<>();
        ArrayList<Slot> freeSlotsForMeeting = new ArrayList<>();  // свободные слоты для встечи
        // Пробуем преобразовать даты из строки к обычным датам
        LocalDateTime start = DateConverter.stringToDate(date_start);
        LocalDateTime end = DateConverter.stringToDate(date_end);

        if (start == null | end == null) return freeSlots; // Выходим из метода, если не удалось сконвертировать
        // Выбираем все события из расписаеия юзера за заданный период:
        ArrayList<Integer> ids = new ArrayList<>();
        for (Integer user : users
                ) {
            ArrayList<Integer> list = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_USER_WITH_ID, String.valueOf(user), EventFilter.BETWEEN_TWO_DATES, date_start, date_end));
            ids.addAll(list);
        }

        ArrayList<DataObject> aldo = loadingService.getListDataObjectByListIdAlternative(ids); // allSlots
        ArrayList<Event> events = new Converter().ToEvent(aldo);
        ArrayList<Slot> usageSlots = new ArrayList<>();
        Meeting meeting = new Meeting(doCache.get(meeting_id));
        // Обходим занятые слоты и переносим в список занятых слотов:
        for (Event event : events) {
            usageSlots.add(new Slot(event));
        }
        // и надо еще как-то отсортировать в порядке увеличения даты // ок, сделал
        Collections.sort(usageSlots);

        // А затем обойти отсортированные занятые слоты и вытащить свободные:
        for (Slot usageSlot : usageSlots) {
            LocalDateTime newend = usageSlot.getStart();
            // если есть свободное место
            if (start.isBefore(newend)) {
                freeSlots.add(new Slot(start, newend));
            }
            start = usageSlot.getEnd();
        }
        // И в конце может остаться кусочек в самом конце отрезка времени (или же весь отрезок, если занятых слотов в нем не было), добавляем его:
        if (start.isBefore(end)) {
            freeSlots.add(new Slot(start, end));
        }

        for (Slot freeSlot : freeSlots
                ) {
            System.out.println(DateConverter.duration(freeSlot.getString_start(), freeSlot.getString_end()));
        }

        int count = 0;
        for (Slot freeSlot : freeSlots) {
            String startTime = freeSlot.getString_start();
            String endTime = freeSlot.getString_end();
            long duration = DateConverter.duration(startTime, endTime);

            if (duration >= Long.parseLong(meeting.getDuration())) {
                System.out.println("ПРОДОЛЖИТЕЛЬНОСТЬ СВОБОДНОГО СЛОТА" + duration);
                System.out.println("ПРОДОЛЖИТЕЛЬНОСТЬ ВСТРЕЧИ" + Long.parseLong(meeting.getDuration()));
                freeSlotsForMeeting.add(freeSlot);
                count++;
            }
        }
        System.out.println("КОЛИЧЕСТВО СВОБОДНЫХ СЛОТОВ :" + count);

        int user_id = userService.getObjID(userService.getCurrentUsername());

        // и оставим точку сохранения в слот-сейвере:
        SlotSaver.add(user_id, meeting_id, events, usageSlots, freeSlotsForMeeting, date_start, date_end);

        return freeSlotsForMeeting;
    }


    // 3) Метод проверки перекрытия ВСЕГО расписания пользователя (наложения задач расписания) на данную встречу // не для оптимизтора, а для проверки сразу после соглашения принять встречу
    public ArrayList<Event> getOverlapEvents(Meeting meeting) throws SQLException, ParseException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ArrayList<Event> result = null;
        // Вытаскиваем все копии встречи (чтобы в них найти подходящую данному юзеру):
        ArrayList<Event> duplicates = meeting.getDuplicates();
        Integer user_id = userService.getCurrentUser().getId();
        // Обходим список дубликатов в поисках события с подвешенным юзером:
        Boolean success_flag = false;
        Event event = null;
        for (int i = 0; i < duplicates.size(); i++) { // Event event : duplicates
            event = duplicates.get(i);
            if (user_id.equals(event.getHost_id())) {
                // Нашли! И выходим, меняя флаг
                success_flag = true;
                break;
            }
        }
        if (success_flag) {
            // Вытаскиваем из базы все, что встреча перекрыла
            String date_start = event.getDate_begin();
            String date_end = event.getDate_end();
            ArrayList<Integer> idsOverlapEvents = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_CURRENT_USER, EventFilter.BETWEEN_TWO_DATES, date_start, date_end));
            ArrayList<DataObject> aldoOverlapEvents = loadingService.getListDataObjectByListIdAlternative(idsOverlapEvents);
            result = new Converter().ToEvent(aldoOverlapEvents);
        }
        return result;
    }

    // 3 Плюс) 2017-04-15 Метод проверки перекрытия расписания (наложения задач расписания) на данную копию встречи (то есть передаем список событий и еще одно событие, с которым сравниваем)
    public ArrayList<Event> getOverlapEvents(ArrayList<Event> events, Event targetEvent) throws SQLException, ParseException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ArrayList<Event> result = new ArrayList<>();

        LocalDateTime date_start = DateConverter.stringToDate(targetEvent.getDate_begin()); // Получаем дату начала копии встречи
        LocalDateTime date_end = DateConverter.stringToDate(targetEvent.getDate_end()); // Получаем дату окончания копии встречи

        // Обходим список переданных событий и проверяем на наличие перекрытия:
        for (Event event : events) {
            LocalDateTime date_event_start = DateConverter.stringToDate(event.getDate_begin()); // Получаем дату начала текущего события из списка событий
            LocalDateTime date_event_end = DateConverter.stringToDate(event.getDate_end()); // Получаем дату окончания текущего события из списка событий

            // Если событие "вложено" в копию встречи,
            // или копия встречи вложена в событие,
            // или начало события раньше начала копии встречи, но окончание в период прохождения копии встречи,
            // или окончание события позже окончания копии встречи, но начало в период прохождения копии встречи,
            // то заносим это событие в список перекрывающихся
            if (date_event_start.isAfter(date_start) & date_event_end.isBefore(date_end) // Если событие "вложено" в копию встречи,
                    || date_start.isAfter(date_event_start) & date_end.isBefore(date_event_end) // или копия встречи вложена в событие,
                    || date_event_start.isBefore(date_start) & date_event_end.isAfter(date_start) // или начало события раньше начала копии встречи, но окончание в период прохождения копии встречи
                    || date_event_start.isBefore(date_end) & date_event_end.isAfter(date_end) // или окончание события позже окончания копии встречи, но начало в период прохождения копии встречи,
                    ) {
                result.add(event);
            }

        }

        return result;
    }

    // 4) Метод для получения списка занятых слотов на основе данных из переданного списка событий за период времени с date_start до date_end
    public ArrayList<Slot> getUsageSlots(ArrayList<Event> events, String date_start, String date_end) throws ParseException {
        ArrayList<Slot> usageSlots = null;
        // Конвертируем даты:
        LocalDateTime start = DateConverter.stringToDate(date_start);
        LocalDateTime end = DateConverter.stringToDate(date_end);

        if (start == null | end == null) return null; // Выходим из метода, если не удалось сконвертировать

        usageSlots = new ArrayList<>();

        // Обходим занятые слоты и переносим в список занятых слотов:
        for (Event event : events) {
            usageSlots.add(new Slot(event));
        }
        // Сортируем в порядке увеличения даты
        Collections.sort(usageSlots);

        return usageSlots;
    }

    // 5) Метод для получения списка свободных слотов, в которые может поместиться наша встреча, на основе данных из переданного списка событий за период времени с date_start до date_end
    public ArrayList<Slot> getFreeSlots(Integer meeting_id, ArrayList<Event> events, String date_start, String date_end) throws ParseException, ExecutionException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        ArrayList<Slot> freeSlots = null;

        // 1 Пробуем преобразовать даты из строки к обычным датам
        LocalDateTime start = DateConverter.stringToDate(date_start);
        LocalDateTime end = DateConverter.stringToDate(date_end);

        // 2 Выходим из метода, если не удалось сконвертировать:
        if (start == null | end == null) return null;

        // 3 Иначе все хорошо, продолжаем. Подготавливаем место под свободные слоты для встечи:
        ArrayList<Slot> freeSlotsForMeeting = new ArrayList<>();  // свободные слоты для встечи

        // 4 Получаем список занятых слотов за данный период:
        ArrayList<Slot> usageSlots = getUsageSlots(events, date_start, date_end);

        // 5 Выходим из метода, если не удалось получить занятые слоты
        if (usageSlots == null) return null;

        // 6 Иначе продолжаем. Формируем список свободных слотов за данный период:
        freeSlots = getAllFreeSlots(events, date_start, date_end); // Получаем все доступные свободные слоты:

        // 7 А теперь выбираем только те слоты, куда сможет поместиться наша встреча
        Meeting meeting = new Meeting(doCache.get(meeting_id));
        for (Slot freeSlot : freeSlots) {
            long duration = DateConverter.duration(freeSlot.getString_start(), freeSlot.getString_end());
            if (duration >= Long.parseLong(meeting.getDuration())) {
                freeSlotsForMeeting.add(freeSlot);
            }
        }
        return freeSlotsForMeeting;
    }

    // 6) Метод для получения списка всех доступных свободных слотов на основе данных из переданного списка событий за период времени с date_start до date_end
    public ArrayList<Slot> getAllFreeSlots(ArrayList<Event> events, String date_start, String date_end) throws ParseException, ExecutionException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        ArrayList<Slot> freeSlots = null;

        // 1 Пробуем преобразовать даты из строки к обычным датам
        LocalDateTime start = DateConverter.stringToDate(date_start);
        LocalDateTime end = DateConverter.stringToDate(date_end);

        // 2 Выходим из метода, если не удалось сконвертировать
        if (start == null | end == null) return null;

        // 3 Иначе продолжаем. Получаем список занятых слотов за данный период:
        ArrayList<Slot> usageSlots = getUsageSlots(events, date_start, date_end);

        // 4 Выходим из метода, если не удалось получить занятые слоты:
        if (usageSlots == null) return null;

        // 5 Иначе продолжаем. Формируем список свободных слотов за данный период:
        freeSlots = new ArrayList<>();
        // Для этого обходим отсортированные занятые слоты и вытаскиваем свободные:
        for (int i = 0; i < usageSlots.size(); i++) {
            Slot slot = usageSlots.get(i);
            LocalDateTime new_end = slot.getStart();
            // если есть свободное место
            if (start.isBefore(new_end)) {
                freeSlots.add(new Slot(start, new_end));
            }
            start = slot.getEnd();
        }
        // И в конце может остаться кусочек в самом конце отрезка времени (или же весь отрезок, если занятых слотов в нем не было), добавляем его:
        if (start.isBefore(end)) {
            freeSlots.add(new Slot(start, end));
        }

        return freeSlots;
    }

    // -----------------------------------------------------------------------------------------------------------------

    // 7) 2017-04-16 Метод сохранения финальной точки сохранения из сейвера в базу данных
    public void saveAllEvents(Integer user_id, Integer meeting_id, String opt_period_date_start, String opt_period_date_end) throws ParseException, SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ExecutionException {

        // 1 Получаем из сейвера начальную точку сохранения (она соответсвует тому, что сейчас есть в базе) по составному ключу:
        ArrayList<Event> startEvents = SlotSaver.getEventStartPoint(user_id, meeting_id, opt_period_date_start, opt_period_date_end);

        // 2 Получаем из сейвера финальную точку сохранения (соответсвует тому, что должно записаться в базу) по составному ключу:
        ArrayList<Event> finalEvents = SlotSaver.getEventFinalPoint(user_id, meeting_id, opt_period_date_start, opt_period_date_end);

        // 3 Подготавливаем точку соединения (обновления) начальной и конечной точек сохранения: -- то, что следует обновить в базе
        ArrayList<Event> updateEvents = new ArrayList<>();

        // 4 Подготавливаем точку соединения (создания) всех новых событий: -- то, что нужно будет создать в базе
        ArrayList<Event> createEvents = new ArrayList<>();

        // 5 Подготавливаем точку соединения (удаления) всех удаленных событий: -- то, что нужно будет удалить из базы
        ArrayList<Event> deleteEvents = null;

        // 6 Подготавливаем точку соединения (постоянства) событий, которых не коснулось изменение: -- то, что нужно просто оставить как есть в базе и в сейвере
        ArrayList<Event> persistenceEvents = new ArrayList<>();

        // 7 Обходим список и находим изменения (может быть разное количество) -- сортируем на три части: что обновить, что создать, а что удалить
        // Внешний цикл по i - обходим финальную точку:
        for (int i = 0; i < finalEvents.size(); i++){
            Event new_event = finalEvents.get(i); // вытаскиваем событие из финальной точки сохранения
            Integer new_event_id = new_event.getId(); // его айди
            String new_event_begin = new_event.getDate_begin(); // дата начала
            String new_event_end = new_event.getDate_end(); // дата окончания
            // Проверяем, вдруг это новое событие, тогда у него не будет еще айди
            if (new_event_id == null){
                // Тогда просто сохраняем его в точку создания (потом при заносе в базу им присвоятся айдишники)
                createEvents.add(new_event);
                // и переходим к следующему шагу по i
                continue;
            }
            // Иначе, всли все хорошо и айдишник есть, то продолжаем:
            // Ищем это событие по его айдишнику среди начальной точки - для этого запускаем внутренний цикл по j:
            for (int j = 0; j < startEvents.size(); j++){
                Event old_event = startEvents.get(i); // вытаскиваем событие из начальной точки сохранения
                Integer old_event_id = old_event.getId(); // его айди
                if (old_event_id.equals(new_event_id)){
                    // Если айдишники совпали, то нашли что ищем,
                    // Тогда можно сравнить время начала и конца:
                    String old_event_begin = old_event.getDate_begin();
                    String old_event_end = old_event.getDate_end();
                    // Если даты начала и конца не изменились, то отправляем событие в точку постоянства
                    if ( old_event_begin.equals(new_event_begin) & old_event_end.equals(new_event_end) ){
                        persistenceEvents.add(new_event);
                    }
                    else{
                        // иначе - в точку обновления
                        updateEvents.add(new_event);
                    }
                    // А еще надо удалить это событие из начальной точки, чтобы потом повторно его не просматривать при поисковом обходе:
                    startEvents.remove(old_event);
                    // И выйти из внутреннего цикла по j:
                    break;
                }
            }
        }

        // 8 В конце после всех обходов в startEvents останутся только те события, которые пользователь удалил из расписания. Их мы переместим в deleteEvents
        deleteEvents = startEvents; // переносим, если конечно осталось что переносить // это чтобы не запутаться

        // 9 Теперь переносим в базу все изменения:
        // 9-1 Обновляем:
        Converter converter = new Converter();
        for (Event event : updateEvents) {
            DataObject dataObject = converter.toDO(event);
            loadingService.updateDataObject(dataObject);
        }
        // 9-2 Создаем все, что нужно создать:
        for (int i = 0; i < createEvents.size(); i++){ // не через фор-ич, чтобы работать не с копиями и можно было изменить поле id у данных событий в сейвере
            Event event = createEvents.get(i);
            DataObject dataObject = converter.toDO(event);
            Integer id = loadingService.setDataObjectToDB(dataObject);
            if (event.getId() == null) event.setId(id); // Устанавливаем айдишник у события, у которого его не было // опять лишние перестраховки, но мало ли, по идее ни у кого в этом списке нет айди
        }
        // 9-3 Удаляем все, что нужно удалить:
        for (Event event : deleteEvents) {
            loadingService.deleteDataObjectById(event.getId());
        }

        // 10 Все прочие точки сохранения удаляем из слот-сейвера
        SlotSaver.remove(user_id, meeting_id, opt_period_date_start, opt_period_date_end);


        // 11 И добавляем новую точку сохранения в сейвер:
        // --- (именно в таком порядке! Сначала дописать новую точку, потом вызвать remove удаление всех остальных до нее.
        // --- Если наоборот, в ветке сейвера останутся две точки, а начальная не будет соответствоватт тому, что есть в базе.
        // --- В этом случае при повторных оптимизациях могут возникнуть попытки создать уже существующие события, а они будут перекрываться, хоть их айди будут разными))
        // Для этого собираем сначала все события в кучу
        finalEvents = new ArrayList<>(); // очищаем список
        {
            finalEvents.addAll(persistenceEvents); // и добавляем неизмененные события
            finalEvents.addAll(updateEvents); // и обновленные события
            finalEvents.addAll(createEvents); // а также созданные события
        }
        // а затем подготавливаем данные - рассчитываем занятые и свободные слоты:
        ArrayList<Slot> usageSlots = this.getUsageSlots(finalEvents, opt_period_date_start, opt_period_date_end);
        ArrayList<Slot> freeSlots = this.getFreeSlots(meeting_id, finalEvents, opt_period_date_start, opt_period_date_end);
        // и, наконец, переносим точку сохранения состояния слотов по составному ключу
        SlotSaver.add(user_id, meeting_id, finalEvents, usageSlots, freeSlots, opt_period_date_start, opt_period_date_end);
        SlotSaver.add(user_id, meeting_id, finalEvents, usageSlots, freeSlots, opt_period_date_start, opt_period_date_end); // и вторую копию



    }


}
