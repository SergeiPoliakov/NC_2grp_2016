package service.partition_filters;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Hroniko on 19.02.2017.
 * Базовый класс-фильтр для редактирования sql запросов (включения-выключения отдельных полей для загрузки) (для частичного вытягивания датаобджектов)
 */
public class BasePartitionFilter {
    // Мапа параметров с перечислением значений
    TreeMap<String, ArrayList<String>> params = new TreeMap<>();

    // Мапа параметров без аргументов:
    TreeMap<String, String> count_0 = new TreeMap<>();
    // Мапа параметров c переменным кличеством аргументов:
    TreeMap<String, String> count_var = new TreeMap<>();

    public BasePartitionFilter() {
    }

    public TreeMap<String, ArrayList<String>> getParams() {
        return params;
    }

    public void addManyParams(String... params) {
        for (int i = 0; i < params.length; ) {
            String key = "";
            String value = "";
            try {
                key = params[i];
                if (count_0.get(key) != null){ // если получили параметр без аргументов
                    addParams(key, key);
                    System.out.println("Выставляю параметр фильтра: " + key);
                }
                else if (count_var.get(key) != null){ // если получили параметр c переменным количеством аргументов
                    while ((i < params.length)  // пока значения не закончились и пока не встретили новый ключ
                            & (count_0.get(params[++i]) == null)
                            & (count_var.get(params[++i]) == null))
                        i++;
                    value = params[i];
                    addParams(key, value);
                }
                i++;
            } catch (Exception e) {
                System.out.println("Параметр " + key + " без значения! Обрабатываться не будет");
            }
        }
    }

    public void addParams(String key, String value){
        if (params.get(key) == null){
            ArrayList<String> ar = new ArrayList<>();
            params.put(key, ar);
        }
        params.get(key).add(value);
    }
}
