package service;

import entities.DataObject;

import java.sql.SQLException;
import java.util.TreeMap;

/**
 * Created by Hroniko on 11.02.2017.
 */
public interface LoadingService {

    public DataObject getDataObjectById(Integer id) throws SQLException;
    public DataObject createDataObject(String name, int objType, TreeMap<Integer, Object> mapAttr) throws SQLException;
    public void deleteDataObjectById(Integer id);

}
