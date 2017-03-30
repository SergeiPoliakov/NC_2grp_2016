package service.search;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by Hroniko on 29.03.2017.
 */
// Класс для разбора запросов от поисковой странички
public class SearchParser {

    public SearchParser() {
    }

    public static ArrayList<String> parse(String search_string){
        //String[] tag_strings = search_string.split("[\\p{P} \\t\\n\\r]");

        // Разделяем на отдельные слова:
        // String[] tag_strings = search_string.split("\\p{P}?[ \\t\\n\\r]+");
        String[] tag_strings = search_string.split("[\\p{P} \\t\\n\\r]+");
        System.out.println(Arrays.toString(tag_strings));
        System.out.println();
        // Выкидываем слова короче 3 букв (предлоги и пр.), переводим в нижний регистр и вставляем в лист:
        ArrayList<String> tags = new ArrayList<>();
        for (String st : tag_strings){
            if (st.length() > 2){
                tags.add(st.toLowerCase());
            }
        }
        System.out.println(tags);
        System.out.println();
        return tags.size() > 0 ? tags : null;
    }

}
