package service;

import entities.DataObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Hroniko on 11.02.2017.
 */
public interface LoadingService {

    DataObject getDataObjectById(Integer id) throws SQLException;

    ArrayList<DataObject> getListDataObjectById(Integer id, String type) throws SQLException;

    DataObject createDataObject(String name, int objType, TreeMap<Integer, Object> mapAttr) throws SQLException;

    void deleteDataObjectById(Integer id);

}
