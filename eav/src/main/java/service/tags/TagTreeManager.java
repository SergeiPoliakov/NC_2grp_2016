package service.tags;

import entities.Log;
import entities.TagNode;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * Created by Hroniko on 23.03.2017.
 */
// Класс-конструктор дерева тегов, тут же все вспомогательные методы работы с тегами (поиск-добавление, поиск юзеров и пр.)
public class TagTreeManager {

    private static final TagNodeTree treeNode = new TagNodeTree(); // собственно центральное дерево тегов, надо подгружать сюда его из базы
    private boolean print_flag = true; // служебный флаг для включения возможности вывода в консоль служебной инфы

    // тут надо сделать метод подгрузки из базы тегов и создания полноценного дерева, а еще методы сброса в базу новых тегов

    // 2017-03-23  Связывает воедино три сервиса - теги в базе, теги в виде дерева в памяти, и сущности tag для передачи в контроллер и дальше на страницу

    // 2017-03-25 Надо еще сделать очередь нодов на добавление в базу (и на обновление в базе, если вешаем юзера),
    // потому что сначала будем из базы строить дерево, а потом работать с ним,
    // и в базу уже заносить только изменения, а не все дерево сначала
    // И тут же надо накапливать все логи, а потом при достижении какого-то фиксированного их значения заносить в базу






    // Конструктор:


    public TagTreeManager() {
        //treeNode = new TagNodeTree();

    }

    // 1) ОСНОВНОЙ Метод получения всех тегов, содержащих в себе заданный тег // идти по нодам рекурсивно, передавая вниз слово, и конкатенировать его с value текущего нода, пока не достигли конца, а потом помещать в лист
    public ArrayList<String> getTagWordList(String base_word){
        ArrayList<String> tag_list = null;
        if (print_flag) System.out.println("Составляем спосок дочерних тегов для базового [" + base_word + "]");
        // Сначала находим нужный нод, (то есть проходим весь путиь до последней буквы)
        TagNode node = treeNode.find(base_word);
        // Если такое такой тег нашли, можем продолжать
        if (node != null){
            tag_list = new ArrayList<>();
            tagWordList(tag_list, node, base_word); // Вызываем вспомогательный рекурсивный метод
        }
        // И когда выполнили весь обход, отдаем лист слов-тегов, которые содержат в себе базовый и также являются узловыми
        return tag_list;
    }

    // 1-1) Вспомогательный (обходим все дочерние ноды):
    private void tagWordList(ArrayList<String> tag_list, TagNode node, String word){
        // Проверяем, есть ли путь дальше вниз по узлам:
        if (node.getParents().size() < 1){
            // Если нет пути дальше, выходим из рекурсии, сохряняя предварительно слово в лист (НО только если к нему приписаны юзеры, а если удалили, то такого тега как бы нет):
            if (node.getUsers().size() > 0){
                tag_list.add(word);
                if (print_flag) System.out.println("В список тегов добавлен новый тег [" + word + "]");
            }
            return;
        }
        // Иначе проверяем, может быть это не конечный, но все равно ключевой нод (если есть привешенные юзеры):
        if (node.getUsers().size() > 0){
            // Сохряняя предварительно слово в лист:
            tag_list.add(word);
            if (print_flag) System.out.println("В список тегов добавлен новый тег [" + word + "]");
            // но уже из рекурсии не выходим, а продолжаем ее
        }
        // Иначе продолжаем рекурсию, обходим всех наследников:
        for (int i = 0; i < node.getParents().size(); i++){
            TagNode parent = node.getParents(i); // вытаскиваем ссылку на наследника,
            char w = parent.getValue(); // и вытаскиваем значение буквы в узле
            //word += w; // Конкатенируем со словом
            tagWordList(tag_list, parent, word + w); // и заходим по рекурсии в этого наследника
        }
    }


    // 2) ОСНОВНОЙ метод получения ids всех юзеров, "подписанных" на данный тег
    public ArrayList<Integer> getUserListWithTag(String word){
        ArrayList<Integer> user_list = null;
        if (print_flag) System.out.println("Составляем список юзеров, у которых есть тег [" + word + "]");
        // Сначала находим нужный нод, (то есть проходим весь путиь до последней буквы)
        TagNode node = treeNode.find(word);
        // Если такое такой тег нашли, можем продолжать
        if (node != null){
            user_list = node.getUsers(); // Просто забираем список ids юзеров у нода
            // Но надо бы еще их и вывести:
            if (print_flag){
                for (Integer id : user_list){
                    System.out.println("В список юзеров добавлен новый юзер [" + id + "]");
                }
            }
        }
        // И отдаем лист ids
        return user_list;
    }

