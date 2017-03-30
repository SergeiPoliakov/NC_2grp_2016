package web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.search.FinderTagRequest;
import service.search.FinderTagResponse;
import service.tags.TagTreeManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Hroniko on 29.03.2017.
 */
// Класс-контроллер для поиска по тегам юзеров, встреч, работы с динамическим поиском, выводом реультатов поиска на страницу, добавление и удаления тегов
@Controller
public class SearchAndTagController {


    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchPage() {
        return "search";
    }

    // 2017-03-30 Запрашиваем теги
    @RequestMapping(value = "/getTags", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    @ResponseBody
    public ArrayList<FinderTagResponse> getTags2(@RequestBody FinderTagRequest finder) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        if (finder.getType().equals("user")){
            // работаем с юзерами
        }
        else if (finder.getType().equals("meeting")){
            // работаем со встречами
        }

        ArrayList<FinderTagResponse> finderTagResponseList = new ArrayList<>();
        System.out.println("Пришел запрос на тег [" + finder.getText() + "]");




        TagTreeManager ttm = new TagTreeManager();
        ArrayList<String> anyTag = ttm.getTagWordListForUser(finder.getText());

        for (int i = 0; i < anyTag.size(); i++){
            FinderTagResponse finderTagResponse = new FinderTagResponse();
            finderTagResponse.setId(i);
            finderTagResponse.setText(anyTag.get(i));
            finderTagResponseList.add(finderTagResponse);
        }



        return finderTagResponseList;
    }



}
