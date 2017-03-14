package entities;

/**
 * Created by Hroniko on 12.03.2017.
 * Сущность, представляющая набор статистик конкретного пользователя
 */
public class Statictics extends BaseEntitie {

    // Скоро сделаю


    private Integer id;

    private String name; // 1


    // Параметры в PARAMS:

    private String date; // Дата регистрации события логгером

    private Integer type; // Тип события (авторизация, посещение страницы, добавление друзей и пр.)
}
