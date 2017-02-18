package service;

import entities.DataObject;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Hroniko on 14.02.2017.
 */
public interface LoadingService {

    // 2017-02-14 Альтернативный метод загрузки датаобджекта по его id
    DataObject getDataObjectByIdAlternative(Integer id) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;


    // 2017-02-14 Альтернативный метод загрузки нескольких датаобджектов в виде списка по списку их id
    ArrayList<DataObject> getListDataObjectByListIdAlternative(ArrayList<Integer> ids) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;


    // 2017-02-14 Альтернативный метод загрузки нескольких датаобджектов в виде списка по списку их id (для ручного ввода переменного количества id)
    ArrayList<DataObject> getListDataObjectByListIdAlternative(Integer... idx) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;


    // 2017-02-14 Метод получения списка id датаобджектов, удовлетворяющих условиям примененных фильтров, фильры задаем списком Фильт1, Значение1, Фильтр2, Значение2 ... и т д
    ArrayList<Integer> getListIdFiltered(String... strings) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;


    // 2017-02-14 Метод получения списка самих датаобджектов, удовлетворяющих условиям примененных фильтров, фильры задаем списком Фильт1, Значение1, Фильтр2, Значение2 ... и т д
    ArrayList<DataObject> getListDataObjectFiltered(String... strings) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;


    DataObject createDataObject(String name, int objType, TreeMap<Integer, Object> mapAttr) throws SQLException;


    void deleteDataObjectById(Integer id) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException;

    void setDataObjectToDB(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

    void updateDataObject(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException;

}
