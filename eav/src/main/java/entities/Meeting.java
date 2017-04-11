package entities;

import dbHelp.DBHelp;
import service.LoadingServiceImp;
import service.converter.Converter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Костя on 07.02.2017.
 */

public class Meeting extends BaseEntitie {

    public static final int objTypeID = 1004;

    private int  id;
    private String title; // 301
    private String date_start; // 302
    private String date_end; // 303
    private String info; // 304
    private User organizer; // 305
    private StringBuilder tag; // 306
    private String members; // 307
    private ArrayList<User> users;
    private ArrayList<Event> events;
    private ArrayList<Event> duplicates; // 313 // Копии задач-отображений встречи на расписание подписанных пользователей (участников встречи)
    private String status; // 309
    private String duration; //310
    // 311 - ссылка на удаленного (-ых) из встречи юзеров, в базе есть
    private String date_edit; // 312

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

    public StringBuilder getTag() {
        return tag;
    }

    public void setTag(StringBuilder tag) {
        this.tag = tag;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static int getObjTypeID() {
        return objTypeID;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate_edit() {
        return date_edit;
    }

    public void setDate_edit(String date_edit) {
        this.date_edit = date_edit;
    }

    public ArrayList<Event> getDuplicates() {
        return duplicates;
    }

    public void setDuplicates(ArrayList<Event> duplicates) {
        this.duplicates = duplicates;
    }

    public void setDuplicates(Event duplicate) {
        this.duplicates.add(duplicate);
    }

    // 2017-04-11 Метод создания дубликата встречи (события) как отображения в пользовательское расписание:
    public void createDuplicate(Integer user_id) throws SQLException, NoSuchMethodException, IllegalAccessException, ParseException, InvocationTargetException {
        Event duplicate = new Event();
        duplicate.setHost_id(user_id);
        duplicate.setName(this.title);
        duplicate.setDate_begin(this.date_start);
        duplicate.setDate_end(this.date_end);
        duplicate.setPriority("Style4"); // 4-ый приоритет, соотвествующий дубликату встречи, надо сделать для него другой цвет (и полупрозрачность) на страничке
        duplicate.setInfo(this.info);
        duplicate.setType_event(Event.DUPLICATE_EVENT);
        // И проверяем, что у нас за встреча:
        if (this.duration == null){
            // имеем дело со встречей с фиксированными границами
            duplicate.setEditable(Event.UNEDITABLE);
        }
        else{
            // иначе имеем дело со встречей с плавающими границами
            duplicate.setDuration(this.duration);
            duplicate.setType_event(Event.DUPLICATE_EVENT);
            duplicate.setEditable(Event.EDITABLE);
            duplicate.setFloating_date_begin(this.date_start);
            duplicate.setFloating_date_end(this.date_end);
        }

        // Привешиваем дубликат к нашей встрече
        this.setDuplicates(duplicate);
        // и сохраняем в базу:
        DataObject dataObject = new Converter().toDO(duplicate);
        new LoadingServiceImp().setDataObjectToDB(dataObject);
    }


    public Meeting(){}

    public Meeting(int id, String title, String date_start, String date_end, String info, User organizer, StringBuilder tag, String members, String duration) {
        this.id = id;
        this.title = title;
        this.date_start = date_start;
        this.date_end = date_end;
        this.info = info;
        this.organizer = organizer;
        this.tag = tag;
        this.members = members;
        this.status = "active";
        this.duration = duration;
    }

    public Meeting(String title, String date_start, String date_end, String info, User organizer, StringBuilder tag, String members, String duration) {
        this.title = title;
        this.date_start = date_start;
        this.date_end = date_end;
        this.info = info;
        this.organizer = organizer;
        this.tag = tag;
        this.members = members;
        this.status = "active";
        this.duration = duration;
    }

    // 2017-04-09 21-52 Самое то для новых встреч
    public Meeting(String title, String date_start, String date_end, String info, User organizer, StringBuilder tag, String members, String duration, String date_edit) {
        this.title = title;
        this.date_start = date_start;
        this.date_end = date_end;
        this.info = info;
        this.organizer = organizer;
        this.tag = tag;
        this.members = members;
        this.status = "active";
        this.duration = duration;
        this.date_edit = date_edit;
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
                    this.organizer = new User(new DBHelp().getObjectsByIdAlternative(Integer.parseInt(param.getValue())));
                    break;
                case (306):
                    this.tag = new StringBuilder(param.getValue());
                    break;
                case (309):
                    this.status = param.getValue();
                    break;
                case (310):
                    this.duration = param.getValue();
                    break;
                case (312):
                    this.date_edit = param.getValue();
                    break;
            }
        }
        // Поле ссылок
        for (Map.Entry<Integer, ArrayList<Integer>> reference : dataObject.getRefParams().entrySet() ) {
            switch (reference.getKey()){
                // Users
                case (307):
                    for (Integer refValue: reference.getValue()) {

                        this.users.add(new User(new DBHelp().getObjectsByIdAlternative(refValue)));
                    }
                    break;
                // Events
                case (308):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.events.add(new Event(new DBHelp().getObjectsByIdAlternative(refValue)));
                        }
                    }
                    break;
            }
        }
    }

    public DataObject toDataObject(){
        DataObject dataObject = new DataObject();
        dataObject.setId(this.id);
        dataObject.setName(this.title);
        dataObject.setObjectTypeId(1004);
        dataObject.setParams(301, this.title);
        dataObject.setParams(302, this.date_start);
        dataObject.setParams(303, this.date_end);
        dataObject.setParams(304, this.info);
        dataObject.setParams(305, this.organizer.getId().toString());
        dataObject.setParams(306, new String(this.tag));
        dataObject.setParams(309, this.status);
        dataObject.setParams(310, this.duration);
        dataObject.setParams(312, this.date_edit);

        for (User user: this.users) {
            dataObject.setRefParams(307, user.getId());
        }

        if (this.events != null) {
            for (Event event : this.events) {
                dataObject.setRefParams(307, event.getId());
            }
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
        map.put(309, status);
        map.put(310, duration);
        map.put(312, date_edit);
        return map;
    }

}
