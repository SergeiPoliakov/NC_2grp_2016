package service.optimizer;

/**
 * Created by Hroniko on 08.04.2017.
 */
// Класс, соотвествующий AJAX-запросу на получение свободных слотов
public class SlotRequest {

    private String user;

    private String start;

    private String end;

    public SlotRequest() {
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }
}
