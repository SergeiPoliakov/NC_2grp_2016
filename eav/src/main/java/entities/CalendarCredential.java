package entities;

import com.google.api.services.calendar.Calendar;

/**
 * Created by Hroniko on 06.03.2017.
 * Новая сущность для хранения идетификационных файлов календаря в базе данных
 * Пока еще не использовал
 */
public class CalendarCredential extends BaseEntitie {
    private int id;
    private String name; // 1 в Params
    private Calendar body; // сами бинарные данные файла, пот сути, тут JSON

    public CalendarCredential() {
    }

    public CalendarCredential(String name, Calendar body) {
        this.name = name;
        //this.extention = extention;
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Calendar getBody() {
        return body;
    }

    public void setBody(Calendar body) {
        this.body = body;
    }
}
