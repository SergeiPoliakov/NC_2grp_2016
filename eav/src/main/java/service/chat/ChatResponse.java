package service.chat;

/**
 * Created by Hroniko on 11.05.2017.
 */
// Класс для хранения одного сообщения, помещаемой в массив-ответ на запрос ChatRequest (AJAX)
public class ChatResponse {

    private Integer id; // айдишник сообщения, нужен будет для того, чтобы знать, с какого последнего сообщения подгружать новые сообщения из сейвера

    private String date_send; // Дата отправки в текстовом формате

    private String text; // Сам текст сообщения

    private Integer from_id; // Айдишник отправителя

    private String from_name; // Имя отправителя

    private String avatar; // Ссылка на аватар отправителя

    public ChatResponse() {
    }

    public ChatResponse(Integer id, String date_send, String text, Integer from_id, String from_name, String avatar) {
        this.id = id;
        this.date_send = date_send;
        this.text = text;
        this.from_id = from_id;
        this.from_name = from_name;
        this.avatar = avatar;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate_send() {
        return date_send;
    }

    public void setDate_send(String date_send) {
        this.date_send = date_send;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getFrom_id() {
        return from_id;
    }

    public void setFrom_id(Integer from_id) {
        this.from_id = from_id;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
