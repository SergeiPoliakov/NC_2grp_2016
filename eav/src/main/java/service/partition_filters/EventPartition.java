package service.partition_filters;

/**
 * Created by Hroniko on 19.02.2017.
 */
public class EventPartition extends BasePartitionFilter {
    public static final String FULL = "with all body";
    public static final String LITE = "only id, type, name";
    public static final String WITH_ALL_PARAMS = "id, type, name with params";
    //public static final String WITH_ALL_REFERENCES = "id, type, name with references";

    // Для параметров с переменным количеством аргументов
    public static final String WITH_PARAMS_LIST = "param1, param2, ...";

    // Сами аргументы для PARAMS_LIST:
    public static final String DATE_BEGIN = "add field DATE_BEGIN";
    public static final String DATE_END = "add field SURNAME";
    public static final String DURATION = "add field DURATION";
    public static final String INFO = "add field INFO";
    public static final String PRIORITY = "add field PRIORITY";

    // Аргументы для REFERENCES_LIST:
    // -----

    public EventPartition(String... params) {
        super();

        System.out.println("Создаю фильтр");
        // Размещаем константы, определяющие фильтр, по мапам, определяющим количество параметров у фильтра
        count_0.put(FULL, "");
        count_0.put(LITE, "");
        count_0.put(WITH_ALL_PARAMS, "");
        //count_0.put(WITH_ALL_REFERENCES, "");

        count_0.put(DATE_BEGIN, "");
        count_0.put(DATE_END, "");
        count_0.put(DURATION, "");
        count_0.put(INFO, "");
        count_0.put(PRIORITY, "");

        count_var.put(WITH_PARAMS_LIST, "");


        // Переносим все параметры:
        addManyParams(params);
    }
}
