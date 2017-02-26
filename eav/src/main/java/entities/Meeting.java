package entities;

import dbHelp.DBHelp;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Костя on 07.02.2017.
 */

public class Meeting extends BaseEntitie {

    public static final int objTypeID = 1004;

    private  int  id;
    private  String title; // 301
    private  String date_start; // 302
    private  String date_end; // 303
    private  String info; // 304
    private  User organizer; // 305
    private  String tag; // 306
    private  String members; // 307
    private ArrayList<User> users;
    private  ArrayList<Event> events;

    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
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

    public Meeting(int id, String title, String date_start, String date_end, String info, User organizer, String tag, String members) {
        this.id = id;
        this.title = title;
        this.date_start = date_start;
        this.date_end = date_end;
        this.info = info;
        this.organizer = organizer;
        this.tag = tag;
        this.members = members;
    }

    public Meeting(String title, String date_start, String date_end, String info, User organizer, String tag, String members) {
        this.title = title;
        this.date_start = date_start;
        this.date_end = date_end;
        this.info = info;
        this.organizer = organizer;
        this.tag = tag;
        this.members = members;
    }

    public Meeting(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.users = new ArrayList<>();
        this.organizer = new User();
        this.events = new ArrayList<>();
        this.id = dataObject.getId();
        // Поле params
        for (Map.Entry<Integer, String> param : dataObject.getParams().entrySet() ) {
            switch (param.getKey()){
                case (301):
                    this.title = param.getValue();
                    break;
                case (302):
                    this.date_start = param.getValue();
                    break;
                case (303):
                    this.date_end = param.getValue();
                    break;
                case (304):
                    this.info = param.getValue();
                    break;
                case (305):
                    User user =  new User(new DBHelp().getObjectsByIdAlternative(Integer.parseInt(param.getValue())));
                    this.organizer = user;
                    break;
                case (306):
                    this.tag = param.getValue();
                    break;
            }
        }
        // Поле ссылок
        for (Map.Entry<Integer, ArrayList<Integer>> reference : dataObject.getRefParams().entrySet() ) {
            switch (reference.getKey()){
                case (307):
                    for (Integer refValue: reference.getValue()) {
                        this.users.add(new DBHelp().getUserAndEventByUserID(refValue)); // Получение списка пользователей через старый метод
                    }
                    break;
            }
        }
    }

    public DataObject toDataObject(){
        DataObject dataObject = new DataObject();
        dataObject.setId(this.id);
        dataObject.setName("эммм");
        dataObject.setObjectTypeId(1004);
        dataObject.setParams(301, this.title);
        dataObject.setParams(302, this.date_start);
        dataObject.setParams(303, this.date_end);
        dataObject.setParams(304, this.info);
        dataObject.setParams(305, this.organizer.getId().toString());
        dataObject.setParams(306, this.tag);

        for (User user: this.users) {
            dataObject.setRefParams(307, user.getId());
        }

        return dataObject;
    }

    public TreeMap<Integer, Object> getArrayWithAttributes(){
        TreeMap<Integer, Object> map = new TreeMap<>();
        map.put(301, title);
        map.put(302, date_start);
        map.put(303, date_end);
        map.put(304, info);
        map.put(305, organizer.getId());
        map.put(306, tag);
        map.put(307, members);
        return map;
    }
}
