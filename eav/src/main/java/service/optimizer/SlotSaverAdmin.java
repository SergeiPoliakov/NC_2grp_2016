package service.optimizer;

import entities.Event;
import entities.Meeting;
import service.statistics.StatRequest;
import service.statistics.StatResponse;
import service.statistics.StatisticSaver;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 07.05.2017.
 */
public class SlotSaverAdmin {

    private static volatile SlotSaverAdmin instance;

    // 0) Счетчик временного айди для создаваемых событий (для того, чтобы не путать, берем с обратным знаком):
    private static Integer tmp_id = -1;

    // 1) Мапа для хранения списка событий, полученных из бызы, и будет составной ключ
    public static final Map<String, ArrayList<ArrayList<Event>>> eventMap = new ConcurrentHashMap<>();

    // 2) Мапа для хранения списка посчитанных зянятых слотов за заданный период
    public static final Map<String, ArrayList<ArrayList<Slot>>> usageSlotMap = new ConcurrentHashMap<>();

    // 3) Мапа для хранения списка посчитанных пустых слотов за заданный период
    public static final Map<String, ArrayList<ArrayList<Slot>>> freeSlotMap = new ConcurrentHashMap<>();

    // 4) Мапа для хранения времени занесения в сейвер очередного сохранения слотов
    public static final Map<String, ArrayList<LocalDateTime>> savePointDateMap = new ConcurrentHashMap<>();

    // 5) Мапа для хранения очередного сообщения для вывода на страницу
    public static final Map<String, String> messageMap = new ConcurrentHashMap<>();

    // 6) 2017-05-07 Мапа для хранения состояния дубликатов встречи (для администраторского оптимизатора)
    public static final Map<String, ArrayList<Meeting>> duplicateMap = new ConcurrentHashMap<>();


    public static SlotSaverAdmin getInstance() {
        if (instance == null)
            synchronized (SlotSaverAdmin.class) {
                if (instance == null)
                    instance = new SlotSaverAdmin();
            }
        return instance;
    }

    // Конструктор:
    public SlotSaverAdmin() {
    }

    // Генератор временного айди события
    public static Integer genereteTempId(){
        return (tmp_id - 1);
    }

    // 2017-05-07 Метод сохранения сообщения по составному ключу
    synchronized public static void addMessage(Integer root_id, Integer meeting_id, String message) throws ParseException, NoSuchMethodException, ExecutionException, IllegalAccessException, SQLException, InvocationTargetException {
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id; // то-то типа "10003~2"

        if (messageMap.get(key) == null) { // если нет, то создаем ячейку
            messageMap.put(key, message);
        }
        else{
            messageMap.remove(key);
            messageMap.put(key, message);
        }

    }

    // 2017-05-07 Метод получения сообщения по составному ключу
    synchronized public static String getMessage(Integer root_id, Integer meeting_id) throws ParseException, NoSuchMethodException, ExecutionException, IllegalAccessException, SQLException, InvocationTargetException {
        // Фиксируем текущее время загрузки в мапу точки сохранения:
        LocalDateTime savePointDate = LocalDateTime.now();
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id; // то-то типа "10003~2"

        String result = messageMap.get(key);

        return result;

    }


    // 2017-05-07 Метод добавления точки сохранения состояния встречи по составному ключу
    synchronized public static void add(Integer root_id, Meeting meeting) throws ParseException, NoSuchMethodException, ExecutionException, IllegalAccessException, SQLException, InvocationTargetException, CloneNotSupportedException {
        // Фиксируем текущее время загрузки в мапу точки сохранения:
        LocalDateTime savePointDate = LocalDateTime.now();

        Integer meeting_id = meeting.getId();

        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id; // то-то типа "10003~2"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) == null) { // если нет, то создаем ячейку для серии точек сохранения
            duplicateMap.put(key, new ArrayList<>());
            savePointDateMap.put(key, new ArrayList<>());
        }

        // и заодно заносим в сейвер встречу

        // А затем привешиваем ко всем мапам: // привешиваем новую точку сохранения
        duplicateMap.get(key).add(meeting);
        savePointDateMap.get(key).add(savePointDate);

