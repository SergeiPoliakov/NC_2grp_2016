package entities;

/**
 * Created by Hroniko on 31.01.2017.
 */
import java.util.Date;

public class Event {

    private int id;
    private int host_id;
    private String name;
    private String date_begin;
    private String date_end;
    private int priority;
    private String info;


    public int getId() {
        return id;
    }
    public void setId(int id) {
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

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDate_begin() { return date_begin; }
    public void setDate_begin(String data_begin) { this.date_begin = data_begin; }

    public String getDate_end() { return date_end; }
    public void setDate_end(String data_end) { this.date_end = data_end; }



    public Event() {}

    public Event(int host_id, String name, String date_begin, String date_end, int priority, String info) {
        this.host_id = host_id;
        this.name = name;
        this.date_begin = date_begin;
        this.date_end = date_end;
        this.priority = priority;
        this.info = info;

    }

}
