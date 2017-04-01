package service.id_filters;

/**
 * Created by Hroniko on 01.04.2017.
 */
// Класс-фильтр (не частичный, а обычный) на выбор логов из бд
public class LogFilter extends BaseFilter {

    public static final String ALL = "all"; // Все имеющиеся в базе логи
    public static final String FOR_CURRENT_USER = "for_current_user"; // Все логи для текущего юзера
    public static final String FOR_USER_WITH_NAME = "for_user_with_name"; // Для юзера с именем
    public static final String FOR_USER_WITH_ID = "for_user_with_id"; // Для юзера с айди
    public static final String WITH_TYPE = "with_name"; // Все логи с заданным типом

    public static final String BETWEEN_TWO_DATES = "between_two_dates"; // Между двумя датами
    public static final String BEFORE_DATE = "before_date"; // До какой-то даты
    public static final String AFTER_DATE = "after_date"; // После какой-то даты



    public LogFilter(String... params) {
        super();

        // Размещаем константы, определяющие фильтр, по мапам, определяющим количество параметров у фильтра
        count_0.put(ALL, "");
        count_0.put(FOR_CURRENT_USER, "");

        count_1.put(FOR_USER_WITH_NAME, "");
        count_1.put(FOR_USER_WITH_ID, "");
        count_1.put(WITH_TYPE, "");
        count_1.put(BEFORE_DATE, "");
        count_1.put(AFTER_DATE, "");

        count_2.put(BETWEEN_TWO_DATES, "");

        // Переносим все параметры:
        addManyParams(params);
    }
}
