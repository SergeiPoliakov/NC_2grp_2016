package service.notifications;

import WebSocket.SocketMessage;
import dbHelp.DBHelp;
import entities.DataObject;
import entities.Notification;
import service.LoadingServiceImp;
import service.converter.Converter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 * Created by Костя on 08.04.2017.
 */
public class NotificationService {

    private static LoadingServiceImp loadingService = new LoadingServiceImp();

    // Отправка уведомления в память, либо в БД, если получатель неактивен.
    // Активен - если запись о нём есть в UsersNotifications
    /*public static void sendNotification(Notification notification) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ParseException {

        notification.setSender( new Converter().ToUser(loadingService.getDataObjectByIdAlternative(notification.getSenderID())));

        UsersNotifications usersNotifications = UsersNotifications.getInstance();
        ArrayList<Notification> userNotifications =  usersNotifications.getNotifications(notification.getRecieverID());

        if (userNotifications != null)
            userNotifications.add(notification);
        else{
            DataObject dataObject = new Converter().toDO(notification);
            new DBHelp().setDataObjectToDB(dataObject);
        }
    }*/

    public static void sendNotification(SocketMessage notification) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ParseException {

        UsersNotifications usersNotifications = UsersNotifications.getInstance();
        ArrayList<SocketMessage> userNotifications =  usersNotifications.getNotifications(Integer.parseInt(notification.getRecieverID()));

        if (userNotifications != null)
            userNotifications.add(notification);
        else{
            // DataObject dataObject = new Converter().toDO(notification);
            // new DBHelp().setDataObjectToDB(dataObject);
        }
    }
}
