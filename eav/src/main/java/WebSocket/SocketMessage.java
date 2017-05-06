package WebSocket;

/**
 * Created by Костя on 09.04.2017.
 */
public class SocketMessage {

    private String type;
    private String senderID;
    private String recieverID;
    private String additionalID;
    private String date;
    // Тут пошла жесть
    private String senderName; // Имя отправителя
    private String senderPic; // Ссылка на аватар отправителя
    private String meetingName; // Название встречи

    public SocketMessage() {
    }

    public SocketMessage(String type) {
        this.type = type;
        this.senderID = "";
        this.recieverID = "";
        this.additionalID = "";
    }

    public SocketMessage(String type, String senderID, String recieverID) {
        this.type = type;
        this.senderID = senderID;
        this.recieverID = recieverID;
    }

    public SocketMessage(String type, String senderID, String recieverID, String additionalID) {
        this.type = type;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.additionalID = additionalID;
    }

    public SocketMessage(String type, String senderID, String recieverID, String senderName, String senderPic) {
        this.type = type;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.senderName = senderName;
        this.senderPic = senderPic;
    }

    public SocketMessage(String type, String senderID, String recieverID, String additionalID, String senderName, String senderPic, String meetingName) {
        this.type = type;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.additionalID = additionalID;
        this.senderName = senderName;
        this.senderPic = senderPic;
        this.meetingName = meetingName;
    }

    public SocketMessage(String type, String senderID, String recieverID, String additionalID, String date, String senderName, String senderPic, String meetingName) {
        this.type = type;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.additionalID = additionalID;
        this.date = date;
        this.senderName = senderName;
        this.senderPic = senderPic;
        this.meetingName = meetingName;
    }

    public SocketMessage(String type, String senderID, String recieverID, String date, String senderName, String senderPic) {
        this.type = type;
        this.senderID = senderID;
        this.recieverID = recieverID;
        this.date = date;
        this.senderName = senderName;
        this.senderPic = senderPic;
    }


    public String getType() {
        return type;
    }

    public String getSenderID() {
        return senderID;
    }

    public String getRecieverID() {
        return recieverID;
    }

    public String getAdditionalID() {
        return additionalID;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPic() {
        return senderPic;
    }

    public void setSenderPic(String senderPic) {
        this.senderPic = senderPic;
    }

    public String getMeetingName() {
        return meetingName;
    }

    public void setMeetingName(String meetingName) {
        this.meetingName = meetingName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
