package entities;

/**
 * Created by Hroniko on 03.02.2017.
 */

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
public class Message extends BaseEntitie implements Comparable<Message> {

    private Integer id; // 1
    private Integer from_id; // 201
    private Integer to_id; // 202
    private String date_send; // 203
    private Integer read_status; // 204
    private String text; // 205
    private String from_name; // 206
    private String to_name; // 207

    public Message() {
    }

    public Message(int id, int from_id, int to_id, String date_send, int read_status, String text, String from_name, String to_name) {
        this.id = id;
        this.from_id = from_id;
        this.to_id = to_id;
        this.date_send = date_send;
        this.read_status = read_status;
        this.text = text;
        this.from_name = from_name;
        this.to_name = to_name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getFrom_id() {
        return from_id;
    }

    public void setFrom_id(Integer from_id) {
        this.from_id = from_id;
    }

    public Integer getTo_id() {
        return to_id;
    }

    public void setTo_id(Integer to_id) {
        this.to_id = to_id;
    }

    public String getDate_send() {
        return date_send;
    }

    public void setDate_send(String date_send) {
        this.date_send = date_send;
    }

    public Integer getRead_status() {
        return read_status;
    }

    public void setRead_status(Integer read_status) {
        this.read_status = read_status;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getFrom_name() {
        return from_name;
    }

    public void setFrom_name(String from_name) {
        this.from_name = from_name;
    }

    public String getTo_name() {
        return to_name;
    }

    public void setTo_name(String to_name) {
        this.to_name = to_name;
    }

    // Сравнение по айди
    @Override
    public int compareTo(Message otherMessage) {
        return (this.getId() - otherMessage.getId());
    }
}
