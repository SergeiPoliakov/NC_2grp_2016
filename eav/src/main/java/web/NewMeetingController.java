package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
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

    // На подгрузку страницы добавления новой встречи:
    @RequestMapping(value = "/newMeeting", method = RequestMethod.GET)
    public String newMeetingPage() throws SQLException {
        return "newMeeting";
    }

    // К запросу на формирование новой встречи
    @RequestMapping(value = "/addNewMeeting", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    @ResponseBody
    public NewMeetingResponce addNewMeeting(@RequestBody NewMeetingRequest meetingRequest) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {
        return new NewMeetingManager().setNewMeeting(meetingRequest);
    }



}
