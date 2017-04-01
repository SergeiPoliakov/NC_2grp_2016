package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import service.statistics.StatRequest;
import service.statistics.StatResponse;

import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Hroniko on 31.03.2017.
 */
// Класс-контроллер для работы со статистиками (подготовка и выдача на страницу данных для диаграмм)
@Controller
public class StatisticController {



    // На подгрузку страницы статистик:
    @RequestMapping(value = "/statistics", method = RequestMethod.GET)
    public String statPage() throws SQLException {

        return "statistics";
    }

    // 2017-03-31 К запросу на получение данных для отрисовки диаграмм и графиков (например, интенсивности работы (по дням, часам, минутам))
    @RequestMapping(value = "/getStat", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    @ResponseBody
    public ArrayList<StatResponse> getStat(@RequestBody StatRequest statRequest) throws SQLException {

        // Запускаем логику обработки статистики и формирования массива ответов StatResponse:
        ArrayList<StatResponse> results = new ArrayList<>(); // Пока просто заглушка

        if (statRequest.getPlotview().equals("plot")){ // Тест - отправляем на страницу статистик данные для графика нагрузки
            results.add(new StatResponse(0d, 0d));
            results.add(new StatResponse(1d, 10d));
            results.add(new StatResponse(2d, 23d));
            results.add(new StatResponse(3d, 17d));
            results.add(new StatResponse(4d, 18d));
            results.add(new StatResponse(5d, 9d));
            results.add(new StatResponse(6d, 11d));
            results.add(new StatResponse(7d, 27d));
            results.add(new StatResponse(8d, 33d));
            results.add(new StatResponse(9d, 40d));
            results.add(new StatResponse(10d, 32d));
            results.add(new StatResponse(11d, 35d));
            results.add(new StatResponse(12d, 30d));
            results.add(new StatResponse(13d, 40d));
            results.add(new StatResponse(14d, 42d));
            results.add(new StatResponse(15d, 47d));
            results.add(new StatResponse(16d, 44d));
            results.add(new StatResponse(17d, 48d));
            results.add(new StatResponse(18d, 52d));
            results.add(new StatResponse(19d, 54d));
            results.add(new StatResponse(20d, 42d));
            results.add(new StatResponse(21d, 55d));
            results.add(new StatResponse(22d, 56d));
            results.add(new StatResponse(23d, 57d));
            results.add(new StatResponse(24d, 60d));
            results.add(new StatResponse(25d, 50d));
            results.add(new StatResponse(26d, 52d));
            results.add(new StatResponse(27d, 51d));
            results.add(new StatResponse(28d, 49d));
            results.add(new StatResponse(29d, 53d));
            results.add(new StatResponse(30d, 55d));
            results.add(new StatResponse(31d, 60d));
            results.add(new StatResponse(32d, 61d));
            results.add(new StatResponse(33d, 59d));
            results.add(new StatResponse(34d, 62d));
            results.add(new StatResponse(35d, 65d));
            results.add(new StatResponse(36d, 62d));
            results.add(new StatResponse(37d, 58d));
            results.add(new StatResponse(38d, 55d));
            results.add(new StatResponse(39d, 61d));
            results.add(new StatResponse(40d, 64d));
            results.add(new StatResponse(41d, 65d));
            results.add(new StatResponse(42d, 63d));
            results.add(new StatResponse(43d, 66d));
            results.add(new StatResponse(44d, 67d));
            results.add(new StatResponse(45d, 69d));
            results.add(new StatResponse(46d, 69d));
            results.add(new StatResponse(47d, 70d));
            results.add(new StatResponse(48d, 72d));
            results.add(new StatResponse(49d, 68d));
            results.add(new StatResponse(50d, 66d));
            results.add(new StatResponse(51d, 65d));
            results.add(new StatResponse(52d, 67d));
            results.add(new StatResponse(53d, 70d));
            results.add(new StatResponse(54d, 71d));
            results.add(new StatResponse(55d, 72d));
            results.add(new StatResponse(56d, 73d));
            results.add(new StatResponse(57d, 75d));
            results.add(new StatResponse(58d, 70d));
            results.add(new StatResponse(59d, 68d));
            results.add(new StatResponse(60d, 64d));
            results.add(new StatResponse(61d, 60d));
            results.add(new StatResponse(62d, 65d));
            results.add(new StatResponse(63d, 67d));
            results.add(new StatResponse(64d, 68d));
            results.add(new StatResponse(65d, 69d));
            results.add(new StatResponse(66d, 70d));
            results.add(new StatResponse(67d, 72d));
            results.add(new StatResponse(68d, 75d));
            results.add(new StatResponse(69d, 80d));
        }
        else if (statRequest.getPlotview().equals("round")){ // Тест - отправляем на страницу статистик данные для круговой диаграммы
            results.add(new StatResponse("Общие встречи", 0.68));
            results.add(new StatResponse("Принятые встречи", 0.21));
            results.add(new StatResponse("Отказы", 0.11));
        }

        return results;
    }

}
