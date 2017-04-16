package service.optimizer;

import entities.Event;
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
 * Created by Hroniko on 09.04.2017.
 */
// Класс для хранения состояния слотов до внесения правок через оптимизатор
// Тут еще будет хранится история работы со слотами с момента загрузки из базы до иоиента нажатия кнопки сохранить у юзера на странице
public class SlotSaver {

    private static volatile SlotSaver instance;

    // 1) Мапа для хранения списка событий, полученных из бызы, и будет составной ключ
    public static final Map<String, ArrayList<ArrayList<Event>>> eventMap = new ConcurrentHashMap<>();

    // 2) Мапа для хранения списка посчитанных зянятых слотов за заданный период
    public static final Map<String, ArrayList<ArrayList<Slot>>> usageSlotMap = new ConcurrentHashMap<>();

    // 3) Мапа для хранения списка посчитанных пустых слотов за заданный период
    public static final Map<String, ArrayList<ArrayList<Slot>>> freeSlotMap = new ConcurrentHashMap<>();

    // 4) Мапа для хранения времени занесения в сейвер очередного сохранения слотов
    public static final Map<String, ArrayList<LocalDateTime>> savePointDateMap = new ConcurrentHashMap<>();


    public static SlotSaver getInstance() {
        if (instance == null)
            synchronized (SlotSaver.class) {
                if (instance == null)
                    instance = new SlotSaver();
            }
        return instance;
    }

    // Конструктор:
    public SlotSaver() {
    }


