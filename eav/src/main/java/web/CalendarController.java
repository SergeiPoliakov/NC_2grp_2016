package web;

import entities.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import service.UserServiceImp;
import service.calendar.CalendarService;
import service.calendar.CalendarSynhronizer;
import service.statistics.StaticticLogger;

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
    // Собственный логгер для контроллера
    private StaticticLogger logger = new StaticticLogger();
    private UserServiceImp userService = new UserServiceImp();

    public CalendarController() throws IOException {
    }

    // Подключение календаря к приложению
    @RequestMapping(value = "/addCalendar", method = RequestMethod.GET)
    public String addCalendar() throws GeneralSecurityException, SQLException, IOException {
        CalendarService.authorize();
        // Логируем:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        logger.add(Log.ADD_CALENDAR, "addCalendar", idUser); // Добавление календаря
        return "redirect:/profile";
    }

    // Синхронизация календаря текущего юзера
    @RequestMapping(value = "/synchronizeCalendar", method = RequestMethod.GET)
    public String synchronizeCalendar()throws GeneralSecurityException, SQLException, IOException, ParseException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // Проверка работы календаря (синхронизация)
        new CalendarSynhronizer().synhronizedCurrentUser("primary");
        // Логируем:
        int idUser = userService.getObjID(userService.getCurrentUsername());
        logger.add(Log.SYNCHRONIZED_CALENDAR, "synchronizeCalendar", idUser);
        return "redirect:/profile";
    }
}
