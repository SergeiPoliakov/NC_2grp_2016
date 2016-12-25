package domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Hroniko (Anatoly Bedarev) on 25.12.2016.
 */

@Entity
public class Message{

    private Integer id; // id сообщения

    private int from_id; // id отправителя

    private int to_id; // id получателя

    private String name; // Загоовок сообщения

    private Date date_begin; // Дата доставки

    private String body; // Тело сообщения



    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public int getFrom_id() {
        return from_id;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public int getTo_id() {
        return to_id;
    }

    public void setTo_id(int to_id) {
        this.to_id = to_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public Message() {
    }

    @Temporal(TemporalType.DATE)
    public Date getDate_begin() { return date_begin; }
    public void setDate_begin(Date data_begin) { this.date_begin = data_begin; }




    public Message(String name, Date date_begin, String body, int from_id, int to_id) {
        this.name = name;
        this.date_begin = date_begin;
        this.body = body;
        this.from_id = from_id;
        this.to_id = to_id;
    }



}
