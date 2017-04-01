package web;

import entities.Log;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.UserServiceImp;
import service.search.FinderLogic;
import service.search.FinderTagRequest;
import service.search.FinderTagResponse;
import service.statistics.StaticticLogger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Hroniko on 29.03.2017.
 */
// Класс-контроллер для поиска по тегам юзеров, встреч, работы с динамическим поиском, выводом реультатов поиска на страницу, добавление и удаления тегов
@Controller
public class SearchAndTagController {

    // Внутренний логгер для контроллера
    private StaticticLogger loggerLog = new StaticticLogger();

    private UserServiceImp userService = new UserServiceImp();

    public SearchAndTagController() throws IOException {
    }

    // На подгрузку страницы поиска:
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchPage() throws SQLException {
        // Логирование
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "search", idUser); // Посещение страницы поиска
        return "search";
    }

    // 2017-03-30 На запрос по тегу и поиск тега
    @RequestMapping(value = "/getTags", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    @ResponseBody
    public ArrayList<FinderTagResponse> getTags(@RequestBody FinderTagRequest finder) throws SQLException {
        // Логирование
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.FIND_TAG, finder.getText(), idUser); // Поиск тегов (запишем поисковую строку)
        // Запускаем логику обработки запроса и выбора подходящих тегов:
        return FinderLogic.getWithLogic(finder);
    }

    // 2017-03-30 Получаем тег и ищем подходящих юзеров (или встречи)
    @RequestMapping(value = "/getFind", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    public String getFind(@RequestBody FinderTagRequest finder,
                          Map<String, Object> mapObjects,
                          HttpServletRequest request) throws SQLException {
        // А тут к нам пришли все нужные параметры, которые достаем и можем испольовать для логики поиска, а потмо подготовить список и отдать на какую-то сраницу
        // Логирование
        int idUser = userService.getObjID(userService.getCurrentUsername());
        if (finder.getType().equals("user")){
            loggerLog.add(Log.FIND_USER, finder.getText(), idUser); // Поиск юзера (запишем строку с именем из поиска)
        }
        else if (finder.getType().equals("meeting")){
            loggerLog.add(Log.FIND_MEETING, finder.getText(), idUser); // Поиск встречи (запишем строку с именем из поиска)
        }

        if (finder.getText() != null) {
            HttpSession session = request.getSession();
            session.setAttribute("finder", finder);
        }

        return "search";
    }


}


