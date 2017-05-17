package web;

import com.google.common.cache.LoadingCache;
import entities.DataObject;
import entities.Log;
import entities.Meeting;
import entities.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.converter.Converter;
import service.search.FinderLogic;
import service.search.FinderTagRequest;
import service.search.FinderTagResponse;
import service.statistics.StatisticLogger;
import service.tags.TagTreeManager;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 29.03.2017.
 */
// Класс-контроллер для поиска по тегам юзеров, встреч, работы с динамическим поиском, выводом реультатов поиска на страницу, добавление и удаления тегов
@Controller
public class SearchAndTagController {

    // Внутренний логгер для контроллера
    private StatisticLogger loggerLog = new StatisticLogger();

    private UserServiceImp userService = new UserServiceImp();

    private TagTreeManager tagTreeManager = new TagTreeManager();

    private Converter converter = new Converter();

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();

    public SearchAndTagController() throws IOException {
    }

    private ArrayList<DataObject> getListDataObject(Map<Integer, DataObject> map) {
        ArrayList<DataObject> list = new ArrayList<>();
        for(Map.Entry<Integer, DataObject> e : map.entrySet()) {
            list.add(e.getValue());
        }
        return list;
    }

    // На подгрузку страницы поиска:
    @RequestMapping(value = "/search", method = RequestMethod.GET)
    public String searchPage() throws SQLException {
        // Логирование
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "search", idUser); // Посещение страницы поиска
        return "search";
    }

    // 2017-03-30 На запрос по тегу и поиск тега (предварительный поиск)
    @RequestMapping(value = "/getTags", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    @ResponseBody
    public ArrayList<FinderTagResponse> getTags(@RequestBody FinderTagRequest finder) throws SQLException {
        // Логирование
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.FIND_TAG, finder.getText(), idUser); // Поиск тегов (запишем поисковую строку)
        // Запускаем логику обработки запроса и выбора подходящих тегов:

        if (finder.getType().equals("name")) finder.setType("pre_name"); // чтобы в методе FinderLogic.getWithLogic(finder) различать предварительный и окончательный поиск
        return FinderLogic.getWithLogic(finder);
    }


    @RequestMapping(value = "/search/{tag}", method = RequestMethod.GET)
    public String getTag(@PathVariable ("tag") String tag,
                         ModelMap m) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        FinderTagRequest finder = new FinderTagRequest();
        finder.setType("meeting");
        finder.setOperation("or");
        finder.setText(tag);
        // Логирование
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.FIND_TAG, finder.getText(), idUser); // Поиск тегов (запишем поисковую строку)
        // Запускаем логику обработки запроса и выбора подходящих тегов:

        ArrayList<FinderTagResponse> finderTagResponseList = FinderLogic.getWithLogic(finder);
        Set<Integer> meetingsID = new HashSet<>();
        ArrayList<Integer> meetingsListWithTag;

        assert finderTagResponseList != null;
        for (FinderTagResponse tagValue : finderTagResponseList
                ) {

            String value = tagValue.getText();
            meetingsListWithTag = tagTreeManager.getMeetingListWithTag(value);
            meetingsID.addAll(meetingsListWithTag);
        }

        try {
            DataObject dataObjectUser = doCache.get(userService.getObjID(userService.getCurrentUsername()));
            User user = converter.ToUser(dataObjectUser);
            Map<Integer, DataObject> map = doCache.getAll(meetingsID);
            ArrayList<DataObject> list = getListDataObject(map);
            ArrayList<Meeting> meetings = new ArrayList<>(list.size());
            for (DataObject dataObject : list) {
                Meeting meeting = new Meeting(dataObject);
                meetings.add(meeting);
            }

            m.put("meetings", meetings);
            m.addAttribute("user", user);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return "/meetings";
    }

    // 2017-03-30 Получаем тег и ищем подходящих юзеров (или встречи)
    @RequestMapping(value = "/getFind", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    public String getFind(@RequestBody FinderTagRequest finder,
                          HttpServletRequest request) throws SQLException {
        // А тут к нам пришли все нужные параметры, которые достаем и можем использовать для логики поиска, а потом подготовить список и отдать на какую-то сраницу
        // Логирование
        int idUser = userService.getObjID(userService.getCurrentUsername());

        if (finder.getType().equals("name") & finder.getText().length() < 1) finder.setType("pre_name");
        //if(finder.getText() == null) finder.setText("zzzzzzz");

        if (finder.getType().equals("user")){
            loggerLog.add(Log.FIND_USER, finder.getText(), idUser); // Поиск юзера (запишем строку с именем из поиска)
        }
        else if (finder.getType().equals("meeting")){
            loggerLog.add(Log.FIND_MEETING, finder.getText(), idUser); // Поиск встречи (запишем строку с именем из поиска)
        }

        HttpSession session = request.getSession();
        session.setAttribute("finder", finder);

        return "search";
    }


}

