package service;

import entities.Event;
import entities.User;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Lawrence on 08.02.2017.
 */

@WebService(name = "UserService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
interface UserService {

    @WebMethod
    String getCurrentUsername();

    @WebMethod
    ArrayList<User> searchUser(String name) throws SQLException;

    @WebMethod
    int getObjID(String username) throws SQLException;

    @WebMethod
    ArrayList<Object> getObjectsIDbyObjectTypeID(int ObjectTypeID) throws SQLException;

    @WebMethod
    ArrayList<User> getUserList() throws SQLException;

    @WebMethod
    User getCurrentUser() throws SQLException;

    @WebMethod
    User getUserByUserID(int userID) throws SQLException;

    @WebMethod
    User getUserAndEventByUserID(int userID) throws SQLException;


    @WebMethod
    void deleteObject(Integer ID) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException;


    @WebMethod
    void setNewUser(int ObjTypeID, String name, TreeMap<Integer, String> massAttr) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException;


    @WebMethod
    void updateUser(int ObjTypeID, String name, TreeMap<Integer, String> massAttr) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException;

    @WebMethod
    void setFriend(int idFriend) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException;

    @WebMethod
    void deleteFriend(int idFriend) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException;

    @WebMethod
    ArrayList<User> getFriendListCurrentUser() throws SQLException;

    @WebMethod
    ArrayList<User> getFriendListByUserId(int userID) throws SQLException;
}


