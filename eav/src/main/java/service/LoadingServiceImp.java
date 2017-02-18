package service;

import dbHelp.DBHelp;
import entities.DataObject;
import service.filters.*;
import service.filters.MeetingFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Hroniko on 11.02.2017.
 * Работа с DataObjects
 */
public class LoadingServiceImp implements LoadingService {

    UserServiceImp userService = UserServiceImp.getInstance();
    Filter filter = new Filter();

    private EventServiceImp eventService = EventServiceImp.getInstance();

    public static class ObjType{

        static final Integer USER = 1001;
        static final Integer EVENT = 1002;
        static final Integer MESSAGE = 1003;
        static final Integer MEETING = 1004;
    }

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
    @Override
    public ArrayList<DataObject> getListDataObjectByListIdAlternative(Integer... idx) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ArrayList<Integer> ids = new ArrayList<>();
        for (Integer i: idx) {
            ids.add(i);
        }
        return new DBHelp().getListObjectsByListIdAlternative(ids);
    }

    // 2017-02-14 Метод получения списка id датаобджектов, удовлетворяющих условиям примененных фильтров, фильры задаем списком Фильт1, Значение1, Фильтр2, Значение2 ...
    @Override
    public ArrayList<Integer> getListIdFiltered(String... strings) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ArrayList<Integer> intList = new DBHelp().getListObjectsByListIdAlternative(strings);
        return intList;
    }









    // 2017-02-16 Метод #2 получения списка id датаобджектов, удовлетворяющих условиям примененных фильтров, фильры задаем списком FilterAlternetive(Параметры), ...
    // Переопределение методов
    // С фильтром для юзера
    public ArrayList<Integer> getListIdFilteredAlternative(UserFilter userFilter) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        System.out.println("Запускаю getListIdFilteredAlternative c полученным фильтром");
        ArrayList<Integer> il = new DBHelp().getListObjectsByFilters(userFilter);
        System.out.print("Вывожу список id отфильтроанных пользователей: ");
        for (int i = 0; i < il.size(); i++){
            System.out.print(il.get(i) + ", ");
        }
        return il;
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
    @Override
    public ArrayList<DataObject> getListDataObjectFiltered(String... strings) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // Сначала применяем фильтры и получем список подходящих айдишников:
        ArrayList<Integer> idList = getListIdFiltered(strings);
        // А затем полученные айди кидаем в метод вывода списка датаобджектов по списку айди:
        ArrayList<DataObject> dataObjectList = getListDataObjectByListIdAlternative(idList);
        return dataObjectList;
    }


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
}
