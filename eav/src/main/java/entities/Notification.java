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

    private  int  id; // 501
    private  String senderID; // 502
    private  String recieverID; // 503
    private String additionalID; // 504
    private String type; // 505
    private String date; // 506
    private String isSeen; // 507 // Просмотренные

    private User sender;
    private User reciever;
    private Meeting meeting; // additional id

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getRecieverID() {
        return recieverID;
    }

    public void setRecieverID(String recieverID) {
        this.recieverID = recieverID;
    }

    public String getAdditionalID() {
        return additionalID;
    }

    public void setAdditionalID(String additionalID) {
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

    public Notification(int id, String senderID, String recieverID, String additionalID, String type, String date, User sender, User reciever, Meeting meeting) {
        this.id = id;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.additionalID = additionalID;
        this.type = type;
        this.date = date;
        this.sender = sender;
        this.reciever = reciever;
        this.meeting = meeting;
    }

    public Notification( String senderID, String recieverID, String additionalID, String type, String date) {
        this.id = 50000;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.additionalID = additionalID;
        this.type = type;
        this.date = date;
        this.isSeen = "false";
    }

    public Notification(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.sender = new User();
        this.reciever =  new User();
        this.meeting = new Meeting();
        this.id = dataObject.getId();
        for (Map.Entry<Integer, String> param : dataObject.getParams().entrySet() ) {
            switch (param.getKey()){
                case (502):
                    this.senderID = param.getValue();
                    break;
                case (503):
                    this.recieverID = param.getValue();
                    break;
                case (504):
                    this.additionalID = param.getValue();
                    break;
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
                        this.senderID = refValue.toString();
                    }
                    break;
                // Reciever
                case (503):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.reciever = new User(new DBHelp().getObjectsByIdAlternative(refValue));
                            this.recieverID = refValue.toString();
                        }
                    }
                    break;
                // Additional
                case (504):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.meeting = new Meeting(new DBHelp().getObjectsByIdAlternative(refValue));
                            this.additionalID = refValue.toString();
                        }
                    }
                    break;
            }
        }
    }

    public DataObject toDataObject(){
        DataObject dataObject = new DataObject();
        dataObject.setId(this.id);
        dataObject.setName(this.senderID);
        dataObject.setObjectTypeId(1007);
        dataObject.setParams(505, this.type);
        dataObject.setParams(506, this.date);
        dataObject.setParams(507, this.isSeen);

        dataObject.setRefParams(502, Integer.parseInt(this.senderID));
        dataObject.setRefParams(503, Integer.parseInt(this.recieverID));
        dataObject.setRefParams(504, Integer.parseInt(this.additionalID));

        return dataObject;
    }
}
