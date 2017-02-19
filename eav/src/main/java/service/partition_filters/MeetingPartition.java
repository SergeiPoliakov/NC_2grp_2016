package service.partition_filters;

/**
 * Created by Hroniko on 19.02.2017.
 */
public class MeetingPartition extends BasePartitionFilter {
    public static final String FULL = "with all body";
    public static final String LITE = "only id, type, name";
    public static final String WITH_ALL_PARAMS = "id, type, name with params";
    public static final String WITH_ALL_REFERENCES = "id, type, name with references";

    // Для параметров с переменным количеством аргументов
    public static final String WITH_PARAMS_OR_REFERENCES_LIST = "param1, param2, refer1, refer2 ...";

    // Сами аргументы для PARAMS_LIST:
    public static final String TITLE = "add field TITLE";
    public static final String DATE_START = "add field DATE_START";
    public static final String DATE_END = "add field DATE_END";
    public static final String INFO = "add field INFO";
    public static final String ORGANIZER = "add field ORGANIZER";
    public static final String TAG = "add field TAG";


    // Аргументы для REFERENCES_LIST:
    public static final String MEMBERS = "add all references MEMBERS";

    public MeetingPartition(String... params) {
        super();

        System.out.println("Создаю фильтр");
        // Размещаем константы, определяющие фильтр, по мапам, определяющим количество параметров у фильтра
        count_0.put(FULL, "");
        count_0.put(LITE, "");
        count_0.put(WITH_ALL_PARAMS, "");
        count_0.put(WITH_ALL_REFERENCES, "");

        count_0.put(TITLE, "");
        count_0.put(DATE_START, "");
        count_0.put(DATE_END, "");
        count_0.put(INFO, "");
        count_0.put(ORGANIZER, "");
        count_0.put(TAG, "");

        count_0.put(MEMBERS, "");

        count_var.put(WITH_PARAMS_OR_REFERENCES_LIST, "");


        // Переносим все параметры:
        addManyParams(params);
    }
}
