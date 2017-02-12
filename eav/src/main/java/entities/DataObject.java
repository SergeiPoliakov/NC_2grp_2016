package entities;

import java.util.TreeMap;

/**
 * Created by Lawrence on 11.02.2017.
 */

public class DataObject {
    private Integer id;
    private String name;
    private Integer objectTypeId;

    private TreeMap<Integer, Integer> refParams = new TreeMap<>();
    private TreeMap<Integer, String> params = new TreeMap<>();

    public DataObject() {
    }

    public DataObject(Integer id, String name, Integer objectTypeId) {
        this.id = id;
        this.name = name;
        this.objectTypeId = objectTypeId;
    }

    public DataObject(Integer id, String name, Integer objectTypeId, TreeMap<Integer, Object> treeMap) {
        this.id = id;
        this.name = name;
        this.objectTypeId = objectTypeId;
        // Пробегаем по парам мапы и в зависимости от типа расталкиваем все двойки по integerTreeMap и stringTreeMap
        for (Integer key : treeMap.keySet()) {
            if (treeMap.get(key) == null) {
                setValue(key, "");
            } else
            setValue(key, treeMap.get(key));
        }
    }

    // Метод установки параметра (добавление в подходящую мапу по ключу-значению)
    public void setValue(Integer key, Object value){
        // в зависимости от типа
        if (value instanceof Integer)
        {
            refParams.put(key, (Integer) value);
        }
        else if (value instanceof String)
        {
            params.put(key, (String) value);
        }
    }

    // Метод получения параметра, смотрит в обе мапы и возращает в виде объекта значение, либо null
    public Object getValue(Integer key){
        if (refParams.get(key) != null){
            return refParams.get(key);
        }
        else if (params.get(key) != null){
            return params.get(key);
        }
        return null;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getObjectTypeId() {
        return objectTypeId;
    }

    public void setObjectTypeId(Integer objectTypeId) {
        this.objectTypeId = objectTypeId;
    }

    public TreeMap<Integer, Integer> getRefParams() {
        return refParams;
    }

    public void setRefParams(TreeMap<Integer, Integer> refParams) {
        this.refParams = refParams;
    }

    public TreeMap<Integer, String> getParams() {
        return params;
    }

    public void setParams(TreeMap<Integer, String> params) {
        this.params = params;
    }
}


