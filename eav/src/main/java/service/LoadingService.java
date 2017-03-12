package service;

import entities.DataObject;
import service.id_filters.*;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Hroniko on 14.02.2017.
 */

@WebService(name = "LoadingService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface LoadingService {

    // 2017-02-14 Альтернативный метод загрузки датаобджекта по его id
    @WebMethod
    DataObject getDataObjectByIdAlternative(Integer id) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;


    // 2017-02-14 Альтернативный метод загрузки нескольких датаобджектов в виде списка по списку их id
    ArrayList<DataObject> getListDataObjectByListIdAlternative(ArrayList<Integer> ids) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;


    // 2017-02-19 Альтернативный метод загрузки нескольких датаобджектов в виде списка по списку их id
   // ArrayList<DataObject> getListDataObjectByListIdAlternative(ArrayList<Integer> ids, PartitionFilter... pfs) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;


    // 2017-02-14 Альтернативный метод загрузки нескольких датаобджектов в виде списка по списку их id (для ручного ввода переменного количества id)



    // 2017-02-14 Метод получения списка id датаобджектов, удовлетворяющих условиям примененных фильтров, фильры задаем списком Фильт1, Значение1, Фильтр2, Значение2 ... и т д



    // 2017-02-14 Метод получения списка самих датаобджектов, удовлетворяющих условиям примененных фильтров, фильры задаем списком Фильт1, Значение1, Фильтр2, Значение2 ... и т д

    @WebMethod
    ArrayList<Integer> getListIdFilteredAlternative(UserFilter userFilter) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    @WebMethod
    ArrayList<Integer> getListIdFilteredAlternative(EventFilter eventFilter) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    @WebMethod
    ArrayList<Integer> getListIdFilteredAlternative(MessageFilter messageFilter) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    @WebMethod
    ArrayList<Integer> getListIdFilteredAlternative(service.id_filters.MeetingFilter meetingFilter) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    @WebMethod // 2017-03-12 // Для уведомлений
    ArrayList<Integer> getListIdFilteredAlternative(service.id_filters.NotificationFilter notificationFilter) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    @WebMethod
    DataObject createDataObject(String name, int objType, TreeMap<Integer, Object> mapAttr) throws SQLException;

    @WebMethod
    void deleteDataObjectById(Integer id) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException;

    @WebMethod
    void setDataObjectToDB(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    @WebMethod
    void updateDataObject(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

}
