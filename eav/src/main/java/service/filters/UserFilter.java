package service.filters;

/**
 * Created by Hroniko on 16.02.2017.
 */
public class UserFilter extends BaseFilter {

    public static final String ALL = "all";
    public static final String CURRENT = "current";
    public static final String WITH_NAME = "with_name";
    public static final String SEARCH_USER = "search_user";

    public static final String WITH_EMAIL = "with_email";
    public static final String WITH_ALL_EVENTS = "with_all_events";
    public static final String ALL_FRIENDS = "all_friends";

    public UserFilter(String... params) {
        super();

        System.out.println("Создаю фильтр");
        // Размещаем константы, определяющие фильтр, по мапам, определяющим количество параметров у фильтра
        count_0.put(ALL, "");
        count_0.put(CURRENT, "");
        count_0.put(WITH_ALL_EVENTS, "");

        count_1.put(SEARCH_USER , "");
        count_1.put(ALL_FRIENDS , "");

        count_var.put(WITH_NAME, "");
        count_var.put(WITH_EMAIL, "");

        // Переносим все параметры:
        addManyParams(params);
    }
}
