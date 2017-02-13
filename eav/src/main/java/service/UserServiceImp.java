package service;

import dbHelp.DBHelp;
import entities.DataObject;
import entities.Event;
import entities.User;
import exception.CustomException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Lawrence on 08.02.2017.
 */

public class UserServiceImp implements UserService {

    private static volatile UserServiceImp instance;

    public static UserServiceImp getInstance() {
        if (instance == null)
            synchronized (DBHelp.class) {
                if (instance == null)
                    instance = new UserServiceImp();
            }
        return instance;
    }

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }

    public ArrayList<User> searchUser(String name) throws SQLException {
        return new DBHelp().searchUser(name);
    }

    public int getObjID(String username) throws SQLException {
        return new DBHelp().getObjID(username);
    }

    // Получение всех пользователей
    public ArrayList<Object> getObjectsIDbyObjectTypeID(int ObjectTypeID) throws SQLException {
        return new DBHelp().getObjectsIDbyObjectTypeID(ObjectTypeID);
    }

    // Получение всех пользователей
    public ArrayList<User> getUserList() throws SQLException {
        return new DBHelp().getUserList();
    }

    // Получение объекта текущего авторизованного пользователя
    public User getCurrentUser() throws SQLException {
        return new DBHelp().getCurrentUser();
    }

    // Получение объекта одного конкретного пользователя по id этого пользователя
    public User getUserByUserID(int userID) throws SQLException {
        return new DBHelp().getUserByUserID(userID);
    }

    //Получения пользователя с его событиями
    public User getUserAndEventByUserID(int userID) throws SQLException {
        return new DBHelp().getUserAndEventByUserID(userID);
    }

    // Удаление пользователя
    public void deleteObject(Integer ID) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {
        new DBHelp().deleteObject(ID);
    }


    // Добавление нового пользователя
    public void setNewUser(DataObject dataObject) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, CustomException {
         if (new DBHelp().getEmail(dataObject.getParams().get(6)).isEmpty()) {
        new DBHelp().setNewUser(dataObject);
          } else {
          throw new CustomException("Пользователь с таким email'ом уже существует");
         }
    }

    // Обновление профиля пользователя
    public void updateUser(DataObject dataObject) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        new DBHelp().updateUser(dataObject);
    }

    // Добавление юзера в список друзей по его ID (2017-02-03) (испр. 2017-02-07)
    public void setFriend(int idFriend) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        new DBHelp().setFriend(idFriend);
    }

    // Удаление юзера из списка друзей по его ID (2017-02-07)
    public void deleteFriend(int idFriend) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        new DBHelp().deleteFriend(idFriend);
    }

    // Получение всех друзей текущего пользователя (2017-02-07)
    public ArrayList<User> getFriendListCurrentUser() throws SQLException {
        return new DBHelp().getFriendListCurrentUser();
    }

    // Получение всех друзей пользователя по его id (2017-02-07)
    public ArrayList<User> getFriendListByUserId(int userID) throws SQLException {
        return new DBHelp().getFriendListByUserId(userID);
    }

    public int generationID(int objTypeID) throws SQLException {
        return new DBHelp().generationID(objTypeID);
    }
}
