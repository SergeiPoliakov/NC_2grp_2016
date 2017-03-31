package service.statistics;

/**
 * Created by Hroniko on 30.03.2017.
 */
// Класс, соотвествующий AJAX-запросу на поиск тега
public class StatRequest {

    private String type;
    private String operation;
    private String text;

    public StatRequest() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOperation() {
        return operation;
    }

    public void setOperation(String operation) {
        this.operation = operation;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}