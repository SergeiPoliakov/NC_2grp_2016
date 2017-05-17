package service.search;

import entities.User;
import service.tags.NameTreeManager;
import service.tags.TagTreeManager;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by Hroniko on 30.03.2017.
 */
// Класс для обработки логики запросов (OR | AND, USER | MEETING) и пр., потом будет расширяться
public class FinderLogic {


    public static ArrayList<FinderTagResponse> getWithLogic(FinderTagRequest finder) {

        if (finder.getType().equals("pre_name")) return preFindUser(finder); // работаем с юзерами, запускаем предварительный поиск
        if (finder.getType().equals("name")) return FindUser(finder); // работаем с юзерами, запускаем окончательный поиск

        // Пропускаем через парсер строку с тегами:

        ArrayList<String> part_tags = SearchParser.parse(finder.getText());
        // Подготавливаем список тегов для ответа:
        ArrayList<FinderTagResponse> finderTagResponseList = new ArrayList<>();
        System.out.println("Пришел запрос на тег [" + finder.getText() + "]");

        // По каждому тегу делаем запросы в дерево тегов:


        if (part_tags != null) {
            TagTreeManager ttm = new TagTreeManager();
            label:
            for (int i = 0; i < part_tags.size(); i++) {
                ArrayList<String> anyTagFromTree = new ArrayList<>();

                switch (finder.getType()) {

                    case "user":
                        // работаем с юзерами, загружаем подходящие теги с подвешенными юзерами
                        anyTagFromTree = ttm.getTagWordListForUser(part_tags.get(i));
                        break;
                    case "meeting":
                        // работаем со встречами, загружаем подходящие теги с подвешенными встречами
                        anyTagFromTree = ttm.getTagWordListForMeeting(part_tags.get(i));
                        break;
                    default:
                        break label;
                }

                if (anyTagFromTree == null) {
                    return finderTagResponseList;
                }


                if (i == 0) { // Если это первый тег из запроса, то просто переносим в лист все найденные теги из ответа
                    for (int j = 0; j < anyTagFromTree.size(); j++) {
                        FinderTagResponse finderTagResponse = new FinderTagResponse();
                        finderTagResponse.setId(j);
                        finderTagResponse.setText(anyTagFromTree.get(j));
                        finderTagResponseList.add(finderTagResponse);
                    }
                } else { // Иначе начинается самый трэш
                    // Если у нас стоит логика обединения результатов нескольких тегов-запросов как OR, то все хорошо, просто проверяем на наличие уже такого тега и добавляем если его нет
                    if (finder.getOperation().equals("or")) {


                        for (int j = 0; j < anyTagFromTree.size(); j++) {
                            boolean flagOK = false;
                            for (FinderTagResponse aFinderTagResponseList : finderTagResponseList) {
                                if (aFinderTagResponseList.getText().equals(anyTagFromTree.get(j))) { // Если совпадают теги, то не добавляем
                                    flagOK = true;
                                    break;
                                }
                            }
                            // Проверяем флажок:
                            if (!flagOK) { // Если флаг сброшен, то совпадений не нашли, потому надо добавить текущий тег
                                FinderTagResponse finderTagResponse = new FinderTagResponse();
                                finderTagResponse.setId(j);
                                finderTagResponse.setText(anyTagFromTree.get(j));
                                finderTagResponseList.add(finderTagResponse);
                            }
                        }


                    }
                    // Иначе если у нас стоит логика обединения результатов нескольких тегов-запросов как AND, то все фигово, проверяем на совпадение привешенных юзеров (или встреч) и несовпадающие узлы из первого результата удаляем
                    else if (finder.getOperation().equals("and")) {
                        System.out.println("Размер листа " + finderTagResponseList.size());
                        for (int k = 0; k < finderTagResponseList.size(); k++) { // Пробегаем по всем существующим в ответе тегам
                            // получаем для текущего тега список подписанных юзеров (или встреч)
                            ArrayList<Integer> ids = new ArrayList<>();
                            if (finder.getType().equals("user")) {
                                // работаем с юзерами, загружаем подходящие айди с подвещенными юзерами
                                ids = ttm.getUserListWithTag(part_tags.get(k));
                            } else if (finder.getType().equals("meeting")) {
                                // работаем со встречами, загружаем подходящие айди с подвещенными встречами
                                ids = ttm.getMeetingListWithTag(part_tags.get(k));
                            }

                            // А затем пробегаем по всем вновь прибывшим тегам и смотрим совпадение юзеров:
                            for (String anAnyTagFromTree : anyTagFromTree) { // Пробегаем по всем вновь прибывшим тегам
                                ArrayList<Integer> ids2 = new ArrayList<>();
                                if (finder.getType().equals("user")) {
                                    // работаем с юзерами, загружаем подходящие айди с подвещенными юзерами
                                    ids2 = ttm.getUserListWithTag(anAnyTagFromTree);
                                } else if (finder.getType().equals("meeting")) {
                                    // работаем со встречами, загружаем подходящие айди с подвещенными встречами
                                    ids2 = ttm.getMeetingListWithTag(anAnyTagFromTree);
                                }

                                boolean flagOK = false; // Флаг, показывающий, что данный тег надо оставить
                                // Пробегаем по всем айдишникам тега из списка на отправку
                                for (Integer id : ids) {
                                    if (ids2.contains(id)) { // сли попали на совпадение тегов, то выставляем флаг и выходим з этого внуреннего цикла
                                        flagOK = true;
                                        break;
                                    }
                                }

                                // Проверяем флажок:
                                if (!flagOK) { // Если флаг сброшен, то совпадений не нашли, потому надо удалить текущий тег из списка на отправку
                                    if (!finderTagResponseList.isEmpty()) {
                                        finderTagResponseList.remove(k);
                                    }
                                }

                            }

                        }

                    }

                }

            }
        } else return finderTagResponseList;

        return finderTagResponseList;
    }


