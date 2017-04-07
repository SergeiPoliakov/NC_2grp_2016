package service.notifications;

import entities.Notification;

import java.util.ArrayList;

/**
 * Created by Костя on 08.04.2017.
 */
public class NotificationThread implements Runnable {

    private ArrayList<Notification> notifications;
    private Integer oldSize;

    public NotificationThread(ArrayList<Notification> notifications, Integer oldSize){
        this.notifications = notifications;
        this.oldSize = oldSize;
    }

    @Override
    public void run() {
        while ( this.oldSize == notifications.size()){
            Thread.yield();
        }
    }
}