    // Метод добавления точки сохранения состояния слотов по составному ключу
    synchronized public static void add(Integer root_id, Integer meeting_id, ArrayList<Event> events, ArrayList<Slot> usageSlots, ArrayList<Slot> freeSlots, String date_start, String date_end) {
        // Фиксируем текущее время загрузки в мапу точки сохранения:
        LocalDateTime savePointDate = LocalDateTime.now();
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + date_start + "~" + date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) == null) { // если нет, то создаем ячейку для серии точек сохранения
            eventMap.put(key, new ArrayList<>());
            usageSlotMap.put(key, new ArrayList<>());
            freeSlotMap.put(key, new ArrayList<>());
            savePointDateMap.put(key, new ArrayList<>());
        }
        // А затем привешиваем ко всем мапам: // привешиваем новую точку сохранения
        eventMap.get(key).add(events);
        usageSlotMap.get(key).add(usageSlots);
        freeSlotMap.get(key).add(freeSlots);
        savePointDateMap.get(key).add(savePointDate);
    }

    // Метод добавления к точке сохранения одного события по составному ключу
    synchronized public static void addEvent(Integer root_id, Integer meeting_id, Event event, String meeting_date_start, String meeting_date_end) throws ParseException, NoSuchMethodException, ExecutionException, IllegalAccessException, SQLException, InvocationTargetException {
        // Фиксируем текущее время загрузки в мапу точки сохранения:
        LocalDateTime savePointDate = LocalDateTime.now();
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + meeting_date_start + "~" + meeting_date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"

        ArrayList<Event> events = null;
        ArrayList<Slot> usageSlots = null;
        ArrayList<Slot> freeSlots = null;


        // Проверяем, есть ли такой составной ключ в сейвере (хотя, по идее, он уже должен быть там!):
        if (savePointDateMap.get(key) == null) { // если нет, то создаем ячейку для серии точек сохранения
            eventMap.put(key, new ArrayList<>());
            usageSlotMap.put(key, new ArrayList<>());
            freeSlotMap.put(key, new ArrayList<>());
            savePointDateMap.put(key, new ArrayList<>());

            events = new ArrayList<>();
        } else { // Иначе, если такой составной ключ есть, копируем последнюю точку сохранения и вносим изменения в нее
            events = copyEvents(getEventFinalPoint(root_id, meeting_id, meeting_date_start, meeting_date_end));
            // Надо бы еще сразу проверку на перекрытие реализовать, но это потом тогда отдельным сервисом
        }
        // Добавляем к списку событий новое событие
        events.add(event);
        // Пересчитываем занятые и свободные слоты:
        usageSlots = new SlotManager().getUsageSlots(events, meeting_date_start, meeting_date_end);
        freeSlots = new SlotManager().getFreeSlots(meeting_id, events, meeting_date_start, meeting_date_end);

        // А затем привешиваем ко всем мапам: // привешиваем новую точку сохранения
        eventMap.get(key).add(events);
        usageSlotMap.get(key).add(usageSlots);
        freeSlotMap.get(key).add(freeSlots);
        savePointDateMap.get(key).add(savePointDate);
    }




    // Метод обновления в точке сохранения одного события по составному ключу
    synchronized public static void updateEvent(Integer root_id, Integer meeting_id, Event event, String meeting_date_start, String meeting_date_end) throws ParseException, NoSuchMethodException, ExecutionException, IllegalAccessException, SQLException, InvocationTargetException {
        // Фиксируем текущее время загрузки в мапу точки сохранения:
        LocalDateTime savePointDate = LocalDateTime.now();
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + meeting_date_start + "~" + meeting_date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"

        ArrayList<Event> events = null;
        ArrayList<Slot> usageSlots = null;
        ArrayList<Slot> freeSlots = null;

        // Копируем последнюю точку сохранения и вносим изменения в нее
        events = copyEvents(getEventFinalPoint(root_id, meeting_id, meeting_date_start, meeting_date_end));
        // Находим в ней данное событие:
        Event old_event = null;
        for (Event tmp_event : events) {
            if (event.getId() == tmp_event.getId()) {
                // Если айдишки совпадают, заканчиваем поиск
                old_event = tmp_event;
                break;
            }
        }
        if (old_event == null) { // Если не нашли, то добавляем
            addEvent(root_id, meeting_id, event, meeting_date_start, meeting_date_end); //
        } // иначе обновляем (заменяем новым):
        else{
            events.remove(old_event);
            old_event = event;
            events.add(old_event);

        }

        // Пересчитываем занятые и свободные слоты:
        usageSlots = new SlotManager().getUsageSlots(events, meeting_date_start, meeting_date_end);
        freeSlots = new SlotManager().getFreeSlots(meeting_id, events, meeting_date_start, meeting_date_end);

        // А затем привешиваем ко всем мапам: // привешиваем новую точку сохранения
        eventMap.get(key).add(events);
        usageSlotMap.get(key).add(usageSlots);
        freeSlotMap.get(key).add(freeSlots);
        savePointDateMap.get(key).add(savePointDate);
    }

    // Метод удаления из точки сохранения одного события по составному ключу
    synchronized public static void removeEvent(Integer root_id, Integer meeting_id, int event_id, String meeting_date_start, String meeting_date_end) throws ParseException, NoSuchMethodException, ExecutionException, IllegalAccessException, SQLException, InvocationTargetException {
        // Фиксируем текущее время загрузки в мапу точки сохранения:
        LocalDateTime savePointDate = LocalDateTime.now();
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + meeting_date_start + "~" + meeting_date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"

        ArrayList<Event> events = null;
        ArrayList<Slot> usageSlots = null;
        ArrayList<Slot> freeSlots = null;

        // Копируем последнюю точку сохранения и вносим изменения в нее
        events = copyEvents(getEventFinalPoint(root_id, meeting_id, meeting_date_start, meeting_date_end));
        // Находим в ней данное событие:
        for (int i = 0; i < events.size(); i++){
            Event tmp_event = events.get(i);
            if (event_id == tmp_event.getId()){
                // Если айдишки совпадают, заканчиваем поиск и удаляем событие
                events.remove(i);
                break;
            }
        }

        // Пересчитываем занятые и свободные слоты:
        usageSlots = new SlotManager().getUsageSlots(events, meeting_date_start, meeting_date_end);
        freeSlots = new SlotManager().getFreeSlots(meeting_id, events, meeting_date_start, meeting_date_end);

        // А затем привешиваем ко всем мапам: // привешиваем новую точку сохранения
        eventMap.get(key).add(events);
        usageSlotMap.get(key).add(usageSlots);
        freeSlotMap.get(key).add(freeSlots);
        savePointDateMap.get(key).add(savePointDate);
    }



    // 2017-04-14 Просто метод копирования событий
    synchronized public static ArrayList<Event> copyEvents(ArrayList<Event> old_events){
        ArrayList<Event> new_events = new ArrayList<>();
        new_events.addAll(old_events);
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

    // GF3) Метод получения финальной точки сохранения эвентов по составному ключу
    synchronized public static ArrayList<Event> getEventFinalPoint(Integer root_id, Integer meeting_id, String date_start, String date_end) {
        ArrayList<Event> result = null;
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + date_start + "~" + date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) != null) { // если есть, то отдаем последнюю сохраненную
            result = eventMap.get(key).get(eventMap.get(key).size() - 1);
            // наче останется нулл, его и отдадим
        }
        return result;
    }

    // GS3) Метод получения стартовой точки сохранения эвентов по составному ключу
    synchronized public static ArrayList<Event> getEventStartPoint(Integer root_id, Integer meeting_id, String date_start, String date_end) {
        ArrayList<Event> result = null;
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id + "~" + date_start + "~" + date_end; // то-то типа "10003~2~02.04.2017 00:00~09.04.2017 00:00"
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) != null) { // если есть, то отдаем последнюю сохраненную
            result = eventMap.get(key).get(0);
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
        if (savePointDateMap.get(key) != null) { // если есть, то вытаскиваем последнюю сохраненную
            // ArrayList<Event> events = eventMap.get(key).get(eventMap.get(key).size()-1);
            // ArrayList<Slot> usageSlots = usageSlotMap.get(key).get(usageSlotMap.get(key).size()-1);
            // ArrayList<Slot> freeSlots = freeSlotMap.get(key).get(freeSlotMap.get(key).size()-1);
            // LocalDateTime savePointDate = savePointDateMap.get(key).get(savePointDateMap.get(key).size()-1);

            // а затем удаляем все остальные, кроме последнего .size()-1
            for (int i = 0; i < usageSlotMap.get(key).size() - 2; i++) {
                eventMap.get(key).remove(i);
                usageSlotMap.get(key).remove(i);
                freeSlotMap.get(key).remove(i);
                savePointDateMap.get(key).remove(i);
            }
        }
    }


}
