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
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Hroniko on 30.03.2017.
 */
// Класс подвешенного дерева для быстрого поиска юзеров по их имени, фамилии, отчеству // нследуемся от дерева тегов и переопределяем часть методов
public class NameNodeTree {

    private static volatile NameNodeTree instance;

    private boolean print_flag = true; // служебный флаг для включения возможности вывода в консоль служебной инфы
    private UserServiceImp userService = new UserServiceImp();
    private LoadingServiceImp loadingService = new LoadingServiceImp();

    private TagNode root = TagNode.getInstance(); // Ссылка на корневой узел

    // 1) 2017-05-13 Мапа для хранения всех пользователей (чтобы не вытаскивать из базы)
    public static final Map<Integer, User> userMap = new ConcurrentHashMap<>();


    public static NameNodeTree getInstance()  {
        if (instance == null)
            synchronized (NameNodeTree.class) {
                if (instance == null)
                    instance = new NameNodeTree();
            }
        return instance;
    }

    // Конструктор:
    public NameNodeTree() {
        try {
            this.loadAndCreateNameNodeTree();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            this.root = TagNode.getInstance();
        }
    }

    // Метод загрузки из базы
    void loadAndCreateNameNodeTree() throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
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

        // И подвешиваем юзеров к мапе:
        for(User user : users){
            userMap.put(user.getId(), user);
        }

        // И затем обходим всех юзеров и подвешиваем их имена к дереву
        for (int i = 0; i < users.size(); i++){
            User user = users.get(i);
            insertForUser(user.getName(), user.getId()); // Вешаем на дерево имя
            insertForUser(user.getSurname(), user.getId()); // Вешаем на дерево фамилию
            if (user.getMiddleName() != null && user.getMiddleName().length() > 0) insertForUser(user.getMiddleName(), user.getId()); // Если еще и отчество есть, то и его вешаем
        }

    }

    // 2017-05-13 Метод получения пользователя (сущности) по его id
    public User getUserById(Integer user_id){
        return userMap.get(user_id);
    }


    // Метод добавления тега-слова для переданного юзера в дерево (основной):
    public void insertForUser(String tag_word, Integer user_id) {
        tag_word = tag_word.toLowerCase();
        insertKeyForUser(this.root, tag_word, 0, user_id);
    }

    // Метод добавления тега-слова для текущего юзера в дерево (основной):
    public void insertForUser(String tag_word) throws SQLException {
        Integer user_id = userService.getObjID(userService.getCurrentUsername());
        insertForUser(tag_word, user_id);
    }


    // Метод вставки узла в дерево (вспомогательный, рекурсия): // node - текущий узел, слово, номер буквы в слове, айди юзера-хранителя тега
    private void insertKeyForUser(TagNode node, String word, int pos, int id_user) {
        char w = word.charAt(pos);
        if (print_flag) System.out.println("Находимся в ноде [" + node.getValue() + "]");
        if (print_flag) System.out.println("Ищу узел для буквы [" + w + "] текущего тега [" + word + "]");

        // А-1 Пробегаем по листу всех дочерних узлов (если это последний, то он будет пуст, не будет ссылок)
        ArrayList<TagNode> tagNodes = node.getParents();
        TagNode parent = null;

        for (int i = 0; i < tagNodes.size(); i++) {
            // и смотрим, есть ли подходящий
            TagNode candidate = tagNodes.get(i);
            if (candidate.getValue() == w) {
                // Если содержит, все хорошо, запоминаем потомка и выходим из цикла:
                parent = candidate;
                if (print_flag) System.out.println("Нашли узел для буквы [" + w + "] текущего имени [" + word + "]");
                break;
            }
        }

        // если в процессе обхода ничего не нашли подходящего, создадим сами
        if (parent == null){
            if (print_flag) System.out.println("Не нашли узел для буквы [" + w + "] текущего имени [" + word + "]");
            if (print_flag) System.out.println("Создаю узел для буквы [" + w + "] текущего имени [" + word + "]");
            parent = new TagNode(); // Создаем новый узел с установкой ссылки на родителя и установкой значения буквы
            parent.setRoot(node);
            parent.setValue(w);
            parent.setName("tag_node_" + parent.getId()); // Устанавливаем имя потомка
            node.setParents(parent); // и добавляем в список потомков текущего узла (родителя)
            System.out.println("Текущее состояние родителя: " + node);
            System.out.println("Текущее состояние потомка: " + parent);

        }

        // После этого проверяем, а не дошли ли мы до конца нашего слова-тега? (у нас уже точно есть новый нод, в который мы можем зайти)
        if (pos == word.length() - 1) {
            // Если долшли, то добавляем ссылки на юзера в наш parent-узел:
            parent.addUserId(id_user); // добавляем юзера
        }
        else { // Иначе еще можно продолжать рекурсию, предварительно увеличив номер буквы в теге-слове
            pos++;
            insertKeyForUser(parent, word, pos, id_user); // и заходим в этого потомка
        }
    }


    // Метод поиска ФИО юзера в дереве (основной):
    public TagNode findForUser(String tag_word) {
        tag_word = tag_word.toLowerCase();
        return findKeyForUser(this.root, tag_word, 0);
    }

    // Метод поиска узла в дереве (вспомогательный, рекурсия)
    private TagNode findKeyForUser(TagNode node, String word, int pos) {
        char w = word.charAt(pos);
        if (print_flag) System.out.println("Ищу узел для буквы [" + w + "] текущего имени [" + word + "]");

        // Пробегаем по листу всех дочерних узлов (если это последний, то он будет пуст, не будет ссылок)
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

        // если в процессе обхода ничего не нашли подходящего, значит нет такого тега
        if (parent == null) {
            if (print_flag) System.out.println("Не найден узел для буквы [" + w + "] текущего имени [" + word + "]. Поиск закончился недачей");
            return null;
        }


        // После этого проверяем, а не дошли ли мы до конца нашего имени?
        if (pos == word.length() - 1) {
            // Если долшли, то отдаем ссылку на нод, в котором совпала последняя буква
            if (print_flag) System.out.println("Поиск имени успешно завершен!");
            return parent;
        }
        else { // Иначе еще можно продолжать рекурсию, предварительно увеличив номер буквы в теге-слове
            pos++;
            return findKeyForUser(parent, word, pos); // и заходим в этого потомка
        }

    }



}