    // 3) ОСНОВНОЙ метод получения ids всех юзеров, у которых есть теги, содерхащие данныйазовый тег:
    public ArrayList<Integer> getUserListWithPartitionTag(String base_word){
        ArrayList<Integer> user_list = null;
        if (print_flag) System.out.println("Составляем спосок юзеров, у которых есть теги, содержащие в себе тег [" + base_word + "]");
        // Сначала находим нужный нод, (то есть проходим весь путиь до последней буквы)
        TagNode node = treeNode.find(base_word);
        // Если такое такой тег нашли, можем продолжать
        if (node != null){
            user_list = new ArrayList<>();
            userWordList(user_list, node); // Вызываем вспомогательный рекурсивный метод
        }
        // И когда выполнили весь обход, отдаем лист слов-тегов, которые содержат в себе базовый и также являются узловыми
        return user_list;
    }

    // 3-1) Вспомогательный для 3 (обходим все дочерние ноды):
    private void userWordList(ArrayList<Integer> user_list, TagNode node){
        // Проверяем, может быть это не конечный, но все равно ключевой нод (если есть привешенные юзеры):
        if (node.getUsers().size() > 0){
            // Сохряняем предварительно ids юзеров в лист:
            for(int i = 0; i < node.getUsers().size(); i++){
                Integer id = node.getUsers(i);
                if (!user_list.contains((Object) id)){ // Проверяем, вдруг такого юзера мы уже занесли в список, зачем же дублировать. Если его нет, то вставляем
                    user_list.add(id);
                    if (print_flag) System.out.println("В список юзеров добавлен новый юзер [" + id + "]");
                }
            }
        }
        // Проверяем, есть ли путь дальше вниз по узлам:
        if (node.getParents().size() < 1){
            // Если нет пути дальше, выходим из рекурсии:
            return;
        }

        // Иначе продолжаем рекурсию, обходим всех наследников:
        for (int i = 0; i < node.getParents().size(); i++){
            TagNode parent = node.getParents(i); // вытаскиваем ссылку на наследника,
            userWordList(user_list, parent); // и заходим по рекурсии в этого наследника
        }
    }

    // 4) Метод добавления нового тега в дерево
    public void addTag(String word) throws SQLException {
        treeNode.insert(word);
    }

    // 5) Метод добавления нового тега в дерево
    public void addTag1(String word) throws SQLException {
        treeNode.insert(word);
    }



    // сделал - см выше - Метод, получающий всех юзеров, привешенных  именно к конкретному узлу


    // сделал - см выше - Метод, получающих всех юзеров НИЖе данного нода


