package service.id_filters;

/**
 * Created by Hroniko on 12.03.2017.
 * Класс id-фильтра для выборки из базы уведомлений
 */
public class NotificationFilter extends BaseFilter  {

    public static final String ALL = "all"; // Все уведомления системы
    public static final String FOR_CURRENT_USER = "for_current_user"; // Уведомления для текущего юзера
    public static final String FOR_USER_WITH_NAME = "for_user_with_name";
    public static final String FOR_USER_WITH_ID = "for_user_with_id";

    public static final String BETWEEN_TWO_DATES = "between_two_dates";
    public static final String BEFORE_DATE = "before_date";
    public static final String AFTER_DATE = "after_date";

    public static final String WITH_TYPE = "with_type";

    public static final String SEEN = "read"; // Прочитанные
    public static final String UNSEEN = "unread"; // Непрочитанные

    public NotificationFilter(String... params) {
        super();
        // Размещаем константы, определяющие фильтр, по мапам, определяющим количество параметров у фильтра
        count_0.put(ALL, "");
        count_0.put(FOR_CURRENT_USER, "");
        count_0.put(SEEN, "");
        count_0.put(UNSEEN, "");

        count_1.put(FOR_USER_WITH_NAME, "");
        count_1.put(FOR_USER_WITH_ID, "");
        count_1.put(BEFORE_DATE, "");
        count_1.put(AFTER_DATE, "");
        count_1.put(WITH_TYPE, "");

        count_2.put(BETWEEN_TWO_DATES, "");

        // Переносим все параметры:
        addManyParams(params);
    }


}
