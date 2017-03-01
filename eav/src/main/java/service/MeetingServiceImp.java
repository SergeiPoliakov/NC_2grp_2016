package service;

import dbHelp.DBHelp;
import entities.DataObject;
import entities.Meeting;
import entities.User;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Lawrence on 08.02.2017.
 */

public class MeetingServiceImp implements MeetingService {

    private static volatile MeetingServiceImp instance;

    public static MeetingServiceImp getInstance() {
        if (instance == null)
            synchronized (MeetingServiceImp.class) {
                if (instance == null)
                    instance = new MeetingServiceImp();
            }
        return instance;
    }

    // Получение списка всех существующих встреч


    // Получение списка всех существующих встреч конкретного пользователя
    public ArrayList<Meeting> getUserMeetingsList(int userID) throws SQLException {
        return new DBHelp().getUserMeetingsList(userID);
    }

    // Добавление встречи


    // Обновление встречи, meeting - обновленные данные события (ИДЕЯ С ОБЪЕКТАМИ ПОХОДУ ХУЙНЯ)


    // Получение встречи
    public Meeting getMeeting(Integer meetingID) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        return new Meeting(new DBHelp().getObjectsByIdAlternative(meetingID));
    }

    // Добавление пользователей на встречу
    public void setUsersToMeeting(int meetingID, String... userIDs) throws SQLException {
        new DBHelp().setUsersToMeeting(meetingID, userIDs);
    }

    // Удаление пользователей со встречи
    public void removeUsersFromMeeting(String meetingID, String... userIDs) throws SQLException {
        new DBHelp().removeUsersFromMeeting(meetingID, userIDs);
    }



    // Просмотр участников встречи
    public ArrayList<User> getUsersAtMeeting(int meetingID) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException, SQLException, NullPointerException {
        return new DBHelp().getUsersAtMeeting(meetingID);
    }

    // Получение конкретной встречи

    // Получение конкретной с пользователями


    public boolean isMeetingMember(int userID, Meeting meeting){
        for (User usr:meeting.getUsers()) {
            if (usr.getId() == userID)
                return true;
        }
        return false;
    }

}
