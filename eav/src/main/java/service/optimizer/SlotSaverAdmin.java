package service.optimizer;
import entities.Event;
import entities.Meeting;

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

    // 1) Мапа для хранения времени занесения в сейвер очередного сохранения встречи
    public static final Map<String, ArrayList<LocalDateTime>> savePointDateMap = new ConcurrentHashMap<>();

    // 2) Мапа для хранения очередного сообщения для вывода на страницу
    public static final Map<String, String> messageMap = new ConcurrentHashMap<>();

    // 3) 2017-05-07 Мапа для хранения состояния дубликатов встречи (для администраторского оптимизатора)
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


    // 2017-05-07 Метод обновления в точке сохранения встречи со всеми дубликатами по составному ключу
    synchronized public static void updateDuplicate(Integer root_id, Meeting meeting) throws ParseException, NoSuchMethodException, ExecutionException, IllegalAccessException, SQLException, InvocationTargetException {
        // Фиксируем текущее время загрузки в мапу точки сохранения:
        LocalDateTime savePointDate = LocalDateTime.now();

        Integer meeting_id = meeting.getId();

        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id; // то-то типа "10003~2"

        // В сейвере две точки: [0] начальная (эталон) и [1] вторая (редактируемая).
        // Удаляем из сейвера вторую точку сохранения:

        // И меняем редактируемую точку сохранения:
        duplicateMap.get(key).remove(1);
        duplicateMap.get(key).add(meeting);
        /*
        Meeting meeting1 = duplicateMap.get(key).get(1);
        for(int i = 0; i < meeting1.getDuplicates().size(); i++){
            Event duplicate = meeting1.getDuplicates().get(i);
        }
        */
        //
        savePointDateMap.get(key).remove(1);
        savePointDateMap.get(key).add(savePointDate);

        System.out.println("Событие успешно изменено в сейвере!");
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

    // GetStart3) 2017-05-07 Метод получения стартовой точки сохранения дубликатов по составному ключу
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


    // --------------------------------------------------------------------------------------------------------------

    // 2017-05-07 Метод удаления всех точек сохранения для данного составного ключа
    // (например, когда уже нажали кнопку сохранить на странице и все изменения зокоммитились в базу, можно сайвер освободить от истории изменений)
    // но можно и оставить самую финальную, чтобы из бызы потом не тянуть
    synchronized public static void remove(Integer root_id, Integer meeting_id) {
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + meeting_id; // то-то типа "10003~2
        // Проверяем, есть ли такой ключ
        if (savePointDateMap.get(key) != null) { // если есть, то
            duplicateMap.remove(key);
            savePointDateMap.remove(key);
        }
    }





}