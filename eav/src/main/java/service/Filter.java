package service;

/**
 * Created by Hroniko on 14.02.2017.
 * Класс фильтров для запросов в базу
 * ЭТО СТАРЫЙ КЛАСС! Теперь все фильтры в пакете service.id_filters (2017-02-16)
 */
public class Filter {

    // 1. для получения протсо всех датаобджектов определенного типа
    // Ключ:
    public static final String OBJECT_TYPE = "OBJECT_TYPE_ID";
    // Возможные значения:
    public static final String OBJECT_TYPE_USER = "1001";
    public static final String OBJECT_TYPE_EVENT = "1002";
    public static final String OBJECT_TYPE_MESSAGE = "1003";
    public static final String OBJECT_TYPE_MEETING = "1004";

    // 2. Для определения всех датаобджектов с определенным именем
    // Ключ:
    public static final String OBJECT_NAME = "OBJECT_NAME";
    // Возможные значения:
    // Любые

    // 3. для определения всех друзей данного пользователя
    // Ключ:
    public static final String FRIENDS_FOR_USER = "12";
    // Возможные значения:
    // id текущего пользователя

    // Надо еще других фиьтров дописать
}
