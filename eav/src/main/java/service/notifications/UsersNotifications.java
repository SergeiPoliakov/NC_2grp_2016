package service.notifications;

import WebSocket.SocketMessage;
import entities.Notification;
import entities.User;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Костя on 07.04.2017.
 */
public class UsersNotifications {
    private HashMap<Integer, ArrayList<SocketMessage>> usersNotifications;
    private static  UsersNotifications instance = new UsersNotifications();

    private UsersNotifications(){
        this.usersNotifications = new HashMap<>();
    }

    public static UsersNotifications getInstance(){
        if (instance == null)
            instance = new UsersNotifications();
        return instance;
    }

    public void setNotifications(Integer userID, ArrayList<SocketMessage> notifications){
        this.usersNotifications.put(userID, notifications);
    }

    public void setNotification(Integer userID, SocketMessage notification){
        usersNotifications.get(userID).add(notification);
    }

    public ArrayList<SocketMessage>  getNotifications(Integer userID){
        return usersNotifications.get(userID);
    }
}
