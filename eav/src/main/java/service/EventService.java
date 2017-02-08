package service;

import entities.Event;

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

@WebService(name = "EventService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
interface EventService {

    @WebMethod
    ArrayList<Event> getEventList(int ObjectID) throws SQLException;

    @WebMethod
    void deleteEvent(Integer eventId) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException;


    @WebMethod
    void setNewEvent(int ObjTypeID, String name, TreeMap<Integer, Object> massAttr) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException;


    @WebMethod
    void updateEvent(int ObjID, String name, TreeMap<Integer, Object> massAttr) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException;

    @WebMethod
    Event getEventByEventID(int EventID) throws SQLException;

}