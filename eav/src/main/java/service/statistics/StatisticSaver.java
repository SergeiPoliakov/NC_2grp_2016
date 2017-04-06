package service.statistics;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Hroniko on 04.04.2017.
 */
// Класс для хранения обсчитанных статистик в памяти для сокращения колчества запросов в базу
// (т.е. тут будут готовые статистики, и если таймаут на их актуальность не вышел, забирать отсюда, а не из базы)
    // Можно еще повесть шедуллер на удаление просроченных статистик
public class StatisticSaver {

    private static volatile StatisticSaver instance;

    // 1) Мапа для хранения готовых статистик, и будет составной ключ
    public static final Map<String, ArrayList<StatResponse>> saveMap = new ConcurrentHashMap<>();

    // 2) Мапа для хранения времени создания готовых статистик, и будет составной ключ
    public static final Map<String, LocalDateTime> dateMap = new ConcurrentHashMap<>();


    public static StatisticSaver getInstance()  {
        if (instance == null)
            synchronized (StatisticSaver.class) {
                if (instance == null)
                    instance = new StatisticSaver();
            }
        return instance;
    }

    // Конструктор:
    public StatisticSaver() {

    }

    // Метод обавления статистики по составному ключу
    synchronized public static void add(Integer root_id, String plotview, String datatype, String period, ArrayList<StatResponse> statResponses){
        // Фиксируем текущее время загрузки в мапу нашей статистики:
        LocalDateTime add_date = LocalDateTime.now();
        // Создаем составной ключ для мапы:
        String key = root_id + "~" + plotview + "~" + datatype + "~" +period; // то-то типа 10003~plot~activity~day
        // Проверяем, есть ли такой ключ
        if (dateMap.get(key) != null){ // если есть, то удаляем из обеих мап
            dateMap.remove(key);
            saveMap.remove(key);
        }
        // А затем привешиваем к обеим мапам:
        dateMap.put(key, add_date);
        saveMap.put(key, statResponses);
    }

    // Метод получения статистики по запрсу и ответу
    public static void add(Integer root_id, StatRequest statRequest, ArrayList<StatResponse> statResponses){
        String plotview = statRequest.getPlotview();
        String datatype = statRequest.getDatatype();
        String period = statRequest.getPeriod();
        add(root_id, plotview, datatype, period, statResponses);
    }


    // Метод получения статистики по составному ключу
    synchronized public static ArrayList<StatResponse> get(Integer root_id, String plotview, String datatype, String period){
        // Фиксируем текущее время запроса из мапы статистики:
        LocalDateTime add_date = LocalDateTime.now();
        ArrayList<StatResponse> result = null;

        // Создаем составной ключ для мапы:
        String key = root_id + "~" + plotview + "~" + datatype + "~" +period; // то-то типа 10003~plot~activity~day
        // Проверяем, есть ли такой ключ

        if (dateMap.get(key) != null){ // если есть, то смотрим, не просроча ли статистика
            LocalDateTime old_date = dateMap.get(key);
            // Пусть у нас срок жизни статистики 10 минут (можно потом выставить в настройках как надо):
            LocalDateTime new_date = add_date.minusMinutes(10);
            if (new_date.isBefore(old_date)){ // Если от старой до новой вставки статистики не прошло еще 10 минут,
                // то можно смело отдать статистику из мапы:
                result = saveMap.get(key);
            }
            // наче останется нулл, его и отдадим
        }
        return result;
    }

    // Метод получения статистики по запросу
    public static ArrayList<StatResponse> get(Integer root_id, StatRequest statRequest){
        String plotview = statRequest.getPlotview();
        String datatype = statRequest.getDatatype();
        String period = statRequest.getPeriod();
        return get(root_id, plotview, datatype, period);
    }

    // Метод удаления просроченных статистик
    synchronized public static void removeOld(){
        // Фиксируем текущее время загрузки в мапу нашей статистики:
        LocalDateTime now_date = LocalDateTime.now();
        // Делаем смещение на 10 минут (можно прописать в настройках потом):
        LocalDateTime new_date = now_date.minusMinutes(10);
        // Обходим мапу дат и смотрим просроченные даты
        for (String key : dateMap.keySet()) {
            LocalDateTime old_date = dateMap.get(key);
            if (new_date.isAfter(old_date)){ // Если от старой до новой вставки статистики уже прошло 10 минут,
                // то можно удалить статистику из мапы:
                dateMap.remove(key);
                saveMap.remove(key);
            }
        }
    }

    // Собственно метод для шедуллера:
    public static void tictack() {
        removeOld();
    }


}
