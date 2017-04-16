package service.meetings;

import com.google.common.cache.LoadingCache;
import entities.DataObject;
import entities.Event;
import entities.Meeting;
import entities.User;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.converter.Converter;
import service.converter.DateConverter;
import service.id_filters.EventFilter;
import service.optimizer.Slot;
import service.optimizer.SlotRequest;
import service.search.SearchParser;
import service.tags.TagNodeTree;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 09.04.2017.
 */
// Класс для работы со встречами (с новыми по структуре встречами)
public class NewMeetingManager {

    private UserServiceImp userService = new UserServiceImp();
    private TagNodeTree tagNodeTree = TagNodeTree.getInstance();
    private LoadingServiceImp loadingService = new LoadingServiceImp();
    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();


    // 1) Метод для создания новой встречи
    public Integer setNewMeeting(String title, String date_start, String date_end, String date_edit, String info, String tag, String duration) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException {
        // Создаем новую встречу
        int id = userService.generationID(1004);

        StringBuilder worlds = new StringBuilder();

        if (!Objects.equals(tag, "")) {
            ArrayList<String> tags = SearchParser.parse(tag);
            assert tags != null;
            for (String value : tags
                    ) {
                tagNodeTree.insertForMeeting(value, id);
                worlds.append(value).append(" ");
            }
        } else worlds.append("встреча");

        Meeting meeting;
        if (duration == null) {
            long durationTime = DateConverter.duration(date_start, date_end);
            meeting = new Meeting(title, date_start, date_end, info, userService.getCurrentUser(), worlds, "", String.valueOf(durationTime));
        } else {
            meeting = new Meeting(title, date_start, date_end, info, userService.getCurrentUser(), worlds, "", String.valueOf(duration), date_edit);
        }

        ArrayList<User> users = new ArrayList<>();
        User user = new User();
        user.setId(meeting.getOrganizer().getId());
        users.add(user);
        meeting.setUsers(users);

        //добавляю дубликат
        Integer user_id = userService.getCurrentUser().getId();
        meeting.createDuplicate(user_id);

        DataObject dataObject = meeting.toDataObject();
        loadingService.setDataObjectToDB(dataObject);
        doCache.invalidate(id);

        return id;
    }
}
