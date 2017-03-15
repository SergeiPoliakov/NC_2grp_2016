package service.statistics;

import dbHelp.DBHelp;
import entities.DataObject;
import entities.Log;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import service.converter.Converter;

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
@Component
@EnableScheduling
public class StaticticLogger {
    // И тут же надо накапливать все логи, а потом при достижении какого-то фиксированного их значения заносить в базу
    private Queue<Log> logQueue; // Очередь логов за сеанс
    private Integer max_count = 10; // Максимальное количество логов для хранения в накопителе, при превышении сброс логов в базу
    private Integer count = 0; // Текущее количество логов в накопителе

    public StaticticLogger() {
        this.logQueue = new ArrayBlockingQueue<Log>(max_count + 1);
    }

    // Добавление в конец внутренней очереди
    public void add(Log log) throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        this.logQueue.add(log);
        this.count ++;
        System.out.println("-----------> Logs size = " + count);
        // Проверяем, не пора ли переносить в базу:
        if (this.count == this.max_count){
            this.loadToDB();
        }
    }

    public void add(Integer logType)  {
        String name = Log.convert(logType);
        String date = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format( new java.util.Date());
        Log log = new Log(logType, name, date);
        try {
            this.add(log);
        } catch (InvocationTargetException | SQLException | IllegalAccessException | ParseException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void add(Integer logType, String info){
        String name = Log.convert(logType);
        String date = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format( new java.util.Date());
        Log log = new Log(logType, name, date, info);
        try {
            this.add(log);
        } catch (InvocationTargetException | SQLException | IllegalAccessException | ParseException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public void add(Integer logType, Integer id){
        String name = Log.convert(logType);
        String date = new java.text.SimpleDateFormat("dd.MM.yyyy HH:mm").format( new java.util.Date());
        Log log = new Log(logType, name, date, id);
        try {
            this.add(log);
        } catch (InvocationTargetException | SQLException | IllegalAccessException | ParseException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    // Получение из начала внутренней очереди (с удалением)
    synchronized public Log remove(){
        Log log = this.logQueue.remove();
        this.count --;
        return log;
    }

    // Перенос в базу: // В т.ч. и принудительный сброс командой извне

    public void loadToDB() throws SQLException, NoSuchMethodException, IllegalAccessException, ParseException, InvocationTargetException {
        System.out.print("Старт записи логов в базу (count = "+count+"): ");
        int i = 0;
        while (count > 0){
        //for (int i = 0; i < count; i++){
            i++;
            Log log = this.remove();
            System.out.print(i + " ");
            // А тут надо перенести в базу
            // Конвертируем в датаобджект:
            DataObject dataObject = new Converter().toDO(log);
            // Генерируем айдишник и вставляем в датаобджект:
            //dataObject.setId(new DBHelp().generationID(1008));
            // и переносим в базу
            new DBHelp().setDataObjectToDB(dataObject);

        }
        System.out.println("\n Конец записи логов в базу.");

    }

    @Scheduled(fixedDelay = 10000)
    public void tictuck() throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        loadToDB();
    }
}
