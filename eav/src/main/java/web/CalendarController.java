package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import service.calendar.CalendarService;
import service.calendar.CalendarSynhronizer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created by Hroniko on 07.03.2017.
 * Контроллер для операций с гугл-календарем
 */
@Controller
public class CalendarController {

    // Подключение календаря к приложению
    @RequestMapping(value = "/addCalendar", method = RequestMethod.GET)
    public String addCalendar() throws GeneralSecurityException, SQLException, IOException {
        CalendarService.authorize();
        return "redirect:/profile";
    }

    // Синхронизация календаря текущего юзера
    @RequestMapping(value = "/synchronizeCalendar", method = RequestMethod.GET)
    public String synchronizeCalendar()throws GeneralSecurityException, SQLException, IOException, ParseException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // Проверка работы календаря (синхронизация)
        new CalendarSynhronizer().synhronizedCurrentUser("primary");
        return "redirect:/profile";
    }
}
