package service.partition_filters;

/**
 * Created by Hroniko on 19.02.2017.
 */
public class UserPartition extends BasePartitionFilter {
    public static final String FULL = "with all body";
    public static final String LITE = "only id, type, name";
    public static final String WITH_ALL_PARAMS = "id, type, name with params";
    public static final String WITH_ALL_REFERENCES = "id, type, name with references";

    // Для параметров с переменным количеством аргументов
    public static final String WITH_PARAMS_OR_REFERENCES_LIST = "param1, param2, refer1, refer2 ...";

    // Сами аргументы для PARAMS_LIST:
    public static final String NAME = "add field NAME";
    public static final String SURNAME = "add field SURNAME";
    public static final String MIDDLENAME = "add field MIDDLENAME";
    public static final String AGEDATA = "add field AGEDATA";
    public static final String EMAIL = "add field EMAIL";
    public static final String PASSWORD = "add field PASWORD";
    public static final String CITY = "add field CITY";
    public static final String SEX = "add field SEX";
    public static final String ADDITIONAL = "add field ADDITIONAL_FIELD";
    public static final String AVATAR = "add field AVATAR";

    // Аргументы для REFERENCES_LIST:
    public static final String EVENTS = "add all references EVENTS";
    public static final String FRIENDS = "add all references FRIENDS";
    public static final String MESSAGES = "add all references MESSAGES";

    public UserPartition(String... params) {
        super();

        System.out.println("Создаю фильтр");
        // Размещаем константы, определяющие фильтр, по мапам, определяющим количество параметров у фильтра
        count_0.put(FULL, "");
        count_0.put(LITE, "");
        count_0.put(WITH_ALL_PARAMS, "");
        count_0.put(WITH_ALL_REFERENCES, "");

        count_0.put(NAME, "");
        count_0.put(SURNAME, "");
        count_0.put(MIDDLENAME, "");
        count_0.put(AGEDATA, "");
        count_0.put(EMAIL, "");
        count_0.put(PASSWORD, "");
        count_0.put(CITY, "");
        count_0.put(SEX, "");
        count_0.put(ADDITIONAL, "");
        count_0.put(AVATAR, "");

        count_0.put(EVENTS, "");
        count_0.put(FRIENDS, "");
        count_0.put(MESSAGES, "");

        count_var.put(WITH_PARAMS_OR_REFERENCES_LIST, "");


        // Переносим все параметры:
        addManyParams(params);
    }
}
