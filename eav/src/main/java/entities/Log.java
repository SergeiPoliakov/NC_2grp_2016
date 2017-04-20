package entities;

/**
 * Created by Hroniko on 12.03.2017.
 * Сущность, представляющая конкретное залогированное действие конкретного пользователя
 */
public class Log extends BaseEntitie { // тип сущности 1008, диапазон кодов для параметров и ссылок 600-699

    // Допускаются следующие типы событий (600 ...699 по диапазонам, чтобы проще было потом добавлять новые):
    // Замечание! В отличие от других сущностей, логи могут храниться либо как ссылки на объекты в базе,
    // либо как записи в параметрах, поэтому либо то, либо то (вместе не храним, зачем лишнее дублирование):
    public static final int LOGIN =         601; // Авторизация -- параметр
    public static final int LOGOUT =        602; // Выход -- параметр
    public static final int RELOG =         603; // Восстановление пароля -- параметр
    public static final int PAGE =          604; // Посещение страницы (по адресу) -- параметр

    public static final int ADD_FRIEND =    611; // Добавление друга (с айди) -- сслылка на объект
    public static final int DEL_FRIEND  =   612; // Удаление друга (с айди) -- сслылка на объект
    public static final int SEARCH_USER =   613; // Поиск пользователя -- параметр
    public static final int VIEW_PROFILE =  614; // Просмотр профиля юзера -- сслылка на объект

    public static final int SEND_MESSAGE =  621; // Отправка сообщения (ссылка на сообщение) -- сслылка на объект
    public static final int GET_MESSAGE =   622; // Получение сообщения (ссылка на сообщение) -- сслылка на объект
    public static final int DEL_MESSAGE =   623; // Удаление сообщения -- параметр

    public static final int ADD_FILE =      631; // Загрузка файла (ссылка на файл) -- сслылка на объект
    public static final int EDIT_FILE =     632; // Изменение описания файла (ссылка на файл) -- сслылка на объект
    public static final int DEL_FILE =      633; // Удаление файла (ссылка на файл)  -- сслылка на объект // Не забыть сделать проверку перед удалением, вдруг файл кем-то еще используется
    public static final int SEND_FILE =     634; // Прикрепление и отправка файла (ссылка на сообщение) -- сслылка на объект
    public static final int AVATAR =        635; // Изменение аватара  -- параметр

    public static final int ADD_EVENT =     641; // Создание события (ссылка на событие) -- сслылка на объект
    public static final int EDIT_EVENT =    642; // Редактирование события (ссылка на событие) -- сслылка на объект
    public static final int DEL_EVENT =     643; // Удаление события (ссылка на событие не нужна!)

    public static final int ADD_MEETING =   651; // Создание встречи -- сслылка на объект
    public static final int EDIT_MEETING =  652; // Редактирование встречи -- сслылка на объект
    public static final int DEL_MEETING =   653; // Удаление встречи -- сслылка на объект
    public static final int SEND_INVITE_MEETING = 654; // Отправить приглашение на встречу -- сслылка на объект
    public static final int GET_INVITE_MEETING = 655; // Принять приглашение на встречу -- сслылка на объект
    public static final int CLOSED_MEETING = 656; // закрытие встречи
    public static final int DELETED_MEETING = 657; // удаление встречи из истории пользователя
    public static final int LEAVED_MEETING = 658; // удаление встречи из истории пользователя

    public static final int ADD_CALENDAR =  661; // Подключение календаря -- параметр
    public static final int SYNCHRONIZED_CALENDAR = 662; // Синхронизация календаря -- параметр

    public static final int EDIT_SETTINGS = 671; // Изменение настроек (в том числе настроек логирования и отображения статистик) -- параметр

    public static final int ADD_TAG =       681; // Добавление тега -- параметр
    public static final int EDIT_TAG =      682; // Редактирование тега -- параметр
    public static final int DEL_TAG =       683; // Удаление тега -- параметр
    public static final int FIND_TAG =      684; // Поиск тега -- параметр
    public static final int FIND_USER =     685; // Поиск юзера по тегу -- параметр
    public static final int FIND_MEETING =  686; // Поиск встречи по тегу -- параметр

    //

    private Integer id;
    private String name; // 1


    // Параметры в PARAMS и REFERENCES:

    private String date; // 600 // Дата регистрации события логгером

    private Integer type; // 601... 699 // Тип события (авторизация, посещение страницы, добавление друзей и пр.)

    private String info; // Информация о событии

    private Integer linkId; // Ссылка на родителя (на id юзера или сообщения или пр.)

    // Конструкторы
    public Log() {
    }

    public Log(Integer type, String name, String date) {
        this.name = name;
        this.date = date;
        this.type = type;
    }

