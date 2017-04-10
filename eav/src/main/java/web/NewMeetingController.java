package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.meetings.NewMeetingManager;
import service.meetings.NewMeetingRequest;
import service.meetings.NewMeetingResponce;
import service.optimizer.SlotManager;
import service.optimizer.SlotRequest;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 09.04.2017.
 */
// Класс-контроллер для работы со встречами (для новых встреч с плавающими границами и пр)
@Controller
public class NewMeetingController {

    // 1) На подгрузку страницы добавления новой встречи:
    @RequestMapping(value = "/newMeeting", method = RequestMethod.GET)
    public String newMeetingPage() throws SQLException {
        return "newMeeting";
    }

    // 2) К запросу на формирование новой встречи
    @RequestMapping(value = "/addNewMeeting", method = RequestMethod.POST)
    public String addNewMeeting(@RequestParam("title") String title,
                                @RequestParam("date_start") String date_start,
                                @RequestParam("date_end") String date_end,
                                @RequestParam("info") String info,
                                @RequestParam("tag") String tag) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {
        Integer id = new NewMeetingManager().setNewMeeting(title, date_start, date_end, null, info, tag, null); // Не знаю, нужен ли нам будет этот айдишник

        return "redirect:/meetings";
    }

    // 3) К запросу на формирование новой встречи с плавающими границами
    @RequestMapping(value = "/addNewFloatingMeeting", method = RequestMethod.POST)
    public String addNewFloatingMeeting(@RequestParam("title") String title,
                                @RequestParam("date_start") String date_start,
                                @RequestParam("date_end") String date_end,
                                @RequestParam("date_edit") String date_edit,
                                @RequestParam("info") String info,
                                @RequestParam("tag") String tag,
                                @RequestParam("duration") String duration) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {
        Integer id = new NewMeetingManager().setNewMeeting(title, date_start, date_end, date_edit, info, tag, duration);

        return "redirect:/meetings";
    }


}
