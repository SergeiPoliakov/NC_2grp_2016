package service.tags;

import entities.TagNode;
import entities.User;

import java.util.ArrayList;

/**
 * Created by Hroniko on 13.05.2017.
 */

// Класс-конструктор дерева имен для динамического поиска, тут же все вспомогательные методы работы с ним
public class NameTreeManager {

    private static final NameNodeTree nameNode = NameNodeTree.getInstance(); // собственно центральное дерево имен, надо подгружать сюда его из базы

    // 1 Метод получения списка пользователей по переданному списку имен (по сути, считаем, что передали ФИО)
    public ArrayList<User> findAllUsersWithNames(ArrayList<String> names2, String AND_OR_FLAG){

        // 0 Уточнение: пока вводим первое имя, поиск работает как достраивание имени до полного (ива -> иван, иванов и пр), а со второго слова уже нормально
        // if (names.size() == 1) names = getNameListForUser(names.get(0));

        // 1 Новое уточнение: каждое имя пытаться достраивать до полного:
        ArrayList<String> names = new ArrayList<>();
        for(int i = 0; i < names2.size(); i++){
            names.addAll(getNameListForUser(names2.get(i)));
        }

        ArrayList<User> userList = new ArrayList<>();
        ArrayList<Integer> users = new ArrayList<>();

        if (AND_OR_FLAG.equals("and")){ // 1 Обходим весь список и постепенно "уточняем" ответ за счет выкидывания из списка не совпадающих айди
            for(int i = 0; i < names.size(); i++){
                String name = names.get(i);
                // Получаем из дереа имен финальный узел (со списком юзеров), соответствующий переданному имени
                TagNode userNode = nameNode.findForUser(name);
                // Проверяем, нашли ли мы хоть что-то:
                ArrayList<Integer> newUsers = userNode.getUsers();
                if (userNode == null || newUsers == null) break; // Если не нашли нода или у нода нет юзеров, то нет смысла искать далее, так как по логике у нас AND и меньшее перекрывает большее
                // Если же не вылетели на предыдущем запросе, проверяем, не первый ли это запрос:
                if (i == 0) {
                    users = newUsers; // и если первый, просто подвешиваем полученный список айдишек пользователей
                }
                else{
                    // Иначе надо сравнить, что мы уже имеем, с тем, что получили в текущем запросе, и все что не совпадает убрать из списка пользователей:

                    for (int j = 0; j < users.size(); ){
                        if (newUsers.contains(users.get(j))){
                            // если содержится, просто переходим к следующему в списке
                            j++;
                        }
                        else{
                            // Иначе, если не содержится, удаляем его из списка:
                            users.remove(j);
                        }

                    }
                }
                // И если после этого спиоск юзеров стал нулевой длины, просто выходим из цикла
                if (users.size() == 0) break;

            }
        }
        else { // or 2 Обходим весь список и постепенно "наращиваем" ответ за счет добавления в список не совпадающих айди
            for(int i = 0; i < names.size(); i++){
                String name = names.get(i);
                // Получаем из дерева имен финальный узел (со списком юзеров), соответствующий переданному имени
                TagNode userNode = nameNode.findForUser(name);
                // Проверяем, нашли ли мы хоть что-то:
                ArrayList<Integer> newUsers = userNode.getUsers();
                if (userNode == null || newUsers == null) continue; // Если не нашли нода или у нода нет юзеров, то надо перейти к следующему слову и проводить поиск для него
                // Если же не вылетели на предыдущем запросе, проверяем, не первый ли это запрос:
                if (i == 0) {
                    users = newUsers; // и если первый, просто подвешиваем полученный список айдишек пользователей
                }
                else{
                    // Иначе надо сравнить, что мы уже имеем, с тем, что получили в текущем запросе, и все что не совпадает пополнять:

                    for (int j = 0; j < newUsers.size(); ){
                        if (users.contains(newUsers.get(j))){
                            // если содержится, просто переходим к следующему в списке
                            j++;
                        }
                        else{
                            // Иначе, если не содержится, добавляем его к списку:
                            users.add(newUsers.get(j));
                        }
                    }
                }
            }
        }

        // 2 А теперь для полученного списка айдишек получаем пользователей
        for(Integer user_id : users){
            User user = nameNode.getUserById(user_id);
            if (user != null) userList.add(user);
        }

        return  userList;


    }




    // 1) ОСНОВНОЙ Метод получения всех имен, содержащих в себе заданный частьимени
    public ArrayList<String> getNameListForUser(String base_word){
        ArrayList<String> tag_list = null;
        // Сначала находим нужный нод, (то есть проходим весь путиь до последней буквы)
        TagNode node = nameNode.findForUser(base_word);
        // Если такое такой тег нашли, можем продолжать
        if (node != null){
            tag_list = new ArrayList<>();
            nameListForUser(tag_list, node, base_word); // Вызываем вспомогательный рекурсивный метод
        }
        // И когда выполнили весь обход, отдаем лист слов-тегов, которые содержат в себе базовый и также являются узловыми
        return tag_list;
    }

    // 1-1) Вспомогательный (обходим все дочерние ноды):
    private void nameListForUser(ArrayList<String> tag_list, TagNode node, String word){
        // Проверяем, есть ли путь дальше вниз по узлам:
        if (node.getParents().size() < 1){
            // Если нет пути дальше, выходим из рекурсии, сохраняя предварительно слово в лист (НО только если к нему приписаны юзеры, а если удалили, то такого тега как бы нет):
            if (node.getUsers().size() > 0){
                tag_list.add(word);
            }
            return;
        }
        // Иначе проверяем, может быть это не конечный, но все равно ключевой нод (если есть привешенные юзеры):
        if (node.getUsers().size() > 0){
            // Сохряняя предварительно слово в лист:
            tag_list.add(word);
            // но уже из рекурсии не выходим, а продолжаем ее
        }
        // Иначе продолжаем рекурсию, обходим всех наследников:
        for (int i = 0; i < node.getParents().size(); i++){
            TagNode parent = node.getParents(i); // вытаскиваем ссылку на наследника,
            char w = parent.getValue(); // и вытаскиваем значение буквы в узле
            //word += w; // Конкатенируем со словом
            nameListForUser(tag_list, parent, word + w); // и заходим по рекурсии в этого наследника
        }
    }


}
