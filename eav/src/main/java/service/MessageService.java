package service;

import entities.Message;

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

@WebService(name = "MessageService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
interface MessageService {

    @WebMethod
    void setNewMessage(int ObjTypeID, TreeMap<Integer, Object> massAttr) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException;

    @WebMethod
    void deleteMessage(Integer messageId) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException, SQLException;

    @WebMethod
    ArrayList<Message> getMessageList(int from_id, int to_id) throws SQLException;
}
