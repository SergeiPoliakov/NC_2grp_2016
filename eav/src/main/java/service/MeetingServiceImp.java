package service;

import dbHelp.DBHelp;
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
            synchronized (DBHelp.class) {
                if (instance == null)
                    instance = new MeetingServiceImp();
            }
        return instance;
    }

    // Получение списка всех существующих встреч
    public ArrayList<Meeting> getAllMeetingsList() throws SQLException {
        return new DBHelp().getAllMeetingsList();
    }

    // Получение списка всех существующих встреч конкретного пользователя
    public ArrayList<Meeting> getUserMeetingsList(int userID) throws SQLException {
        return new DBHelp().getUserMeetingsList(userID);
    }

    // Добавление встречи
    public void setMeeting(Meeting meeting) throws SQLException {
        new DBHelp().setMeeting(meeting);
    }

    // Обновление встречи, meeting - обновленные данные события (ИДЕЯ С ОБЪЕКТАМИ ПОХОДУ ХУЙНЯ)
    public void updateEvent(String meetingID, Meeting newmeeting) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        new DBHelp().updateEvent(meetingID, newmeeting);
    }

    // Добавление пользователей на встречу
    public void setUsersToMeeting(int meetingID, String... userIDs) throws SQLException {
        new DBHelp().setUsersToMeeting(meetingID, userIDs);
    }

    // Удаление пользователей со встречи
    public void removeUsersFromMeeting(String meetingID, String... userIDs) throws SQLException {
        new DBHelp().removeUsersFromMeeting(meetingID, userIDs);
    }

    @Override
    public ArrayList<User> getUsersAtMeeting(String meetingID) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException, NullPointerException {
        return null;
    }


    // Просмотр участников встречи
    public ArrayList<User> getUsersAtMeeting(int meetingID) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException, SQLException, NullPointerException {
        return new DBHelp().getUsersAtMeeting(meetingID);
    }

    // Получение конкретной встречи
    public Meeting getMeeting(int meetingID) throws SQLException {
        return new DBHelp().getMeeting(meetingID);
    }

    // Получение конкретной с пользователями
    public Meeting getMeetingWithUsers(int meetingID) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Meeting meeting = new DBHelp().getMeeting(meetingID);
        meeting.setUsers(new DBHelp().getUsersAtMeeting( meetingID));
        meeting.setEvents(new DBHelp().getEventList(meetingID));
        return meeting;
    }

}
