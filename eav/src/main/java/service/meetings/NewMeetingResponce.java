package service.meetings;

/**
 * Created by Hroniko on 09.04.2017.
 */
// Класс для ответа на запрос создания новой встречи, отправляем статус готовности (добавлена встреча или нет) и айди новой встречи (AJAX)
public class NewMeetingResponce {

    private Integer id; // служебное поле, айди созданной встречи (может понадобиться для перехода на страницу редактирования данной встречи после ее создания)

    private String status; // Строковый ключ (все ради универсальности)

    public NewMeetingResponce() {
        this.id = null;
        this.status = "Error";
    }

    public NewMeetingResponce(Integer id) {
        this.id = id;
        if (id != null) this.status = "OK";
    }

    public NewMeetingResponce(Integer id, String status) {
        this.id = id;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
