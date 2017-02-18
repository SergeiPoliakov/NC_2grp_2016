package service;

import dbHelp.DBHelp;
import entities.Message;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Lawrence on 08.02.2017.
 */
public class MessageServiceImp implements MessageService {

    private static volatile MessageServiceImp instance;

    public static MessageServiceImp getInstance() {
        if (instance == null)
            synchronized (MessageServiceImp.class) {
                if (instance == null)
                    instance = new MessageServiceImp();
            }
        return instance;
    }

    // Метод добавления сообщения со всеми его атрибутами (2017-02-04)
    public void setNewMessage(int ObjTypeID, TreeMap<Integer, Object> massAttr) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        new DBHelp().setNewMessage(ObjTypeID, massAttr);
    }

    // Удаление сообщения:
    public void deleteMessage(Integer messageId) throws IllegalAccessException,
            IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {
        new DBHelp().deleteMessage(messageId);
    }

    // Получение ВСЕХ соообщений данного пользователя
    public ArrayList<Message> getMessageList(int from_id, int to_id) throws SQLException {
        return new DBHelp().getMessageList(from_id, to_id);
    }

}
