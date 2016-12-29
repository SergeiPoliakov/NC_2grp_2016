package domain;

import org.hibernate.annotations.GenericGenerator;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by Lawrence on 19.12.2016.
 */

@Entity
public class Event {
    private Integer id;

    private int host_id;

    private String name;

    private Date date_begin;

    private Date date_end;

    private Integer priority;

    private String info;

    @Id
    @GeneratedValue(generator = "increment")
    @GenericGenerator(name = "increment", strategy = "increment")
    public Integer getId() {
        return id;
    }
    public void setId(Integer id) {
        this.id = id;
    }

    public int getHost_id() {
        return host_id;
    }

    public void setHost_id(int host_id) {
        this.host_id = host_id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    @Temporal(TemporalType.DATE)
    public Date getDate_begin() { return date_begin; }
    public void setDate_begin(Date data_begin) { this.date_begin = data_begin; }

    @Temporal(TemporalType.DATE)
    public Date getDate_end() { return date_end; }
    public void setDate_end(Date data_end) { this.date_end = data_end; }



    public Event() {

    }

    public Event(int host_id, String name, Date date_begin, Date date_end, int priority, String info) {
        this.host_id = host_id;
        this.name = name;
        this.date_begin = date_begin;
        this.date_end = date_end;
        this.priority = priority;
        this.info = info;

    }



}
