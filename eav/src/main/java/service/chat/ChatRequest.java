package service.chat;

/**
 * Created by Hroniko on 11.05.2017.
 */
// Класс, соотвествующий AJAX-запросу на получение сообщений чата (как всех, так и отдельных, смотря какому контроллеру посылается)
// Ну и еще для пересылки новых сообщений (часть полей не используется)
public class ChatRequest {

    private Integer meeting_id; // для данного идентификатора встречи

    private Integer message_id; // начиная с этого айди прислать все последующие сообщения

    private String text; // Для сообщения

    public ChatRequest() {
    }

    public Integer getMeeting_id() {
        return meeting_id;
    }

    public void setMeeting_id(Integer meeting_id) {
        this.meeting_id = meeting_id;
    }

    public Integer getMessage_id() {
        return message_id;
    }

    public void setMessage_id(Integer message_id) {
        this.message_id = message_id;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
