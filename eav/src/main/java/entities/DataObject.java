package entities;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Lawrence on 11.02.2017.
 * Альтернативный DataObject Update by Hroniko on 14.02.2017.
 * 
 */

public class DataObject {
    private Integer id;
    private Integer objectTypeId;
    private String name;

    // 2017-02-14 значения ссылок теперь хранятся в списке, так как ссылок может быть много
    private TreeMap<Integer, ArrayList<Integer>> refParams = new TreeMap<>();
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

    // Универсальный метод установки параметра (добавление в подходящую мапу по ключу-значению)
    public void setValue(Integer key, Object value){
        // в зависимости от типа
        if (value instanceof Integer)
        {
            setRefParams(key, (Integer) value);

        }
        else if (value instanceof String)
        {
            params.put(key, (String) value);
        }
    }

    // Универсальный метод получения параметра, смотрит в обе мапы и возращает в виде объекта значение, либо null
    public Object getValue(Integer key){
        if (refParams.get(key) != null){
            return refParams.get(key);
        }
        else if (params.get(key) != null){
            return params.get(key);
        }
        return null;
    }


    // 2017-02-14 Метод установки параметра
    public void setParams(Integer key, String value){
        params.put(key, value);
    }
    // 2017-02-14 Метод получения параметра
    public String getParams(String key){
        return params.get(key);
    }


    // 2017-02-14 Метод установки ссылки в список ссылок данного типа
    public void setRefParams(Integer key, Integer value){
        if (refParams.get(key) == null) { // если список ссылок с таким ключем еще не создан, создаем его
            ArrayList<Integer> ar = new ArrayList<>();
            refParams.put(key, ar);
        }
        // и кладем в лист наше значение
        refParams.get(key).add(value);
    }
/*    // 2017-02-14 Метод получения списка ссылок
    public ArrayList<Integer> getRefParams(Integer key){
        return refParams.get(key);
    }

*/

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

    public TreeMap<Integer, ArrayList<Integer>> getRefParams() {
        return refParams;
    }

    public void setRefParams(TreeMap<Integer, ArrayList<Integer>> refParams) {
        this.refParams = refParams;
    }

    public TreeMap<Integer, String> getParams() {
        return params;
    }

    public void setParams(TreeMap<Integer, String> params) {
        this.params = params;
    }
}


