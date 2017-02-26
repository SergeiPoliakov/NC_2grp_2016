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
    public ArrayList<Meeting> getAllMeetingsList() throws SQLException {
        return new DBHelp().getAllMeetingsList();
    }

    // Получение списка всех существующих встреч конкретного пользователя
    public ArrayList<Meeting> getUserMeetingsList(int userID) throws SQLException {
        return new DBHelp().getUserMeetingsList(userID);
    }

    // Добавление встречи
    public void setMeeting(Meeting meeting) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        //new DBHelp().setMeeting(meeting);
        meeting.setId(0);

        ArrayList<User> users = new ArrayList<>();
        User user = new User();
        user.setId(meeting.getOrganizer().getId());
        users.add(user);
        meeting.setUsers(users);

        DataObject dataObject = meeting.toDataObject();
        new DBHelp().setDataObjectToDB(dataObject);
    }

    // Обновление встречи, meeting - обновленные данные события (ИДЕЯ С ОБЪЕКТАМИ ПОХОДУ ХУЙНЯ)
    public void updateMeeting(Integer meetingID, Meeting meeting) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

    }

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
      //  meeting.setEvents(new DBHelp().getEventList(meetingID));
        return meeting;
    }

    public boolean isMeetingMember(int userID, Meeting meeting){
        for (User usr:meeting.getUsers()) {
            if (usr.getId() == userID)
                return true;
        }
        return false;
    }

}
