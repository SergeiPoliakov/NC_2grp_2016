package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import service.statistics.StatRequest;
import service.statistics.StatResponse;
import service.statistics.StatSetting;
import service.statistics.StatisticManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 31.03.2017.
 */
// Класс-контроллер для работы со статистиками (подготовка и выдача на страницу данных для диаграмм)
@Controller
public class StatisticController {


    // На подгрузку страницы статистик с карточками:
    @RequestMapping(value = "/statistic", method = RequestMethod.GET)
    public String statisticPage() throws SQLException {

        return "statistic";
    }

    // На подгрузку страницы статистик:
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public String statPage() throws SQLException {

        return "statistics";
    }

    // 2017-03-31 К запросу на получение данных для отрисовки диаграмм и графиков (например, интенсивности работы (по дням, часам, минутам))
    @RequestMapping(value = "/getStat", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    @ResponseBody
    public ArrayList<StatResponse> getStat(@RequestBody StatRequest statRequest) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {

        return new StatisticManager().getStatistic(statRequest);
    }



    // 2017-04-01 К запросу на получение настроек, какие данные для отрисовки диаграмм и графиков выставил юзер
    @RequestMapping(value = "/getStatSettings", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    @ResponseBody
    public ArrayList<StatSetting> getStatSettings(@RequestBody StatRequest statRequest) throws SQLException {

        ArrayList<StatSetting> results = new ArrayList<>(); // Пока просто заглушка

        if (statRequest.getPlotview().equals("settings")){ // Тест - отправляем на страницу статистик настройки
            // Эти настройки надо из настроек из базы вытаскивать

            // results.add(new StatSetting("plot", "on", "activity", "day", "location_2", "Время, часы", "Интенсивность работы"));
            results.add(new StatSetting("plot", "on", "activity", "day", "location_2","Интенсивность работы за сутки", "Время, часы", "Интенсивность работы"));

            results.add(new StatSetting("round", "on", "meeting", "day", "location_1", "Процентаж встреч за текущий месяц"));

            results.add(new StatSetting("round", "on", "procentazh", "month", "location_4", "Ваша активность за текущий месяц"));

        }

        return results;
    }

}
