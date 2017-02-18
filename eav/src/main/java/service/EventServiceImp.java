package service;

import dbHelp.DBHelp;
import entities.DataObject;
import entities.Event;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Lawrence on 08.02.2017.
 */
public class EventServiceImp implements EventService {

    private static volatile EventServiceImp instance;

    public static EventServiceImp getInstance() {
        if (instance == null)
            synchronized (EventServiceImp.class) {
                if (instance == null)
                    instance = new EventServiceImp();
            }
        return instance;
    }

    // Получение ВСЕХ событий данного пользователя
    public ArrayList<DataObject> getEventList(int ObjectID) throws SQLException {
        return new DBHelp().getEventList(ObjectID);
    }

    // Удаление события:
    public void deleteEvent(Integer eventId) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {
        new DBHelp().deleteEvent(eventId);
    }

    // Метод добавления события со всеми его атрибутами
    public void setNewEvent(DataObject dataObject) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        new DBHelp().setNewEvent(dataObject);
    }

    // Обновление события
    public void updateEvent(DataObject dataObject) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
       // new  DBHelp().updateEvent(dataObject);
    }

    // Получение одного конкретного события данного пользователя по id этого события
    public Event getEventByEventID(int EventID) throws SQLException {
        return new DBHelp().getEventByEventID(EventID);
    }
}
