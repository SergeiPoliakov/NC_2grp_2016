package service.tags;

import dbHelp.DBHelp;
import entities.DataObject;
import entities.TagNode;
import entities.User;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.converter.Converter;
import service.id_filters.UserFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Hroniko on 30.03.2017.
 */
// Класс подвешенного дерева для быстрого поиска юзеров по их имени, фамилии, отчеству // нследуемся от дерева тегов и переопределяем часть методов
public class NameNodeTree extends TagNodeTree {

    private static volatile TagNodeTree instance;

    private boolean print_flag = true; // служебный флаг для включения возможности вывода в консоль служебной инфы
    private UserServiceImp userService = new UserServiceImp();
    private LoadingServiceImp loadingService = new LoadingServiceImp();

    private TagNode root = TagNode.getInstance(); // Ссылка на корневой узел


    public static TagNodeTree getInstance()  {
        if (instance == null)
            synchronized (TagNodeTree.class) {
                if (instance == null)
                    instance = new TagNodeTree();
            }
        return instance;
    }

    // Конструктор:
    public NameNodeTree() {
        try {
            this.loadAndCreateTagNodeTree();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            this.root = TagNode.getInstance();
        }
    }

    // Переопределяем метод загрузки из базы
    @Override
    void loadAndCreateTagNodeTree() throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        ArrayList<Integer> user_ids = loadingService.getListIdFilteredAlternative(new UserFilter(UserFilter.ALL));
        System.out.println("Список загружаемых из базы юзеров :" + user_ids);
        // Получаем список всех датаобджектов-юзеров:
        ArrayList<DataObject> user_list = new ArrayList<>();
        for(int i = 0; i < user_ids.size(); i++){
            DataObject dataObject =  new DBHelp().getObjectsByIdAlternative(user_ids.get(i));
            System.out.println("Загружен юзер:" + dataObject.getId() + " : " + dataObject.getRefParams());
            user_list.add(dataObject); // подгружаем по одному из базы и закидываем в список
        }

        // Конвертируем датаобджекты в юзеров
        ArrayList<User> users = new Converter().ToUser(user_list);


        this.root = TagNode.getInstance(); // Создаем базовый узел

        // И затем обходим всех юзеров и подвешиваем их имена к дереву
        for (int i = 0; i < users.size(); i++){
            User user = users.get(i);
            super.insertForUser(user.getName(), user.getId()); // Вешаем на дерево имя
            super.insertForUser(user.getSurname(), user.getId()); // Вешаем на дерево фамилию
            if (user.getMiddleName().length() > 0) super.insertForUser(user.getMiddleName(), user.getId()); // Если еще и отчество есть, то и его вешаем
        }


    }


    @Override
    // Метод поиска ФИО юзера в дереве (основной):
    public TagNode findForUser(String tag_word) {
        // Сначала дополнительные проверки:
        if (tag_word == null) {
            return null;
        }
        if (tag_word.length() == 0) {
            return null;
        }
        // 2017-03-23 Надо еще автоматически приводить буквы в слове к нижнему регистру!!! Чтобы не дублировать одно и то же в разных регистрах
        tag_word = tag_word.toLowerCase();
        return findKeyForUser(this.root, tag_word, 0);
    }

    // Метод поиска узла в дереве (вспомогательный, рекурсия)
    private TagNode findKeyForUser(TagNode node, String word, int pos) {

        char w = word.charAt(pos);
        if (print_flag) System.out.println("Ищу узел для буквы [" + w + "] текущего имени [" + word + "]");

        // А-1 Пробегаем по листу всех дочерних узлов (если это последний, то он будет пуст, не будет ссылок)
        ArrayList<TagNode> tagNodes = node.getParents();
        TagNode parent = null;
        int i = 0;
        for (; i < tagNodes.size(); i++) {
            // и смотрим, есть ли подходящий
            TagNode candidate = tagNodes.get(i);
            if (candidate.getValue() == w) {
                // Если содержит, все хорошо, запоминаем потомка и выходим из цикла:
                parent = candidate;
                if (print_flag) System.out.println("Нашли узел для буквы [" + w + "] текущего имени [" + word + "]");
                break;
            }
        }

        // A-2 если в процессе обхода ничего не нашли подходящего, значит нет такого тега
        if (parent == null) {
            if (print_flag) System.out.println("Не найден узел для буквы [" + w + "] текущего имени [" + word + "]. Поиск закончился недачей");
            return null;
        }


        // A-3 После этого проверяем, а не дошли ли мы до конца нашего имени?
        if (pos == word.length() - 1) {
            // Если долшли, то отдаем ссылку на нод, в котором совпала последняя буква
            if (print_flag) System.out.println("Поиск успешно завершен!");
            return parent;
        }
        else { // Иначе еще можно продолжать рекурсию, предварительно увеличив номер буквы в теге-слове
            pos++;
            return findKeyForUser(parent, word, pos); // и заходим в этого потомка
        }

    }



}
