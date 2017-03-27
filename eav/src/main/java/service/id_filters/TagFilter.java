package service.id_filters;

/**
 * Created by Hroniko on 25.03.2017.
 */
// Класс-фильтр (не частичный, а обычный) на выбор тегов из бд
public class TagFilter extends BaseFilter {
    // Тут нам нужно либо загрузать все теги, чтобы отстроить в памяти полноценное дерево тегов, либо вытащить новые, который в нашем дереве еще нет (передаем те, которые есть, получаем те, которых нет)
    public static final String ALL = "all";
    public static final String WITHOUT_ID = "current";

    public TagFilter(String... params) {
        super();

        System.out.println("Создаю фильтр");
        // Размещаем константы, определяющие фильтр, по мапам, определяющим количество параметров у фильтра
        count_0.put(ALL, "");

        count_var.put(WITHOUT_ID, "");

        // Переносим все параметры:
        addManyParams(params);
    }
}
