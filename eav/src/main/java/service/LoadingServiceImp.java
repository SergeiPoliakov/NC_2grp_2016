package service;

import dbHelp.DBHelp;
import entities.DataObject;

import java.sql.SQLException;
import java.util.TreeMap;

/**
 * Created by Hroniko on 11.02.2017.
 * Работа с DataObjects
 */
public class LoadingServiceImp implements LoadingService {

    UserServiceImp userService = UserServiceImp.getInstance();

    public static class ObjType{

        static final Integer USER = 1001;
        static final Integer EVENT = 1002;
        static final Integer MESSAGE = 1003;
        static final Integer MEETING = 1004;
    }

    @Override
    public DataObject getDataObjectById(Integer id) throws SQLException {
        DataObject dataObject = null;
        // Проверяем, в каком диапазоне у нас id, и в зависимости от этого тянем методы из dbhelp
        if ((id > 10000) & (id < 20000)){
            // Тянем юзера - получаем параметры из БД через DBHelp
            TreeMap<Integer, Object> treeMap = new DBHelp().getUserById(id);
            String name = (String) treeMap.get(3) + " " + (String) treeMap.get(1) + " " + (String) treeMap.get(2);
            // treeMap.remove(1);
            dataObject = new DataObject(id, name, ObjType.USER, treeMap);
        }
        else if ((id > 20000) & (id < 30000)){
            // Тянем  события
        }
        else if ((id > 30000) & (id < 40000)){
            // Тянем сообщения
        }
        else if (id < 10000){
            // Тянем встречи
        }

        // иначе что-то странное запросили, потому в dataObject останется null
        return dataObject;
    }


    @Override
    public void deleteDataObjectById(Integer id) {

    }

    @Override
    public DataObject createDataObject(String name, int objType, TreeMap<Integer, Object> mapAttr) throws SQLException {
        DataObject dataObject = new DataObject(userService.generationID(objType), name, objType, mapAttr);
        return dataObject;
    }
}
