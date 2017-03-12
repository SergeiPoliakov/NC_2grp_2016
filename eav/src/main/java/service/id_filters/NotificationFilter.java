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

    public static final String FROM_TO_USERS_WITH_NAMES = "from_to_users_with_names";
    public static final String FROM_TO_USERS_WITH_IDS = "from_to_two_users_with_ids";
    public static final String BETWEEN_TWO_USERS_WITH_NAMES = "between_two_users_with_names"; // В оба направления
    public static final String BETWEEN_TWO_USERS_WITH_IDS = "between_two_users_with_ids"; // В оба направления
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
        count_0.put(SEEN, "");      // На будущее, пока не реализовано поле в базе
        count_0.put(UNSEEN, "");    // На будущее, пока не реализовано поле в базе

        count_1.put(FOR_USER_WITH_NAME, "");
        count_1.put(FOR_USER_WITH_ID, "");
        count_1.put(BEFORE_DATE, "");
        count_1.put(AFTER_DATE, "");


        count_2.put(BETWEEN_TWO_DATES, "");
        count_2.put(FROM_TO_USERS_WITH_NAMES , "");
        count_2.put(FROM_TO_USERS_WITH_IDS , "");
        count_2.put(BETWEEN_TWO_USERS_WITH_NAMES , "");
        count_2.put(BETWEEN_TWO_USERS_WITH_IDS , "");

        count_var.put(WITH_TYPE, "");

        // Переносим все параметры:
        addManyParams(params);
    }


}
