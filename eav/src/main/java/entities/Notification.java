package entities;

import dbHelp.DBHelp;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Костя on 12.03.2017.
 */
// 1007
public class Notification extends BaseEntitie{

    // 2017-11-04 Константы класса - типы уведомлений:
    public static final String FRIEND_REQUEST = "friendRequest";
    public static final String MEETING_INVITE = "meetingInvite";
    public static final String MEETING_ACCEPT = "meetingAccept"; // уведомление о принятии встречи
    public static final String MEETING_REFUSE = "meetingRefuse"; // уведомление об отказе от встречи
    public static final String MEETING_REQUEST = "meetingRequest";
    public static final String INFO_FRIEND_ACCEPT = "infoFriendAccept";

    public static final int objTypeID = 1007;

    private  int  id; // не 501, это не параметр, это ключ из таблицы обджектов
    private String name; // 1 // имя для таблицы обджектов

    private int senderID; // 502
    private int recieverID; // 503
    private Integer additionalID; // 504
    private String type; // 505
    private String date; // 506
    private String isSeen = "0"; // 507 // 0 - Непросмотренные, 1 - Просмотренные

    private User sender;
    private User reciever;
    private Meeting meeting; // additional id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderID() {
        return senderID;
    }

    public void setSenderID(int senderID) {
        this.senderID = senderID;
    }

    public int getRecieverID() {
        return recieverID;
    }

    public void setRecieverID(int recieverID) {
        this.recieverID = recieverID;
    }

    public int getAdditionalID() {
        return additionalID;
    }

    public void setAdditionalID(int additionalID) {
        this.additionalID = additionalID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public static int getObjTypeID() {
        return objTypeID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIsSeen() {
        return isSeen;
    }

    public void setIsSeen(String isSeen) {
        this.isSeen = isSeen;
    }

    public User getReciever() {
        return reciever;
    }

    public void setReciever(User reciever) {
        this.reciever = reciever;
    }

    public Meeting getMeeting() {
        return meeting;
    }

    public void setMeeting(Meeting meeting) {
        this.meeting = meeting;
    }

    public Notification(){}

    public Notification(int id, String name, int senderID, int recieverID, int additionalID, String type, String date, User sender, User reciever, Meeting meeting) {
        this.id = id;
        this.name = name;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.additionalID = additionalID;
        this.type = type; // не тот тайп, который в таблице обджектов!
        this.date = date;
        this.sender = sender;
        this.reciever = reciever;
        this.meeting = meeting;
    }

    public Notification(int id, String name,int senderID, int recieverID, int additionalID, String type, String date) {
        this.id = id;
        this.name = name;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.additionalID = additionalID;
        this.type = type;
        this.date = date;
    }

    public Notification(String name,int senderID, int recieverID, String type, String date) {
        this.name = name;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.type = type;
        this.date = date;
    }

    public Notification(String name,int senderID, int recieverID, String type) {
        this.name = name;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.type = type;
        this.date = "11.11.1111 11:11";
    }

    public Notification(String name,int senderID, int recieverID, int additionalID, String type, String date) {
        this.name = name;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.additionalID = additionalID;
        this.type = type;
        this.date = date;
    }

    public Notification(String name,int senderID, int recieverID, int additionalID, String type) {
        this.name = name;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.additionalID = additionalID;
        this.type = type;
        this.date = "11.11.1111 11:11";
    }

    public Notification(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.id = dataObject.getId();
        this.name = dataObject.getName();
        this.sender = new User();
        this.reciever =  new User();
        this.meeting = new Meeting();

        for (Map.Entry<Integer, String> param : dataObject.getParams().entrySet() ) {
            switch (param.getKey()){
                case (505):
                    this.type = param.getValue();
                    break;
                case (506):
                    this.date = param.getValue();
                    break;
                case (507):
                    this.isSeen = param.getValue();
                    break;
            }
        }
        // Поле ссылок
        for (Map.Entry<Integer, ArrayList<Integer>> reference : dataObject.getRefParams().entrySet() ) {
            switch (reference.getKey()){
                // Sender
                case (502):
                    for (Integer refValue : reference.getValue()) {
                        this.sender = new User(new DBHelp().getObjectsByIdAlternative(refValue));
                        this.senderID = refValue;
                    }
                    break;
                // Reciever
                case (503):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.reciever = new User(new DBHelp().getObjectsByIdAlternative(refValue));
                            this.recieverID = refValue;
                        }
                    }
                    break;
                // Additional
                case (504):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.meeting = new Meeting(new DBHelp().getObjectsByIdAlternative(refValue));
                            this.additionalID = refValue;
                        }
                    }
                    break;
            }
        }
    }

    public DataObject toDataObject(){
        DataObject dataObject = new DataObject();
        dataObject.setId(this.id);
        dataObject.setName(this.name);
        dataObject.setObjectTypeId(objTypeID);
        dataObject.setRefParams(502, this.senderID);
        dataObject.setRefParams(503, this.recieverID);
        dataObject.setRefParams(504, this.additionalID);
        dataObject.setParams(505, this.type);
        dataObject.setParams(506, this.date);
        dataObject.setParams(507, this.isSeen);
        return dataObject;
    }
}
