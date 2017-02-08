package service;

import entities.Meeting;
import entities.User;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Lawrence on 08.02.2017.
 */

@WebService(name = "MeetingService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
interface MeetingService {

    @WebMethod
    ArrayList<Meeting> getAllMeetingsList() throws SQLException;

    @WebMethod
    ArrayList<Meeting> getUserMeetingsList(int userID) throws SQLException;

    @WebMethod
    void setMeeting(Meeting meeting) throws SQLException;

    @WebMethod
    void updateEvent(String meetingID, Meeting newmeeting) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException;

    @WebMethod
    void setUsersToMeeting(String meetingID, String... userIDs) throws SQLException;

    @WebMethod
    void removeUsersFromMeeting(String meetingID, String... userIDs) throws SQLException;

    @WebMethod
    ArrayList<User> getUsersAtMeeting(String meetingID) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException, SQLException, NullPointerException;

    @WebMethod
    Meeting getMeeting(int meetingID) throws SQLException;


}
