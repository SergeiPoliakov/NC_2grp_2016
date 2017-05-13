package service.search;

/**
 * Created by Hroniko on 30.03.2017.
 */
// Класс для хранения одного тега-слова помещаемого в массив-ответ на запрос тегов (AJAX)
public class FinderTagResponse {

    private int id;
    private String text;

    public FinderTagResponse() {
    }

    public FinderTagResponse(int id, String text) {
        this.id = id;
        this.text = text;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
