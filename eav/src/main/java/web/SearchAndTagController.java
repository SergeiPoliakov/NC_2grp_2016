package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.search.FinderLogic;
import service.search.FinderTagRequest;
import service.search.FinderTagResponse;

import java.util.ArrayList;

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


}
