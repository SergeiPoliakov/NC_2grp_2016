package service.tags;

import dbHelp.DBHelp;
import entities.DataObject;
import entities.Event;
import entities.TagNode;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.converter.Converter;
import service.id_filters.TagFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Hroniko on 22.03.2017.
 */
// Класс нагруженного дерева тегов
public class TagNodeTree {

    private static volatile TagNodeTree instance;

    private boolean print_flag = true; // служебный флаг для включения возможности вывода в консоль служебной инфы
    private UserServiceImp userService = new UserServiceImp();
    private LoadingServiceImp loadingService = new LoadingServiceImp();

    // Поля и методы самого дерева:
    private TagNode root = TagNode.getInstance(); // Ссылка на корневой узел

    // private long height, depth, size; // Служебные поля: высота, глубина и размер дерева

    private static final Integer max_count = 2; // 10 Максимальное количество нодов для хранения в накопителе, при превышении сброс нодов в базу
    // Общая очередь новых нодов на всех юзеров
    private static final Queue<TagNode> newTagQueue = new ArrayBlockingQueue<>(max_count + 1); // Очередь новых нодов (их потом надо будет перенести в базу)
    // Общая очередь всех нодов, существующих в базе, но подлежащих обновлению (например, к ним добавили юзера или удалили юзера)
    private static final Queue<TagNode> updTagQueue = new ArrayBlockingQueue<>(max_count + 1); // Очередь нодов на обновление (их потом надо будет обновить в базе)

    private static Integer max_id = 90_000; // переменная для хранения максимального значения айдишника нода в дереве тегов, чтобы потом можно было при генерации знать, от чего увеличивать id


    public static TagNodeTree getInstance() {
        if (instance == null)
            synchronized (TagNodeTree.class) {
                if (instance == null)
                    instance = new TagNodeTree();
            }
        return instance;
    }

    // Конструктор:
    public TagNodeTree() {
        //this.root = new TagNode();
        try {
            this.loadAndCreateTagNodeTree();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        } finally {
            this.root = TagNode.getInstance();
        }
        //this.height = 0;
        //this.depth = 0;
        //this.size = 0;
    }

    // Метод-генератор нового айди для нвоого нода:
    private static Integer generateId() {
        max_id++; // Увеличиваем
        System.out.println("Сгенерирован новый id для узла [" + max_id + "]");
        return max_id; // и возвращаем
    }

    // 0) Нужен еще метод загрузки и создания дерева из базы:
    private void loadAndCreateTagNodeTree() throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        //this.root = new TagNode(); // Создаем базовый узел, на всякий пожарный, потом его перезапишем
        // И надо получить из базы список датаобджектов нодов, преобразовать в ноды и потом его обойти, находя в дереве место, куда подвесить этот нод
        // Получаем список всех узлов тегов:
        ArrayList<Integer> tag_node_ids = loadingService.getListIdFilteredAlternative(new TagFilter(TagFilter.ALL));
        System.out.println("Список загружаемых из базы нодов :" + tag_node_ids);
        // Получаем список всех датаобджектов узлов тегов:
        //ArrayList<DataObject> tag_node_list = new DBHelp().getListObjectsByListIdAlternative(tag_node_ids); // лучше не так, а в цикле через одиночный метод, тогда все ноды будут точно отсортированы по
        // айдишнику, поскольку список айди отсортирован по возрастанию, избежим лишней работы с поиском
        // Получаем список всех датаобджектов узлов тегов:
        ArrayList<DataObject> tag_node_list = new ArrayList<>();
        for(int i = 0; i < tag_node_ids.size(); i++){
            Integer id = tag_node_ids.get(i);
            DataObject dataObject =  new DBHelp().getObjectsByIdAlternative(id);
            System.out.println("Загружен нод:" + dataObject.getId() + " : " + dataObject.getRefParams());
            tag_node_list.add(dataObject); // подгружаем по одному из базы и закидываем в список
        }


        // и устанавливаем максимальный номер среди нодов дерева, чтобы потом генератор правильно работал:
        max_id = tag_node_ids.get(tag_node_ids.size()-1);

        // Первый в списке и есть root_node, его возьмем и в рекурсии будем обходить весь список, развешивая потомков
        this.root = TagNode.getInstance(); // Создаем базовый узел


