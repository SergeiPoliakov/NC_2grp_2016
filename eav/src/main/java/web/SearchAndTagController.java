package web;

import com.google.common.cache.LoadingCache;
import entities.DataObject;
import entities.Log;
import entities.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.converter.Converter;
import service.id_filters.UserFilter;
import service.search.FinderLogic;
import service.search.FinderTagRequest;
import service.search.FinderTagResponse;
import service.search.SearchParser;
import service.tags.TagTreeManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 29.03.2017.
 */
// Класс-контроллер для поиска по тегам юзеров, встреч, работы с динамическим поиском, выводом реультатов поиска на страницу, добавление и удаления тегов
@Controller
public class SearchAndTagController {

    // На подгрузку страницы поиска:
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchPage() {
        return "search";
    }

    // 2017-03-30 На запрос по тегу и поиск тега
    @RequestMapping(value = "/getTags", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    @ResponseBody
    public ArrayList<FinderTagResponse> getTags(@RequestBody FinderTagRequest finder) {
        // Запускаем логику обработки запроса и выбора подходящих тегов:
        return FinderLogic.getWithLogic(finder);
    }

    // 2017-03-30 Получаем тег и ищем подходящих юзеров (или встречи)
    @RequestMapping(value = "/getFind", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    public String getFind(@RequestBody FinderTagRequest finder,
                          Map<String, Object> mapObjects,
                          HttpServletRequest request) {
        // А тут к нам пришли все нужные параметры, которые достаем и можем испольовать для логики поиска, а потмо подготовить список и отдать на какую-то сраницу


        if (finder.getText() != null) {
            HttpSession session = request.getSession();
            session.setAttribute("allUsers", finder);
        }

        return "search";
    }


}

