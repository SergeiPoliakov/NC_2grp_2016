package service;

import dbHelp.DBHelp;
import entities.DataObject;
import service.id_filters.*;
import service.id_filters.MeetingFilter;
import service.partition_filters.EventPartition;
import service.partition_filters.MeetingPartition;
import service.partition_filters.MessagePartition;
import service.partition_filters.UserPartition;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Hroniko on 11.02.2017.
 * Работа с DataObjects
 */
public class LoadingServiceImp implements LoadingService {

    private UserServiceImp userService = new UserServiceImp();



    // 2017-02-14 Альтернативный метод загрузки одного конкретного датаобджекта по его id
    @Override
    public DataObject getDataObjectByIdAlternative(Integer id) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        DataObject dataObject = new DBHelp().getObjectsByIdAlternative(id);
        return dataObject;
    }

    // 2017-02-14 Альтернативный метод загрузки нескольких датаобджектов в виде списка по списку их id
    @Override
    public ArrayList<DataObject> getListDataObjectByListIdAlternative(ArrayList<Integer> ids) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ArrayList<DataObject> dataObjectList = new DBHelp().getListObjectsByListIdAlternative(ids);
        return dataObjectList;
    }


    // 2017-02-14 Альтернативный метод загрузки нескольких датаобджектов в виде списка по списку их id (для ручного ввода переменного количества id)


    // 2017-02-14 Метод получения списка id датаобджектов, удовлетворяющих условиям примененных фильтров, фильры задаем списком Фильт1, Значение1, Фильтр2, Значение2 ...










    // 2017-02-16 Метод #2 получения списка id датаобджектов, удовлетворяющих условиям примененных фильтров, фильры задаем списком FilterAlternetive(Параметры), ...
    // Переопределение методов
    // С фильтром для юзера
    public ArrayList<Integer> getListIdFilteredAlternative(UserFilter userFilter) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return new DBHelp().getListObjectsByFilters(userFilter);
    }
    // с фильтром для событий
    public ArrayList<Integer> getListIdFilteredAlternative(EventFilter eventFilter) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return new DBHelp().getListObjectsByFilters(eventFilter);
    }
    // с фильтром для сообщений
    public ArrayList<Integer> getListIdFilteredAlternative(MessageFilter messageFilter) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return new DBHelp().getListObjectsByFilters(messageFilter);

    }
    // с фильтром для встреч
    public ArrayList<Integer> getListIdFilteredAlternative(MeetingFilter meetingFilter) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return new DBHelp().getListObjectsByFilters(meetingFilter);
    }


    // 2017-02-14 Метод получения списка самих датаобджектов, удовлетворяющих условиям примененных фильтров, фильры задаем списком Фильт1, Значение1, Фильтр2, Значение2 ... и т д



    // Старый метод создания датаобджекта
    @Override
    public DataObject createDataObject(String name, int objType, TreeMap<Integer, Object> mapAttr) throws SQLException {
        DataObject dataObject = new DataObject(userService.generationID(objType), name, objType, mapAttr);
        return dataObject;
    }

    // 2017-02-18 Новый метод переноса датаобджекта в базу
    @Override
    public void setDataObjectToDB(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        new DBHelp().setDataObjectToDB(dataObject);
    }
    // 2017-02-18 Новый метод обновления датаобджекта в базе
    @Override
    public void updateDataObject(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        new DBHelp().updateDataObject(dataObject);
    }
    // 2017-02-18 Новый метод удаления датаобджекта из базы
    @Override
    public void deleteDataObjectById(Integer id) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        new DBHelp().deleteDataObject(id);

    }

    /*...............................................................................................................*/
    /// 2017-02-19 Применение частичных фильтров:
    // С фильтром для юзера
    public ArrayList<DataObject> getListPartitionsDataObjects(ArrayList<Integer> idList, UserPartition userPartition) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return new DBHelp().getPartitionsDataObjectsList(idList, userPartition);
    }
    // с фильтром для событий
    public ArrayList<DataObject> getListPartitionsDataObjects(ArrayList<Integer> idList, EventPartition eventPartition) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return new DBHelp().getPartitionsDataObjectsList(idList, eventPartition);
    }
    // с фильтром для сообщений
    public ArrayList<DataObject> getListPartitionsDataObjects(ArrayList<Integer> idList, MessagePartition messagePartition) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return new DBHelp().getPartitionsDataObjectsList(idList, messagePartition);
    }
    // с фильтром для встреч
    public ArrayList<DataObject> getListPartitionsDataObjects(ArrayList<Integer> idList, MeetingPartition meetingPartition) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        return new DBHelp().getPartitionsDataObjectsList(idList, meetingPartition);
    }

}
