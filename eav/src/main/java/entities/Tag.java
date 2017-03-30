package entities;

import java.util.ArrayList;

/**
 * Created by Hroniko on 22.03.2017.
 */
// Класс для хранения одного тега-слова
public class Tag extends BaseEntitie {

    private int id;
    private String text;

    public Tag() {
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
