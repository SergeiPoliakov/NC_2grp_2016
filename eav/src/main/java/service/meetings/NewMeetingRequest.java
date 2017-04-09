package service.meetings;

/**
 * Created by Hroniko on 09.04.2017.
 */
// Класс, соотвествующий AJAX-запросу на создание новой встречи
public class NewMeetingRequest {

    private String title; // название
    private String date_start; // дата начала
    private String date_end; // дата конца
    private String date_edit; // дата окончания возможности редактирования и блокировки встречи с плавающими границами
    private String info; // информация
    private String tag; // теги
    private String duration; // длительность встречи (если null, то имеем дело со встречей с фиксированными границами, иначе это встреча с плавающими границами)

    public NewMeetingRequest() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public String getDate_edit() {
        return date_edit;
    }

    public void setDate_edit(String date_edit) {
        this.date_edit = date_edit;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }
}
