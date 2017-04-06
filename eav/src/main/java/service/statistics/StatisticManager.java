package service.statistics;


import com.google.api.client.util.DateTime;
import dbHelp.DBHelp;
import entities.DataObject;
import entities.Log;
import service.UserServiceImp;
import service.converter.Converter;
import service.id_filters.LogFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Hroniko on 12.03.2017.
 * Класс для формирования различных статистик на основе данных, полученных и сохраненных в базу регистратором статистик
 */
public class StatisticManager {
    // Скоро сделаю // 2017-03-31 получилось не очень скоро, но пора делать)
    private UserServiceImp userService = new UserServiceImp();


    public StatisticManager() {
    }

    // 2017-04-01 Конвертер даты из Java-8 строку
    public static String dateToString(LocalDateTime ldt) throws ParseException {
        //LocalDateTime sr = LocalDateTime.now().minusHours(1);
        String res = ldt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        //System.out.println(dd);
        return res;
    }

    // 2017-04-01 Конвертер строки в дату из Java-8
    public static LocalDateTime stringToDate(String str) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime res = LocalDateTime.parse(str, formatter);
        return res;
    }

    // Вид диаграммы: plot - график  | round - круговая диаграмма | settings - настройки
    public class PlotView{
        private static final String PLOT =  "plot"; // обычный график
        private static final String ROUND = "round"; // Круговая диаграмма
    }

    // Тип данных для диаграммы: activity - активность юзера за период | meeting - соотношение встреч | message - соотношение сообщений ...
    public static class DataType{
        private static final String ACTIVITY =  "activity"; // активность юзера за период
        private static final String MEETING =   "meeting"; // соотношение встреч
    }

    // Период выборки: hour - за последний час | day - за последний день | week - за последнюю неделю | month - за последний месяц | year - за последний год
    public class Period{
        private static final String HOUR =  "hour";
        private static final String DAY =   "day";
        private static final String WEEK =  "week";
        private static final String MONTH = "month";
        private static final String YEAR =  "year";
    }


    // Получаем запрос на формирование статистики (ОСНОВНОЙ метод):
    public ArrayList<StatResponse> getStatistic(StatRequest statRequest) throws SQLException, NoSuchMethodException, IllegalAccessException, ParseException, InvocationTargetException {

        // Получаем айди юзера
        Integer root_id = userService.getCurrentUser().getId();
        // Проверяем, есть ли такая статистика у нас в сейвере:
        ArrayList<StatResponse> results = StatisticSaver.get(root_id, statRequest);

        if (results == null){ // Если нет, то запускаем логику сбора логов из базы и формирования статистики:
            // Проверяем, какой механизм формирования статистики вызвать
            if (statRequest.getDatatype().equals(DataType.ACTIVITY)) {
                results = getActivity(statRequest);
            }
            else if (statRequest.getDatatype().equals(DataType.MEETING)) {
                results = getMeeting(statRequest);
            }
            // И конечно надо сохранить в сейвере результат - обсчитанную статистику:
            StatisticSaver.add(root_id, statRequest, results);
        }

        // И отдаем результат в контроллер:
        return results;
    }


    //// ------------------------------------------------------------------------------------

    // 1 Статистика: автивность юзера на сайте (можно, конечно, ввести весовую функцию, но пока просто считает общее время, проведенное на сайте, по отношению к периоду измерения)
    private ArrayList<StatResponse> getActivity(StatRequest statRequest) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        ArrayList<StatResponse> results = new ArrayList<>();

        // Определяем, от какого дня считать:
        LocalDateTime start_date; // = LocalDateTime.now().minusHours(1);
        long delta = 0; // Сдвиг по времени в минутах(дельта для интервалов)
        switch (statRequest.getPeriod()) {
            case Period.HOUR:
                start_date = LocalDateTime.now().minusHours(1);
                delta = 1; // на 60 частей по 1 минуте // Сдвиг (шаг) по времени в минутах (дельта для интервалов)
                break;
            case Period.DAY:
                start_date = LocalDateTime.now().minusDays(1);
                delta = 60; // на 24 части по 1 часу // Сдвиг (шаг) по времени в минутах (дельта для интервалов)
                break;
            case Period.WEEK:
                start_date = LocalDateTime.now().minusWeeks(1);
                delta = 60*24; // на 7 частей по одному дню // Сдвиг (шаг) по времени в минутах (дельта для интервалов)
                break;
            case Period.MONTH:
                start_date = LocalDateTime.now().minusMonths(1);
                delta = 60*24; // на 30 частей по одному дню // Сдвиг (шаг) по времени в минутах (дельта для интервалов)
                break;
            case Period.YEAR:
                start_date = LocalDateTime.now().minusYears(1);
                delta = 60*24*7*4; // а 12 частей по одному месяцу // Сдвиг (шаг) по времени в минутах (дельта для интервалов)
                break;
            default:
                start_date = LocalDateTime.now().minusHours(1);
                delta = 1; // Сдвиг (шаг) по времени в минутах (дельта для интервалов)
                break;
        }
        start_date = start_date.minusMinutes(delta); // делаем еще один сдвиг влево на дельту, чтобы не потерять еще один отсчет
        String date = dateToString(start_date);




        // Делаем запрос в базу и вытаскиваем нужные логи (все логи):
        ArrayList<Integer> ids = new DBHelp().getListObjectsByFilters(new LogFilter(LogFilter.FOR_CURRENT_USER, LogFilter.AFTER_DATE, date));
        ArrayList<DataObject> aldo = new DBHelp().getListObjectsByListIdAlternative(ids);

        // Конвертируем в логи:
        ArrayList<Log> logs = new Converter().ToLog(aldo);

        // Разбиваем временной интервал на подинтервалы (на 60)
        // запускаем цикл по всем логам:
        Double sum = 0.0;
        Double j = 0.0;
        Long sdvig = delta;
        LocalDateTime ldt2 = start_date.plusMinutes(sdvig);

        for (int i = 0; i < logs.size(); i++){

            LocalDateTime ldt = stringToDate(logs.get(i).getDate());

            if (ldt.isBefore(ldt2)){ // Если текущий лог произошел РАНЬШЕ чем старовая дата плос сдвиг
                sum++;
            }
            else{ // иначе записываем точку и переходим к другой
                // нормировать не стал, пока не привязался к базе
                results.add(new StatResponse(j, sum));
                j ++;
                sum = 0.0;
                // Увеличиваем смещение
                ldt2 = ldt2.plusMinutes(sdvig);
            }
        }



        return results;
    }

    // 2 Статистика: соотношение встреч юзера
    private ArrayList<StatResponse> getMeeting(StatRequest statRequest){
        ArrayList<StatResponse> results = new ArrayList<>(); // Пока просто заглушка


            results.add(new StatResponse("Общие встречи", 0.68));
            results.add(new StatResponse("Принятые встречи", 0.21));
            results.add(new StatResponse("Отказы", 0.11));


        return results;
    }




}
