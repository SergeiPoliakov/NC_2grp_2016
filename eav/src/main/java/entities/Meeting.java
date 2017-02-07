package entities;

import java.util.TreeMap;

/**
 * Created by Костя on 07.02.2017.
 */

/*INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('301', 'title');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('302', 'date_start');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('303', 'date_end');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('304', 'info');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('305', 'organizer');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('306', 'tag');
INSERT INTO ATTRIBUTES (ATTR_ID, ATTR_NAME) VALUES ('307', 'member');*/
public class Meeting {

    public static final int objTypeID = 1004;

    private  String id;
    private  String title; // 301
    private  String date_start; // 302
    private  String date_end; // 303
    private  String info; // 304
    private  String organizer; // 305
    private  String tag; // 306
    private  String members; // 307

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getOrganizer() {
        return organizer;
    }

    public void setOrganizer(String organizer) {
        this.organizer = organizer;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public Meeting(){}

    public Meeting(String id, String title, String date_start, String date_end, String info, String organizer, String tag, String members) {
        this.id = id;
        this.title = title;
        this.date_start = date_start;
        this.date_end = date_end;
        this.info = info;
        this.organizer = organizer;
        this.tag = tag;
        this.members = members;
    }

    public Meeting(String title, String date_start, String date_end, String info, String organizer, String tag, String members) {
        this.title = title;
        this.date_start = date_start;
        this.date_end = date_end;
        this.info = info;
        this.organizer = organizer;
        this.tag = tag;
        this.members = members;
    }

    public TreeMap<Integer, Object> getArrayWithAttributes(){
        TreeMap<Integer, Object> map = new TreeMap<>();
        map.put(301, title);
        map.put(302, date_start);
        map.put(303, date_end);
        map.put(304, info);
        map.put(305, organizer);
        map.put(306, tag);
        map.put(307, members);
        return map;
    }
}
