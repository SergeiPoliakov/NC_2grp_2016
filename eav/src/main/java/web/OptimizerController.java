package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import service.optimizer.Slot;
import service.optimizer.SlotManager;
import service.optimizer.SlotRequest;
import service.statistics.StatRequest;
import service.statistics.StatResponse;
import service.statistics.StatisticManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 08.04.2017.
 */
// Класс-контроллер для работы с оптимизатором встреч
@Controller
public class OptimizerController {

    // На подгрузку страницы cвободных слотов:
    @RequestMapping(value = "/slots", method = RequestMethod.GET)
    public String slotsPage() throws SQLException {
        return "slots";
    }

    // К запросу на получение данных по свободным слотам
    @RequestMapping(value = "/getFreeSlots", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    @ResponseBody
    public ArrayList<Slot> getStat(@RequestBody SlotRequest slotRequest) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {
        return new SlotManager().getFreeSlots(slotRequest);
    }
}
