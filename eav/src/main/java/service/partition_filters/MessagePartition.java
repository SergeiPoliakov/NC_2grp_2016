package service.partition_filters;

/**
 * Created by Hroniko on 19.02.2017.
 */
public class MessagePartition extends BasePartitionFilter {
    public static final String FULL = "with all body";
    public static final String LITE = "only id, type, name";
    public static final String WITH_ALL_PARAMS = "id, type, name with params";
    //public static final String WITH_ALL_REFERENCES = "id, type, name with references";

    // Для параметров с переменным количеством аргументов
    public static final String WITH_PARAMS_LIST = "param1, param2, ...";

    // Сами аргументы для PARAMS_LIST:
    public static final String FROM_ID = "add field FROM_ID";
    public static final String TO_ID = "add field TO_ID";
    public static final String DATE_SEND = "add field DATE_SEND";
    public static final String READ_STATUS = "add field READ_STATUS";
    public static final String TEXT = "add field TEXT";
    public static final String FROM_NAME = "add field TEXT";
    public static final String TO_NAME = "add field TEXT";

    // Аргументы для REFERENCES_LIST:
    // -----

    public MessagePartition(String... params) {
        super();

        System.out.println("Создаю фильтр");
        // Размещаем константы, определяющие фильтр, по мапам, определяющим количество параметров у фильтра
        count_0.put(FULL, "");
        count_0.put(LITE, "");
        count_0.put(WITH_ALL_PARAMS, "");
        //count_0.put(WITH_ALL_REFERENCES, "");

        count_0.put(FROM_ID, "");
        count_0.put(TO_ID, "");
        count_0.put(DATE_SEND, "");
        count_0.put(READ_STATUS, "");
        count_0.put(TEXT, "");
        count_0.put(FROM_NAME, "");
        count_0.put(TO_NAME, "");

        count_var.put(WITH_PARAMS_LIST, "");


        // Переносим все параметры:
        addManyParams(params);
    }
}