        createTagNodeTree(tag_node_list, tag_node_ids, root, 0); // рекурсивно строим дерево


    }


    // 0-1) Вспомогательный метод создания дерева для метода 0) (рекурсия):
    private void createTagNodeTree(ArrayList<DataObject> tag_node_list, ArrayList<Integer> tag_node_ids, TagNode node, int pos) {

        DataObject dataObject = tag_node_list.get(pos);

        node.setId(tag_node_ids.get(pos));

        System.out.println("К этой позиции добавлен элемент " + node.getId());


        node.setName(dataObject.getName());

        System.out.println("Текущий нод " + node);


        // Переносим параметры:
        for (Map.Entry<Integer, String> param : dataObject.getParams().entrySet() ) {
            System.out.println("Текущие параметры нода: " + param);
            switch (param.getKey()){
                case (701): // 701 // Значение (буква)
                    if (pos > 0) node.setValue(param.getValue().charAt(0));
                   /* {
                        //
                        String tmp = param.getValue();
                        char [] chars = tmp.toCharArray();
                        node.setValue(chars[0]);
                        System.out.println("Текущая буква нода: " + node.getValue());
                    }*/
                    // node.setValue(param.getValue().charAt(0));
                    break;
                case (702): // 702 // Число использования
                    if (pos > 0) node.setUsage_count(Integer.parseInt(param.getValue()));
                    /*
                    {
                    int us_count = Integer.parseInt(param.getValue());
                    node.setUsage_count(us_count);
                    }*/

                    // node.setUsage_count(Integer.getInteger(param.getValue()));
                    break;
            }
        }

        System.out.println("Перенесли параметры нода");

        // Переносим ссылки:
        ArrayList<Integer> parent_ids = new ArrayList<>();
        // получаем все ссылки на потомков (тег ноды) и юзеров (их айдишники):
        for (Map.Entry<Integer, ArrayList<Integer>> reference : dataObject.getRefParams().entrySet()) {
            System.out.println("Текущие ссылки нода: " + reference);
            switch (reference.getKey()) {
                case (704): // parents
                    for (Integer refValue : reference.getValue()) {
                        parent_ids.add(refValue);
                    }
                    break;
                case (705): // users
                    for (Integer refValue : reference.getValue()) {
                        node.addUserId(refValue);
                    }
                    break;
            }
        }

        if (pos == 0) {
            System.out.println("В дерево добавлен базовый нод тегов: {" + root.getId() + " : " + root.getName() + "}");
        }
        else{
            System.out.println("В дерево добавлен дочерний нод тегов: {" + node.getId() + " : " + node.getName() + "}");
        }

        // А теперь обходим (и создаем ноды) всех потомков:
        //if (parent_ids.size() < 1) return;

        System.out.println("Количество дочерних нод-тегов: " + parent_ids.size());
        System.out.println("Дочерние нод-теги: " + parent_ids);

        for (int i = 0; i < parent_ids.size(); i++){
            int parent_id = parent_ids.get(i); // Получаем айдишник текущего потомка
            TagNode parent = new TagNode(); // Создаем новый узел
            parent.setRoot(node); // Устанавливаем родителя
            node.setParents(parent); // Устанавливаем потомка родителю



            pos = tag_node_ids.indexOf(parent_id); // Определяем следующую позицию
            System.out.println("Следующая позиция: " + pos);

            // И рекурсивно заходим в потомка:
            createTagNodeTree(tag_node_list, tag_node_ids, parent, pos);
        }

    }


    // 1) Метод добавления тега-слова для переданного юзера в дерево (основной):
    public void insert(String tag_word, Integer user_id) {
        // Сначала дополнительные проверки:
        if (tag_word == null) {
            return;
        }
        if (tag_word.length() == 0) {
            return;
        }
        // 2017-03-23 Надо еще автоматически приводить буквы в слове к нижнему регистру!!! Чтобы не дублировать одно и то же в разных регистрах
        tag_word = tag_word.toLowerCase();

        System.out.println("ДО ОТПРАААААААААААВКИ" + this.root);
        insertKey(this.root, tag_word, 0, user_id);
    }

    // 1a) Метод добавления тега-слова для текущего юзера в дерево (основной):
    public void insert(String tag_word) throws SQLException {
        Integer user_id = userService.getObjID(userService.getCurrentUsername());
        insert(tag_word, user_id);
    }

    // 2) Метод поиска тега-слова в дереве (основной):
    public TagNode find(String tag_word) {
        // Сначала дополнительные проверки:
        if (tag_word == null) {
            return null;
        }
        if (tag_word.length() == 0) {
            return null;
        }
        // 2017-03-23 Надо еще автоматически приводить буквы в слове к нижнему регистру!!! Чтобы не дублировать одно и то же в разных регистрах
        tag_word = tag_word.toLowerCase();
        return findKey(this.root, tag_word, 0);
    }

    // 3) Метод удаления (удаляются только подписанные юзеры из ключевых нодов, а не сами ноды! Иначе все другие юзеры потеряют теги)
    public void deleteUserFromTagNode(String tag_word, Integer user_id) {
        // Сначала дополнительные проверки:
        if (tag_word == null) {
            return;
        }
        if (tag_word.length() == 0) {
            return;
        }
        deleteKey(this.root, tag_word, 0, user_id);
    }


    // 1-1) Метод вставки узла в дерево (вспомогательный, рекурсия): // node - текущий узел, слово, номер буквы в слове, айди юзера-хранителя тега
    private void insertKey(TagNode node, String word, int pos, int id_user) {
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
                if (print_flag) System.out.println("Нашли узел для буквы [" + w + "] текущего тега [" + word + "]");
                break;
            }
        }

        // A-2 если в процессе обхода ничего не нашли подходящего, создадим сами
        if (parent == null){
            if (print_flag) System.out.println("Не нашли узел для буквы [" + w + "] текущего тега [" + word + "]");
            if (print_flag) System.out.println("Создаю узел для буквы [" + w + "] текущего тега [" + word + "]");
            parent = new TagNode(); // Создаем новый узел с установкой ссылки на родителя и установкой значения буквы
            parent.setRoot(node);
            parent.setValue(w);
            parent.setId(generateId()); // Генерируем новый айди и вставляем его в потомка
            parent.setName("tag_node_" + parent.getId()); // Устанавливаем имя потомка
            node.setParents(parent); // и добавляем в список потомков текущего узла (родителя)
            System.out.println("Текущее состояние родителя: " + node);
            System.out.println("Текущее состояние потомка: " + parent);

            try {
                addToNewTagQueue(parent); // !!!! Также добавляем в очередь на перенос в базу (создание) наследника
                addToUpdTagQueue(node); // и обновляем в базе самого родителя (у него добавятся ссылки на потомков)
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // A-3 После этого проверяем, а не дошли ли мы до конца нашего слова-тега? (у нас уже точно есть новый нод, в который мы можем зайти)
        if (pos == word.length() - 1) {
            // Если долшли, то добавляем ссылки на юзера в наш parent-узел:
            parent.addUserId(id_user); // добавляем юзера
        }
        else { // Иначе еще можно продолжать рекурсию, предварительно увеличив номер буквы в теге-слове
            pos++;
            insertKey(parent, word, pos, id_user); // и заходим в этого потомка
        }

        // К завершению метода обе очереди будут либо наполнены, либо наполнены и уже перенесены в базу
    }

    // 2-1) Метод поиска узла в дереве (вспомогательный, рекурсия)
    private TagNode findKey(TagNode node, String word, int pos) {

        char w = word.charAt(pos);
        if (print_flag) System.out.println("Ищу узел для буквы [" + w + "] текущего тега [" + word + "]");

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
                if (print_flag) System.out.println("Нашли узел для буквы [" + w + "] текущего тега [" + word + "]");
                break;
            }
        }

        // A-2 если в процессе обхода ничего не нашли подходящего, значит нет такого тега
        if (parent == null) {
            if (print_flag) System.out.println("Не найден узел для буквы [" + w + "] текущего тега [" + word + "]. Поиск закончился недачей");
            return null;
        }


        // A-3 После этого проверяем, а не дошли ли мы до конца нашего слова-тега?
        if (pos == word.length() - 1) {
            // Если долшли, то отдаем ссылку на нод, в котором совпала последняя буква
            if (print_flag) System.out.println("Поиск успешно завершен!");
            return parent;
        }
        else { // Иначе еще можно продолжать рекурсию, предварительно увеличив номер буквы в теге-слове
            pos++;
            return findKey(parent, word, pos); // и заходим в этого потомка
        }

    }

    // 3-1)  Метод удаления (вспомогательный)
    private void deleteKey(TagNode node, String word, int pos, int id_user) {
        if (print_flag)
            System.out.println("Получил команду на удаление у юзера [" + id_user + "] тега с именем [" + word + "]");
        // Сначала получим этот нод через поиск:
        TagNode find_node = findKey(node, word, pos);
        // А затем, если он не null, зайдем в него и удалим данного юзера (его айди из листа):
        if (find_node != null) {
            if (find_node.getUsers().contains(id_user)) {
                find_node.delUsers(id_user);
                try {
                    addToUpdTagQueue(node); // и обновляем в базе сам нод (у него удаляться ссылки на потомков)
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (print_flag) System.out.println("Удалили у юзера [" + id_user + "] тег с именем [" + word + "]");
            } else {
                if (print_flag)
                    System.out.println("Удалять нечего, у юзера [" + id_user + "] нет тега с именем [" + word + "]");
            }
        } else {
            if (print_flag) System.out.println("Не нашли в дереве тега с именем [" + word + "]");
        }
    }


    // Методы обслуживания очередей нодов:

    // 4) Добавление нового узла на создание в базе
    private void addToNewTagQueue(TagNode tagNode) throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        if(newTagQueue.contains(tagNode)) return; // Если очередь уже содержит задание на создание такого узла, то дублировать не стоит
        // Иначе же полноценно добавляем:
        newTagQueue.add(tagNode);

        System.out.println(" ::: Добавление нового тега [" + tagNode.getName() + "] в очередь на добавление в базу, размер очереди: " + newTagQueue.size()); // System.out.println(log.getDate() + " ::: Добавление лога в очередь, размер очереди: " + logQueue.size());
        // Проверяем, не пора ли переносить в базу:
        if (newTagQueue.size() == max_count) {
            loadToDB(); // Пора скидывать в базу
        }
    }

    // 5) Добавление нового узла на обновление в базе
    private  void addToUpdTagQueue(TagNode tagNode) throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        if(updTagQueue.contains(tagNode)) return; // Если очередь уже содержит задание на обновление такого узла, то дублировать не стоит
        if(newTagQueue.contains(tagNode)) return; // А также если в очереди на добавление все еще стоит этот нод, то обновлять не стоит
        // Иначе же полноценно добавляем:
        updTagQueue.add(tagNode);

        System.out.println(" ::: Добавление нового тега [" + tagNode.getName() + "] в очередь на обновление в базе, размер очереди: " + updTagQueue.size()); // System.out.println(log.getDate() + " ::: Добавление лога в очередь, размер очереди: " + logQueue.size());
        // Проверяем, не пора ли переносить в базу:
        if (updTagQueue.size() == max_count) {
            loadToDB(); // Пора скидывать в базу
        }
    }


    // Реализовать методы переноса в базу и обновления базы!!!!!!
    // Получение нода тега из начала внутренней очереди на добавление в бд (с удалением из очереди)
    synchronized private static TagNode removeNewTagQueue(){
        return newTagQueue.remove();
    }
    // Получение нода тега из начала внутренней очереди на обновление в бд (с удалением из очереди)
    synchronized private static TagNode removeUpdTagQueue(){
        return updTagQueue.remove();
    }

    // 6) Метод переноса новых узлов тегов в базу
    public void loadToDB() throws SQLException, NoSuchMethodException, IllegalAccessException, ParseException, InvocationTargetException {
        // Берем очередь, обходим ее, вытаскиваем ноды, делаем из них датаобджекты и set в базу
        System.out.println(new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm").format( new java.util.Date()) + " :: Старт копирования новых тегов в базу (count = " + newTagQueue.size() + "): ");
        Converter converter = new Converter();
        int i = 0;
        ArrayList<DataObject> newTagList = new ArrayList<>();
        while (newTagQueue.size() > 0){
            i++;
            TagNode node = removeNewTagQueue();
            System.out.print(i + " ");
            // А тут надо перенести в базу
            // Конвертируем в датаобджект:
            DataObject dataObject = converter.toDO(node);
            // Переносим в массив:
            newTagList.add(dataObject);
        }

        System.out.println(new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm").format( new java.util.Date()) + " :: Старт копирования обновляемых тегов в базу (count = " + updTagQueue.size() + "): ");

        i = 0;
        ArrayList<DataObject> updTagList = new ArrayList<>();
        while (updTagQueue.size() > 0){
            i++;
            TagNode node = removeUpdTagQueue();
            System.out.print(i + " ");
            // А тут надо перенести в базу
            // Конвертируем в датаобджект:
            DataObject dataObject = converter.toDO(node);
            // Переносим в массив:
            updTagList.add(dataObject);
        }

        // и переносим в базу:
        new DBHelp().setDataObjectTag(newTagList, updTagList);

        System.out.println("\n" + new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm").format( new java.util.Date()) + " :: Конец копирования новых тегов в базу.");
    }



    public static Integer getMax_count() {
        return max_count;
    }

    public static Queue<TagNode> getNewTagQueue() {
        return newTagQueue;
    }

    public static Queue<TagNode> getUpdTagQueue() {
        return updTagQueue;
    }
}