    // 2017-05-13 Метод динамического (предварительного) поиска юзера (по ФИО, а не по тегам в данном случае) // Теперь всегда ищет по OR !!!
    private static ArrayList<FinderTagResponse> preFindUser(FinderTagRequest finder){
        // 1 Пропускаем через парсер строку запроса и разбираем на массив слов:
        ArrayList<String> words = SearchParser.parse(finder.getText());
        // 2 Подготавливаем список подсказок для ответа (в поле динамического поиска):
        ArrayList<FinderTagResponse> finderResponseList = new ArrayList<>();
        // 3 Делаем запрос и получаем список пользователей через методы NameTreeManager'а:
        ArrayList<User> users = new NameTreeManager().findAllUsersWithNames(words, "or");

        // 4 Обходим всех пользователей и формируем список ответов:
        for (User user : users){
            Integer user_id = user.getId();
            String user_name = user.getSurname() + " " + user.getName() + " " + user.getMiddleName();
            finderResponseList.add(new FinderTagResponse(user_id, user_name));
        }

        return finderResponseList;
    }


    // 2017-05-17 Метод окончательного поиска юзера (по ФИО, а не по тегам в данном случае) по OR или AND
    private static ArrayList<FinderTagResponse> FindUser(FinderTagRequest finder){
        // 1 Пропускаем через парсер строку запроса и разбираем на массив слов:
        ArrayList<String> words = SearchParser.parse(finder.getText());
        // 2 Подготавливаем список для ответа (в поле динамического поиска):
        ArrayList<FinderTagResponse> finderResponseList = new ArrayList<>();


        // 3 Делаем запрос и получаем список пользователей через методы NameTreeManager'а:
        ArrayList<User> users = new ArrayList<>();
        if (finder.getOperation().equals("and")){
            users = new NameTreeManager().findAllUsersWithNames(words, "and");
        }
        else{
            users = new NameTreeManager().findAllUsersWithNames(words, "or");
        }

        // 4 Обходим всех пользователей и формируем список ответов:
        for (User user : users){
            Integer user_id = user.getId();
            String user_name = user.getSurname() + " " + user.getName() + " " + user.getMiddleName();
            finderResponseList.add(new FinderTagResponse(user_id, user_name));
        }

        return finderResponseList;
    }





}