    // Тестовый метод:
    public void test() throws SQLException {
        // Создаем новое центральное дерево:
        // this.treeNode = new TagNodeTree(); - не надо уже, так как статическая ссылка на дерево
        // Автоматически к нему вешается корневой узел root, к которому можно будет вешать все остальные
        // Пробуем повесить теги:
        System.out.println("\n!!!!! Получена команда на добавление тега-слово [авто]");
        treeNode.insert("авто");

        System.out.println("\n!!!!! Получена команда на добавление тега-слово [автомобиль]");
        treeNode.insert("автомобиль", 10001);

        System.out.println("\n!!!!! Получена команда на добавление тега-слово [АвтомобиЛЬ]");
        treeNode.insert("АвтомобиЛЬ");

        System.out.println("\n!!!!! Получена команда на добавление тега-слово [автомойка]");
        treeNode.insert("автомойка");

        System.out.println("\n!!!!! Получена команда на добавление тега-слово [автомобильпроверкадлинных]");
        treeNode.insert("автомобильпроверкадлинных");
        //treeNode.insert("автомобильпроверкадлинных");

        System.out.println();

        // А теперь пробуем найти их:
        TagNode tagNode1 = treeNode.find("авто");
        if (tagNode1 != null) {
            System.out.println("Нашли тег [авто]\n\n");
            System.out.println("Он содержит букву [" + tagNode1.getValue() + "] и ссылку на юзера [" + tagNode1.getUsers(0) + "]"); // System.out.println("Он содержит букву [" + tagNode1.getValue() + "] и ссылку на юзера [" + tagNode1.getUsers().get(0) + "]"); //
        } else {
            System.out.println("НЕ нашли тег [авто]\n\n");
        }

        TagNode tagNode2 = treeNode.find("автомобиль");
        if (tagNode2 != null) {
            System.out.println("Нашли тег [автомобиль]");
            System.out.println("Он содержит букву [" + tagNode2.getValue()+ "] и ссылку на юзера [" + tagNode2.getUsers(0) + "]");
        } else {
            System.out.println("НЕ нашли тег [автомобиль]");
        }

        TagNode tagNode3 = treeNode.find("АвтомобиЛЬ");
        if (tagNode3 != null) {
            System.out.println("Нашли тег [АвтомобиЛЬ]");
            System.out.println("Он содержит букву [" + tagNode3.getValue()+ "] и ссылку на юзера [" + tagNode3.getUsers(0) + "]");
        } else {
            System.out.println("НЕ нашли тег [АвтомобиЛЬ]");
        }
        // и те, которых нет
        TagNode tagNode4 = treeNode.find("Автомо");
        if (tagNode4 != null) {
            System.out.println("Нашли тег [Автомо]");
            System.out.println("Он содержит букву [" + tagNode4.getValue()+ "] и ссылку на юзера [" + tagNode4.getUsers(0) + "]");
        } else {
            System.out.println("НЕ нашли тег [Автомо]");
        }

        TagNode tagNode5 = treeNode.find("Автобобин");
        if (tagNode5 != null) {
            System.out.println("Нашли тег [Автобобин]");
            System.out.println("Он содержит букву [" + tagNode5.getValue()+ "] и ссылку на юзера [" + tagNode5.getUsers(0) + "]");
        } else {
            System.out.println("НЕ нашли тег [Автобобин]");
        }

        // А теперь попробуем удалить (не нод, а юзера из нода, а это равносильно удалению тега у юзера):
        treeNode.deleteUserFromTagNode("авто", 10001); // удаляем юзера, которого заведомо там нет

        // А теперь пробуем найти их:
        tagNode1 = treeNode.find("авто");
        if (tagNode1 != null) {
            System.out.println("Нашли тег [авто]");
            System.out.println("Он содержит букву [" + tagNode1.getValue() + "] и ссылку на юзера [" + tagNode1.getUsers(0) + "]"); // System.out.println("Он содержит букву [" + tagNode1.getValue() + "] и ссылку на юзера [" + tagNode1.getUsers().get(0) + "]"); //
        } else {
            System.out.println("НЕ нашли тег [авто]");
        }

       /*
        this.treeNode.deleteUserFromTagNode("авто", 10003); // удаляем юзера, который точно там есть

        // А теперь пробуем найти их:
        tagNode1 = this.treeNode.find("авто");
        if (tagNode1 != null) {
            System.out.println("Нашли тег [авто]");
            System.out.println("Он содержит букву [" + tagNode1.getValue() + "] и ссылку на юзера [" + tagNode1.getUsers(0) + "]"); // System.out.println("Он содержит букву [" + tagNode1.getValue() + "] и ссылку на юзера [" + tagNode1.getUsers().get(0) + "]"); //
        } else {
            System.out.println("НЕ нашли тег [авто]");
        }
        */

        // А теперь пробуем найти все теги, содержащие данный:
        ArrayList<String> words = getTagWordList("авт");

        // А теперь ищем всех юзеров с тегом "авто"
        System.out.println();
        ArrayList<Integer> ul = getUserListWithTag("авто");
        System.out.println();

        // И все юзеров, у которыхеги содержат заданный тег "авто"
        System.out.println();
        ArrayList<Integer> ul2 = getUserListWithPartitionTag("авто");
        System.out.println();

    }

    // Тестовый метод:
    public void test2() throws SQLException {
        // Создаем новое центральное дерево:
        //this.treeNode = new TagNodeTree();
        // Автоматически к нему вешается корневой узел root, к которому можно будет вешать все остальные
        // Пробуем повесить теги:
        treeNode.insert("а", 10001);

        // А теперь пробуем найти их:
        TagNode tagNode1 = this.treeNode.find("а");
        if (tagNode1 != null) {
            System.out.println("Нашли тег [а]");
            System.out.println("Он содержит букву [" + tagNode1.getValue() + "] и ссылку на юзера [" + tagNode1.getUsers(0) + "]"); //
        } else {
            System.out.println("НЕ нашли тег [а]");
        }


    }



    // Тестовый метод:
    public void test3() throws SQLException {
        // Создаем новое центральное дерево:
        System.out.println("\n!!!!! Получена команда на добавление тега-слово [ав]");
        treeNode.insert("ав");

        System.out.println("\n!!!!! Получена команда на добавление тега-слово [аc]");
        treeNode.insert("аc", 10001);
        System.out.println();
    }
}
