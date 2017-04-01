package service.statistics;

import dbHelp.DBHelp;
import entities.DataObject;
import entities.Log;

import entities.Settings;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import service.application_settings.SettingsLoader;
import service.converter.Converter;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;



/**
 * Created by Hroniko on 12.03.2017.
 * Класс для регистрации всех элементарных событий и действий юзера в базу
 * для последующего использования в менеджере статистики StatisticManager
 */

public class StatisticLogger {

    private static boolean on_off_logger = false; // Флаг включения/выключения работы логгера, по умолчанию выключен false
    private static boolean on_off_sheduler = false; // Флаг включения/выключения работы шедуллера, по умолчанию выключен false

    // И тут же надо накапливать все логи, а потом при достижении какого-то фиксированного их значения заносить в базу
    private static Integer max_count = 20; // Максимальное количество логов для хранения в накопителе, при превышении сброс логов в базу
    // Будет одна общая очередь на всех
    private static final Queue<Log> logQueue = new ArrayBlockingQueue<>(max_count + 1); // Очередь логов

    // И еще одна общая очередь на всех уже для хранения id юзера, которому принадлежит лог
    private static final Queue<Integer> idQueue = new ArrayBlockingQueue<>(max_count + 1); // Очередь айди

    public StatisticLogger() throws IOException {
        //this.logQueue = new ArrayBlockingQueue<Log>(max_count + 1);
        loadSetting();
    }

    // Добавление в конец внутренней очереди
    public void add(Log log, Integer user_id) throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        if (!on_off_logger) return;
        logQueue.add(log);
        idQueue.add(user_id);
        //count ++;
        System.out.println(log.getDate() + " ::: Добавление лога в очередь, размер очереди: " + logQueue.size());
        // Проверяем, не пора ли переносить в базу:
        if (logQueue.size() == max_count){
            loadToDB();
        }
    }

    public void add(Integer logType, Integer user_id)  {
        if (! on_off_logger) return;
        String name = Log.convert(logType);
        String date = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format( new java.util.Date());
        Log log = new Log(logType, name, date);
        try {
            add(log, user_id);
        } catch (InvocationTargetException | SQLException | IllegalAccessException | ParseException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void add(Integer logType, String info, Integer user_id){
        if (! on_off_logger) return;
        String name = Log.convert(logType);
        String date = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format( new java.util.Date());
        Log log = new Log(logType, name, date, info);
        try {
            this.add(log, user_id);
        } catch (InvocationTargetException | SQLException | IllegalAccessException | ParseException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void add(Integer logType, Integer id, Integer user_id){
        if (! on_off_logger) return;
        String name = Log.convert(logType);
        String date = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format( new java.util.Date());
        Log log = new Log(logType, name, date, id);
        try {
            this.add(log, user_id);
        } catch (InvocationTargetException | SQLException | IllegalAccessException | ParseException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    // Получение лога из начала внутренней очереди (с удалением)
    synchronized public static Log removeLog(){
        Log log = logQueue.remove();
        //count --;
        return log;
    }
    // Получение айдишника юзера из начала внутренней очереди (с удалением)
    synchronized public static Integer removeId(){
        Integer id = idQueue.remove();
        //count --;
        return id;
    }

    // Перенос в базу: // В т.ч. и принудительный сброс командой извне

    public static void loadToDB() throws SQLException, NoSuchMethodException, IllegalAccessException, ParseException, InvocationTargetException {
        System.out.println(new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm").format( new java.util.Date()) + " :: Старт записи логов в базу (count = " + logQueue.size() + "): ");
        int i = 0;
        while (logQueue.size() > 0){
            //for (int i = 0; i < count; i++){
            i++;
            Log log = removeLog();
            Integer id = removeId();
            System.out.print(i + " ");
            // А тут надо перенести в базу
            // Конвертируем в датаобджект:
            DataObject dataObject = new Converter().toDO(log);
            // Генерируем айдишник и вставляем в датаобджект:
            dataObject.setId(new DBHelp().generationID(1008));
            // и переносим в базу
            new DBHelp().setDataObjectToDB(dataObject, id);

        }
        System.out.println("\n" + new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm").format( new java.util.Date()) + " :: Конец записи логов в базу.");

    }

    public static void tictack() throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        if (! on_off_sheduler) return;
        if (logQueue.size() < 1) return;
        loadToDB();
    }

    // 2017-03-17 Метод загрузки настройки логгера из настроечного файла приложения: // чтобы по ходу работы можно было менять, будет другой метод
    private void loadSetting() throws IOException {

        String log_queue_max_size = SettingsLoader.getSetting("logger_queue_max_size");
        max_count = Integer.parseInt(log_queue_max_size.trim());

        String on_off_log = SettingsLoader.getSetting("logger");
        if (on_off_log.equals("on")){
            on_off_logger = true;
        }
        String on_off_shedu = SettingsLoader.getSetting("logger_sheduler");
        if (on_off_shedu.equals("on")){
            on_off_sheduler = true;
        }
    }
}
