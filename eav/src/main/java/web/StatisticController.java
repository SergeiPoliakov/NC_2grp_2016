package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.sql.SQLException;

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
}
