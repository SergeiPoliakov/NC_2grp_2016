package entities;

/**
 * Created by Hroniko on 31.01.2017.
 */
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TreeMap;

public class Event {

    public static final int objTypeID = 1002;

    private int id;
    private int host_id;
    private String name;
    private String date_begin;
    private String date_end;
    private String priority;
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

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getDate_begin() throws ParseException {

        return date_begin;
    }
    public void setDate_begin(String data_begin) { this.date_begin = data_begin; }

    public String getDate_end() { return date_end; }
    public void setDate_end(String data_end) { this.date_end = data_end; }



    public Event() {}

    public Event(String name, String date_begin, String date_end, String priority, String info) {
        this.name = name;
        this.date_begin = date_begin;
        this.date_end = date_end;
        this.priority = priority;
        this.info = info;

    }

    public TreeMap<Integer, Object> getArrayWithAttributes(){
        TreeMap<Integer, Object> map = new TreeMap<>();
        map.put(101, date_begin);
        map.put(102, date_end);
        map.put(103, null); // Продолжительность события. Пока что так, потом исправить, вставить расчет
        map.put(104, info);
        map.put(105, priority);
        return map;
    }

}