    public Log(Integer type, String name, String date, String info) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.info = info;
    }

    public Log(Integer id, Integer type, String name, String date) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.type = type;
    }

    public Log(Integer id, Integer type, String name, String date, String info) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.type = type;
        this.info = info;
    }

    public Log(Integer type, String name, String date, Integer linkId) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.linkId = linkId;
    }

    public Log(Integer type, String name, String date, String info, Integer linkId) {
        this.name = name;
        this.date = date;
        this.type = type;
        this.info = info;
        this.linkId = linkId;

    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public Integer getLinkId() {
        return linkId;
    }

    public void setLinkId(Integer linkId) {
        this.linkId = linkId;
    }

    // Конвертация из типа в его строковое представление
    public static String convert (int logType){

        String name = "";
        switch (logType){
            case (LOGIN): // Авторизация
                name = "LOGIN";
                break;
            case (LOGOUT): // Выход
                name = "LOGOUT";
                break;
            case (RELOG): // Восстановление пароля
                name = "RELOG";
                break;
            case (PAGE): // Посещение страницы (по адресу)
                name = "PAGE";
                break;
            case (ADD_FRIEND): // Добавление друга (с айди)
                name = "ADD_USER";
                break;
            case (DEL_FRIEND): // Удаление друга (с айди)
                name = "DEL_USER";
                break;
            case (SEARCH_USER): // Удаление друга (с айди)
                name = "SEARCH_USER";
                break;
            case (VIEW_PROFILE): // Просмотр профиля(с айди)
                name = "VIEW_PROFILE";
                break;
            case (SEND_MESSAGE): // Отправка сообщения (ссылка на сообщение)
                name = "SEND_MESSAGE";
                break;
            case (GET_MESSAGE): // Получение сообщения (ссылка на сообщение)
                name = "GET_MESSAGE";
                break;
            case (DEL_MESSAGE): // Удаление сообщения
                name = "DEL_MESSAGE";
                break;
            case (ADD_FILE): // Загрузка файла (ссылка на файл)
                name = "ADD_FILE";
                break;
            case (EDIT_FILE): // Изменение описания файла (ссылка на файл)
                name = "EDIT_FILE";
                break;
            case (DEL_FILE): // Удаление файла (ссылка на файл)
                name = "DEL_FILE";
                break;
            case (SEND_FILE): // Прикрепление и отправка файла (ссылка на сообщение)
                name = "SEND_FILE";
                break;
            case (AVATAR): // Изменение аватара (ссылка на аватар)
                name = "AVATAR";
                break;
            case (ADD_EVENT): // Создание события (ссылка на событие)
                name = "ADD_EVENT";
                break;
            case (EDIT_EVENT): // Редактирование события (ссылка на событие)
                name = "EDIT_EVENT";
                break;
            case (DEL_EVENT): // Удаление события (ссылка на событие не нужна!)
                name = "DEL_EVENT";
                break;
            case (ADD_MEETING): // Создание встречи
                name = "ADD_MEETING";
                break;
            case (EDIT_MEETING): // Редактирование встречи
                name = "EDIT_MEETING";
                break;
            case (DEL_MEETING): // Удаление встречи
                name = "DEL_MEETING";
                break;
            case (SEND_INVITE_MEETING): // Отправить приглашение на встречу
                name = "SEND_INVITE_MEETING";
                break;
            case (GET_INVITE_MEETING): // Принять приглашение на встречу
                name = "GET_INVITE_MEETING";
                break;
            case (CLOSED_MEETING):  //закрытие встерчи
                name = "CLOSED_MEETING";
                break;
            case (DELETED_MEETING):  // удаление встречи из истории пользователя
                name = "DELETED_MEETING";
                break;
            case (LEAVED_MEETING):  // удаление встречи из истории пользователя
                name = "LEAVED_MEETING";
                break;
            case (ADD_CALENDAR): // Подключение календаря
                name = "ADD_CALENDAR";
                break;
            case (SYNCHRONIZED_CALENDAR): // Синхронизация календаря
                name = "SYNCHRONIZED_CALENDAR";
                break;
            case (EDIT_SETTINGS): // Изменение настроек (в том числе настроек логирования и отображения статистик)
                name = "EDIT_SETTINGS";
                break;
            case (ADD_TAG): // Добавление нового тега (к юзеру или ко встрече)
                name = "ADD_TAG";
                break;
            case (EDIT_TAG): // Изменение тега
                name = "EDIT_TAG";
                break;
            case (DEL_TAG): // Удаление тега
                name = "DEL_TAG";
                break;
            case (FIND_TAG): // Поиск тега по дереву
                name = "FIND_TAG";
                break;
            case (FIND_USER): // Поиск юзера с подходящим тегом
                name = "FIND_USER";
                break;
            case (FIND_MEETING): // Поиск встречи с подходящим тегом
                name = "FIND_MEETING";
                break;
        }

        return name;

    }

}
