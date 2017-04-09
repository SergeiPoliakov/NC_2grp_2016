package service.meetings;

import entities.DataObject;
import entities.Event;
import entities.Meeting;
import entities.User;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.converter.Converter;
import service.id_filters.EventFilter;
import service.optimizer.Slot;
import service.optimizer.SlotRequest;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 09.04.2017.
 */
// Класс для работы со встречами (с новыми по структуре встречами)
public class NewMeetingManager {

    private UserServiceImp userService = new UserServiceImp();

    private LoadingServiceImp loadingService = new LoadingServiceImp();

    // 1) Метод для создания новой встречи
    public NewMeetingResponce setNewMeeting(NewMeetingRequest meetingRequest) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException {
        return setNewMeeting(meetingRequest.getTitle(), meetingRequest.getDate_start(),
                meetingRequest.getDate_end(), meetingRequest.getDate_edit(),
                meetingRequest.getInfo(), meetingRequest.getTag(), meetingRequest.getDuration());
    }

    // 2) Метод для создания новой встречи
    public NewMeetingResponce setNewMeeting(String title, String date_start, String date_end, String date_edit, String info, String tag, String duration) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException {
        // Создаем новую встречу
        Meeting meeting = new Meeting(title, date_start, date_end, date_edit, info, userService.getCurrentUser(), new StringBuilder(tag), null, duration);
        // Конвертируем в датаобджект:
        DataObject dataObject = meeting.toDataObject();
        // Cохраняем в базу и получаем заодно айдишник новой встречи:
        Integer id = loadingService.setDataObjectToDB(dataObject);
        return new NewMeetingResponce(id);
    }
}
