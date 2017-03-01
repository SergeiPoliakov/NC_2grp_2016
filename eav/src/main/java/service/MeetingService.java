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
    ArrayList<Meeting> getUserMeetingsList(int userID) throws SQLException;


    @WebMethod
    void setUsersToMeeting(int meetingID, String... userIDs) throws SQLException;

    @WebMethod
    void removeUsersFromMeeting(String meetingID, String... userIDs) throws SQLException;

}
