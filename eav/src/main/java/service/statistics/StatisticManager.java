package service.statistics;


import com.google.api.client.util.DateTime;
import com.google.common.cache.LoadingCache;
import dbHelp.DBHelp;
import entities.DataObject;
import entities.Event;
import entities.Log;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.converter.Converter;
import service.converter.DateConverter;
import service.id_filters.EventFilter;
import service.id_filters.LogFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 12.03.2017.
 * Класс для формирования различных статистик на основе данных, полученных и сохраненных в базу регистратором статистик
 */
public class StatisticManager {
    // Скоро сделаю // 2017-03-31 получилось не очень скоро, но пора делать)
    private LoadingServiceImp loadingService = new LoadingServiceImp();
    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();
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
        private static final String PROCENTAZH = "procentazh"; // процентаж встреч / событий / свободного времени в виде круговой диаграммы
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
    public ArrayList<StatResponse> getStatistic(StatRequest statRequest) throws SQLException, NoSuchMethodException, IllegalAccessException, ParseException, InvocationTargetException, ExecutionException {

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
            else if (statRequest.getDatatype().equals(DataType.PROCENTAZH)) {
                results = getUsageAndFreeTime(statRequest);
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

    // 2017-05-08 Статистика № 3 Соотношение свободного и занятого времени (круговая диаграмма)
    private ArrayList<StatResponse> getUsageAndFreeTime(StatRequest statRequest) throws ParseException, SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, ExecutionException {

        // 1 Определяем, за какое время считать статистику:
        LocalDateTime start_date;
        LocalDateTime end_date = LocalDateTime.now();
        switch (statRequest.getPeriod()) {
            case Period.HOUR:
                start_date = end_date.minusHours(1);
                break;
            case Period.DAY:
                start_date = end_date.minusDays(1);
                break;
            case Period.WEEK:
                start_date = end_date.minusWeeks(1);
                break;
            case Period.MONTH:
                start_date = end_date.minusMonths(1);
                break;
            case Period.YEAR:
                start_date = end_date.minusYears(1);
                break;
            default:
                start_date = end_date.minusMonths(1);
                break;
        }

        // 2 Конвертируем даты начала и конца выборки в строку:
        String date_1 = DateConverter.dateToString(start_date);
        String date_2 = DateConverter.dateToString(end_date);


        // 3 Выбираем из базы все события за интересующий интервал в ArrayList<Event> events:
        int user_id = userService.getObjID(userService.getCurrentUsername());
        ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_CURRENT_USER, EventFilter.BETWEEN_TWO_DATES, date_1, date_2));
        Map<Integer, DataObject> map = doCache.getAll(il);
        ArrayList<DataObject> list = new Converter().getMapToListDataObject(map);
        ArrayList<Event> events = new ArrayList<>(list.size());
        for (DataObject dataObject : list) {
            Event event = new Event(dataObject);
            events.add(event);
        }

        // 4 Обходим все события и считаем суммарную длительность дубликатов встреч и обычных событий:
        Duration du_dublicates = Duration.between(end_date, end_date); // Подготавливаем нулевую длительность, к ней будем потом суммировать длительности дубликатов
        Duration du_events = Duration.between(end_date, end_date); // Подготавливаем нулевую длительность, к ней будем потом суммировать длительности обычных событий
        // и начинаем обходить:
        // Ищем место слева и справа:
        for (int j = 0; j < events.size(); j++) {
            Event event = events.get(j);
            // и вычисляем продолжительность
            Duration ev_duration = event.getDlitelnost();
            // проверяем, с каким типом события имеем дело:
            if (event.getPriority().equals(Event.PRIOR_DUPLICATE)) {
                // имеем дело с дубликатом встречи
                du_dublicates = du_dublicates.plus(ev_duration);
            } else {
                // иначе имеем дело с обычным событием
                du_events = du_events.plus(ev_duration);
            }
        }

        // 5 Вычисляем длительность свободного времени как разницу всего интересующего периода и длительностей всех событий:
        Duration du_all = Duration.between(start_date, end_date);
        Duration du_free = Duration.between(start_date, end_date);
        du_free = du_free.minus(du_dublicates);
        du_free = du_free.minus(du_events);

        // 6 Рассчитываем процентаж:
        Double pr_dublicates = 1.0 * du_dublicates.toMinutes() / du_all.toMinutes();
        Double pr_base_events = 1.0 * du_events.toMinutes()  / du_all.toMinutes();
        Double pr_free = 1.0 * du_free.toMinutes() / du_all.toMinutes();


        // 7 И переносим в responce:
        ArrayList<StatResponse> results = new ArrayList<>();

        /*
        results.add(new StatResponse("Встречи", 0.2));
        results.add(new StatResponse("Прочие события", 0.5));
        results.add(new StatResponse("Свободное время", 0.3));
        */

        results.add(new StatResponse("Встречи", pr_dublicates));
        results.add(new StatResponse("Прочие события", pr_base_events));
        results.add(new StatResponse("Свободное время", pr_free));





        return results;
    }




}
