package service;

import entities.DataObject;
import entities.Event;
import entities.User;
import exception.CustomException;
import org.springframework.mail.MailException;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Lawrence on 08.02.2017.
 */

@WebService(name = "UserService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
interface UserService {

    @WebMethod
    String getCurrentUsername();

    @WebMethod
    int getObjID(String username) throws SQLException;

    @WebMethod
    User getCurrentUser() throws SQLException;

    @WebMethod
    ArrayList<User> getFriendListCurrentUser() throws SQLException;

    @WebMethod
    ArrayList<Object> getEmail(String email) throws SQLException;

    @WebMethod
    String generateEmailToken(int length);

    @WebMethod
    String generatePhoneToken();

    @WebMethod
    void setFriend(int idFriend) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException;

    @WebMethod
    void deleteFriend(int idFriend) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException;

    @WebMethod
    int generationID(int objTypeID) throws SQLException;

    @WebMethod
    void fittingEmail(String type, Integer fromID, Integer toID) throws MailException, UnsupportedEncodingException,
            MessagingException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException;

    @WebMethod
    void sendEmail (MimeMessage message);

    @WebMethod
    public void sendSmS(String type, Integer fromID, Integer toID) throws ExecutionException;
}


