package WebSocket;

/**
 * Created by Костя on 09.04.2017.
 */
public class SocketMessage {

    private String type;
    private String senderID;
    private String recieverID;
    private String additionalID;

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
}
