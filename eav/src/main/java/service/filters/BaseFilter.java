package service.filters;

import java.util.ArrayList;
import java.util.TreeMap;

/**
 * Created by Hroniko on 16.02.2017.
 * Новый базовый класс-фильтр для формирования sql запросов (для последующего вытягивания подходящих под условия IDs датаобджектов)
 */
public class BaseFilter {
    // Мапа параметров с перечислением значений
    TreeMap<String, ArrayList<String>> params = new TreeMap<>();

    // Мапа параметров без аргументов:
    TreeMap<String, String> count_0 = new TreeMap<>();
    // Мапа параметров c одним аргументом:
    TreeMap<String, String> count_1 = new TreeMap<>();
    // Мапа параметров c двумя аргументами:
    TreeMap<String, String> count_2 = new TreeMap<>();
    // Мапа параметров c переменным кличеством аргументов:
    TreeMap<String, String> count_var = new TreeMap<>();

    public BaseFilter() {
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
                else if (count_1.get(key) != null){ // если получили параметр c одним аргументом
                    i++;
                    value = params[i];
                    addParams(key, value);
                }
                else if (count_2.get(key) != null){ // если получили параметр c двумя аргументами
                    i++;
                    value = params[i];
                    addParams(key, value);
                    i++;
                    value = params[i];
                    addParams(key, value);
                }
                else if (count_var.get(key) != null){ // если получили параметр c переменным количеством аргументов
                    while ((i < params.length)  // пока значения не закончились и пока не встретили новый ключ
                            & (count_0.get(params[++i]) == null)
                            & (count_1.get(params[++i]) == null)
                            & (count_2.get(params[++i]) == null)
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
