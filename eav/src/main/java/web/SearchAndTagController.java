package web;

import dbHelp.DBHelp;
import entities.DataObject;
import entities.Message;
import entities.Tag;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import service.id_filters.MessageFilter;
import service.id_filters.NotificationFilter;
import service.id_filters.UserFilter;
import service.tags.TagTreeManager;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

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
    @RequestMapping(value = "/getTags", method = RequestMethod.GET)
    public @ResponseBody
    ArrayList<Tag> getTags(@RequestParam String text) { // text для проверки тут, какую именно инфу вернуть. ифы и ветвление по запросам ajax

        ArrayList<Tag> tagList = new ArrayList<>();
        System.out.println("Пришел запрос на тег [" + text + "]");

        TagTreeManager ttm = new TagTreeManager();
        ArrayList<String> anyTag = ttm.getTagWordListForUser(text);

        for (int i = 0; i < anyTag.size(); i++){
            Tag tag = new Tag();
            tag.setId(i);
            tag.setText(anyTag.get(i));
            tagList.add(tag);
        }


        /*
        Tag tag = new Tag();
        tag.setId(1);
        tag.setText("Вася");
        tagList.add(tag);

        Tag tag2 = new Tag();
        tag2.setId(2);
        tag2.setText("Петя");
        tagList.add(tag2);
        */

        return tagList;
    }



}
