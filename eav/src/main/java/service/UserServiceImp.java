package service;

import dbHelp.DBHelp;
import entities.DataObject;
import entities.User;
import org.springframework.mail.MailException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Lawrence on 08.02.2017.
 */

public class UserServiceImp implements UserService {

    private static volatile UserServiceImp instance;

    public static UserServiceImp getInstance() {
        if (instance == null)
            synchronized (UserServiceImp.class) {
                if (instance == null)
                    instance = new UserServiceImp();
            }
        return instance;
    }

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }



    public int getObjID(String username) throws SQLException {
        return new DBHelp().getObjID(username);
    }




    // Получение объекта текущего авторизованного пользователя
    public User getCurrentUser() throws SQLException {
        return new DBHelp().getCurrentUser();
    }






    // Получение всех друзей текущего пользователя (2017-02-07)
    public ArrayList<User> getFriendListCurrentUser() throws SQLException {
        return new DBHelp().getFriendListCurrentUser();
    }



    // Получение всех активных email
    public ArrayList<Object> getEmail(String email) throws SQLException {
        return new DBHelp().getEmail(email);
    }

    // Получение случайного токета для завершения регистрации
    public String generateToken(int length)
    {
        String characters = "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOASDFGHJKLZXCVBNM";
        Random rnd = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rnd.nextInt(characters.length()));
        }
        return new String(text);
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


    public int generationID(int objTypeID) throws SQLException {
        return new DBHelp().generationID(objTypeID);
    }

    public void sendEmail(String text, DataObject dataObject) throws MailException {
        //TODO: Здесь будем отправлять опощения пользователю.
    }
}