        // А также вторую (редактируемую) новую точку сохранения
        Meeting copyMeeting = (Meeting) meeting.clone();
        duplicateMap.get(key).add(copyMeeting);
        savePointDateMap.get(key).add(savePointDate);

    }

    // 2017-04-16 Метод добавления к точке сохранения одного события по составному ключу
    synchronized public static void addEvent(Integer root_id, Integer meeting_id, Event event, String meeting_date_start, String meeting_date_end) throws ParseException, NoSuchMethodException, ExecutionException, IllegalAccessException, SQLException, InvocationTargetException {
        // Фиксируем текущее время загрузки в мапу точки сохранения:
        LocalDateTime savePointDate = LocalDateTime.now();
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + meeting_date_start + "~" + meeting_date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"

        // В сейвере две точки: [0] начальная (эталон) и [1] вторая (редактируемая).
        // Вытаскиваем из сейвера вторую точку сохранения:
        ArrayList<Event> events = eventMap.get(key).get(1);
        // Добавляем новое событие:
        events.add(event);

        // Пересчитываем занятые и свободные слоты:
        ArrayList<Slot> usageSlots = new SlotManager().getUsageSlots(events, meeting_date_start, meeting_date_end);
        ArrayList<Slot> freeSlots = new SlotManager().getFreeSlots(meeting_id, events, meeting_date_start, meeting_date_end);

        // И меняем редактируемую точку сохранения:
        eventMap.get(key).remove(1);
        eventMap.get(key).add(events);
        //
        usageSlotMap.get(key).remove(1);
        usageSlotMap.get(key).add(usageSlots);
        //
        freeSlotMap.get(key).remove(1);
        freeSlotMap.get(key).add(freeSlots);
        //
        savePointDateMap.get(key).remove(1);
        savePointDateMap.get(key).add(savePointDate);

        System.out.println("Новое событие успешно добавлено в сейвер!");

    }




    // 2017-04-16 Метод обновления в точке сохранения одного события по составному ключу
    synchronized public static void updateEvent(Integer root_id, Integer meeting_id, Event event, String meeting_date_start, String meeting_date_end) throws ParseException, NoSuchMethodException, ExecutionException, IllegalAccessException, SQLException, InvocationTargetException {
        // Фиксируем текущее время загрузки в мапу точки сохранения:
        LocalDateTime savePointDate = LocalDateTime.now();
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + meeting_date_start + "~" + meeting_date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"

        // В сейвере две точки: [0] начальная (эталон) и [1] вторая (редактируемая).
        // Вытаскиваем из сейвера вторую точку сохранения:
        ArrayList<Event> events = eventMap.get(key).get(1);

        // Находим в ней данное событие: Event event : events
        for (int i = 0; i < events.size(); i++) {
            Event old_event = events.get(i);
            if (event.getId().equals(old_event.getId())) {
                System.out.println("Меняем событие " + old_event);

                // Если айдишки совпадают, заканчиваем поиск и подменяем новым значением:
                old_event.setName(event.getName());
                old_event.setDate_begin(event.getDate_begin());
                old_event.setDate_end(event.getDate_end());
                old_event.setDuration(event.getDuration());
                old_event.setPriority(event.getPriority());
                old_event.setInfo(event.getInfo());

                events.remove(i);
                events.add(old_event);
                System.out.println("на событие " + old_event);
                break;
            }
        }
        // Пересчитываем занятые и свободные слоты:
        ArrayList<Slot> usageSlots = new SlotManager().getUsageSlots(events, meeting_date_start, meeting_date_end);
        ArrayList<Slot> freeSlots = new SlotManager().getFreeSlots(meeting_id, events, meeting_date_start, meeting_date_end);

        // И меняем редактируемую точку сохранения:
        eventMap.get(key).remove(1);
        eventMap.get(key).add(events);
        //
        usageSlotMap.get(key).remove(1);
        usageSlotMap.get(key).add(usageSlots);
        //
        freeSlotMap.get(key).remove(1);
        freeSlotMap.get(key).add(freeSlots);
        //
        savePointDateMap.get(key).remove(1);
        savePointDateMap.get(key).add(savePointDate);

        System.out.println("Событие успешно изменено в сейвере!");
    }

    // 2017-04-16 Метод удаления из точки сохранения одного события по составному ключу
    synchronized public static void removeEvent(Integer root_id, Integer meeting_id, Integer event_id, String meeting_date_start, String meeting_date_end) throws ParseException, NoSuchMethodException, ExecutionException, IllegalAccessException, SQLException, InvocationTargetException {
        // Фиксируем текущее время загрузки в мапу точки сохранения:
        LocalDateTime savePointDate = LocalDateTime.now();
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + meeting_date_start + "~" + meeting_date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"

        // В сейвере две точки: [0] начальная (эталон) и [1] вторая (редактируемая).
        // Вытаскиваем из сейвера вторую точку сохранения:
        ArrayList<Event> events = eventMap.get(key).get(1);

        // Находим в ней данное событие: Event event : events
        for (int i = 0; i < events.size(); i++) {
            Event old_event = events.get(i);
            if (event_id.equals(old_event.getId())) {
                // Если айдишки совпадают, удаляем его:
                events.remove(i);
                break;
            }
        }
        // Пересчитываем занятые и свободные слоты:
        ArrayList<Slot> usageSlots = new SlotManager().getUsageSlots(events, meeting_date_start, meeting_date_end);
        ArrayList<Slot> freeSlots = new SlotManager().getFreeSlots(meeting_id, events, meeting_date_start, meeting_date_end);

        // И меняем редактируемую точку сохранения:
        eventMap.get(key).remove(1);
        eventMap.get(key).add(events);
        //
        usageSlotMap.get(key).remove(1);
        usageSlotMap.get(key).add(usageSlots);
        //
        freeSlotMap.get(key).remove(1);
        freeSlotMap.get(key).add(freeSlots);
        //
        savePointDateMap.get(key).remove(1);
        savePointDateMap.get(key).add(savePointDate);

        System.out.println("Событие успешно удалено из сейвера!");
    }



    // 2017-04-14 Просто метод копирования событий
    synchronized public static ArrayList<Event> copyEvents(ArrayList<Event> old_events) throws CloneNotSupportedException {
        ArrayList<Event> new_events = new ArrayList<>();

        for(int i = 0; i < old_events.size(); i++){
            Event copyEvent = (Event) old_events.get(i).clone();
            new_events.add(copyEvent);
        }
        return new_events;
    }

    //------------------------------------------------------------------------------------------------

    // GF1) Метод получения финальной точки сохранения занятых слотов по составному ключу
    synchronized public static ArrayList<Slot> getUsageSlotFinalPoint(Integer root_id, Integer meeting_id, String date_start, String date_end) {
        ArrayList<Slot> result = null;
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + date_start + "~" + date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) != null) { // если есть, то отдаем последнюю сохраненную
            result = usageSlotMap.get(key).get(usageSlotMap.get(key).size() - 1);
            // наче останется нулл, его и отдадим
        }
        return result;
    }

    // GS1) Метод получения стартовой точки сохранения занятых слотов по составному ключу
    synchronized public static ArrayList<Slot> getUsageSlotStartPoint(Integer root_id, Integer meeting_id, String date_start, String date_end) {
        ArrayList<Slot> result = null;
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + date_start + "~" + date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) != null) { // если есть, то отдаем последнюю сохраненную
            result = usageSlotMap.get(key).get(0);
            // наче останется нулл, его и отдадим
        }
        return result;
    }

    // GP1) Метод получения промежуточной точки сохранения занятых слотов по составному ключу
    synchronized public static ArrayList<Slot> getUsageSlotPoint(Integer root_id, Integer meeting_id, String date_start, String date_end, int posPoint) {
        ArrayList<Slot> result = null;
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + date_start + "~" + date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) != null) { // если есть, то отдаем последнюю сохраненную
            result = usageSlotMap.get(key).get(posPoint);
            // наче останется нулл, его и отдадим
        }
        return result;
    }

    //------------------------------------------------------------------------------------------------

    // GF2) Метод получения финальной точки сохранения свободных слотов по составному ключу
    synchronized public static ArrayList<Slot> getFreeSlotFinalPoint(Integer root_id, Integer meeting_id, String date_start, String date_end) {
        ArrayList<Slot> result = null;
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + date_start + "~" + date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) != null) { // если есть, то отдаем последнюю сохраненную
            result = freeSlotMap.get(key).get(freeSlotMap.get(key).size() - 1);
            // наче останется нулл, его и отдадим
        }
        return result;
    }

    // GS2) Метод получения стартовой точки сохранения свободных слотов по составному ключу
    synchronized public static ArrayList<Slot> getFreeSlotStartPoint(Integer root_id, Integer meeting_id, String date_start, String date_end) {
        ArrayList<Slot> result = null;
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + date_start + "~" + date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) != null) { // если есть, то отдаем последнюю сохраненную
            result = freeSlotMap.get(key).get(0);
            // наче останется нулл, его и отдадим
        }
        return result;
    }

    // GP2) Метод получения промежуточной точки сохранения свободных слотов по составному ключу
    synchronized public static ArrayList<Slot> getFreeSlotPoint(Integer root_id, Integer meeting_id, String date_start, String date_end, int posPoint) {
        ArrayList<Slot> result = null;
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + date_start + "~" + date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) != null) { // если есть, то отдаем последнюю сохраненную
            result = freeSlotMap.get(key).get(posPoint);
            // наче останется нулл, его и отдадим
        }
        return result;
    }

    //------------------------------------------------------------------------------------------------

    // GetEnd3) 2017-05-07 Метод получения финальной точки сохранения дубликатов по составному ключу
    synchronized public static Meeting getDuplicateFinalPoint(Integer root_id, Integer meeting_id) {
        Meeting result = null;
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id; // то-то типа "10003~2"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) != null) { // если есть, то отдаем последнюю сохраненную
            result = duplicateMap.get(key).get(1);
            // наче останется нулл, его и отдадим
        }
        return result;
    }

    // GetStart3) Метод получения стартовой точки сохранения дубликатов по составному ключу
    synchronized public static Meeting getDuplicateStartPoint(Integer root_id, Integer meeting_id) {
        Meeting result = null;
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id; // то-то типа "10003~2"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) != null) { // если есть, то отдаем последнюю сохраненную
            result = duplicateMap.get(key).get(0);
            // наче останется нулл, его и отдадим
        }
        return result;
    }

    // GP3) Метод получения промежуточной точки сохранения эвентов по составному ключу
    synchronized public static ArrayList<Event> getEventPoint(Integer root_id, Integer meeting_id, String date_start, String date_end, int posPoint) {
        ArrayList<Event> result = null;
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + date_start + "~" + date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) != null) { // если есть, то отдаем последнюю сохраненную
            result = eventMap.get(key).get(posPoint);
            // наче останется нулл, его и отдадим
        }
        return result;
    }

    // --------------------------------------------------------------------------------------------------------------

    // Метод удаления всех точек сохранения для данного составного ключа
    // (например, когда уже нажали кнопку сохранить на странице и все изменения зокоммитились в базу, можно сайвер освободить от истории изменений)
    // но можно и оставить самую финальную, чтобы из бызы потом не тянуть
    synchronized public static void remove(Integer root_id, Integer meeting_id, String date_start, String date_end) {
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + date_start + "~" + date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) != null) { // если есть, то
            eventMap.remove(key);
            usageSlotMap.remove(key);
            freeSlotMap.remove(key);
            savePointDateMap.remove(key);

           /*
            //  удаляем все остальные (по два каждого)
            while (savePointDateMap.get(key).size() > 0){
                eventMap.get(key).remove(0);
                usageSlotMap.get(key).remove(0);
                freeSlotMap.get(key).remove(0);
                savePointDateMap.get(key).remove(0);
            }
            */

        }
    }


}