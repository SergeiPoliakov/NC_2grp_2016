package service;

import com.google.common.cache.LoadingCache;
import dbHelp.DBHelp;
import entities.DataObject;
import entities.User;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import service.application_settings.SettingsLoader;
import service.cache.DataObjectCache;
import service.converter.Converter;
import web.SMSCSender;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by Lawrence on 08.02.2017.
 */

public class UserServiceImp implements UserService {

    private static String host_name = "";

    private static String host_port = "";

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();

    public String getCurrentUsername() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return auth.getName();
    }



    public int getObjID(String username) throws SQLException {
        return new DBHelp().getObjID(username);
    }




    // Получение объекта текущего авторизованного пользователя
    public User getCurrentUser() throws SQLException {
        return new DBHelp().getCurrentUser();
    }






    // Получение всех друзей текущего пользователя (2017-02-07)
    public ArrayList<User> getFriendListCurrentUser() throws SQLException {
        return new DBHelp().getFriendListCurrentUser();
    }



    // Получение всех активных email
    public ArrayList<Object> getEmail(String email) throws SQLException {
        return new DBHelp().getEmail(email);
    }

    // Получение случайного токета для завершения регистрации
    public String generateEmailToken(int length)
    {
        String characters = "qwertyuiopasdfghjklzxcvbnm1234567890QWERTYUIOASDFGHJKLZXCVBNM";
        Random rnd = new Random();
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
        {
            text[i] = characters.charAt(rnd.nextInt(characters.length()));
        }
        return new String(text);
    }


    public String generatePhoneToken()
    {
        String characters = "123456789";
        Random rnd = new Random();
        char[] text = new char[4];
        for (int i = 0; i < 4; i++)
        {
            text[i] = characters.charAt(rnd.nextInt(characters.length()));
        }
        return new String(text);
    }


    // Добавление юзера в список друзей по его ID (2017-02-03) (испр. 2017-02-07)
    public void setFriend(int idFriend) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        new DBHelp().setFriend(idFriend);
    }

    // Удаление юзера из списка друзей по его ID (2017-02-07)
    public void deleteFriend(int idFriend) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        new DBHelp().deleteFriend(idFriend);
    }


    public int generationID(int objTypeID) throws SQLException {
        return new DBHelp().generationID(objTypeID);
    }

    public void fittingEmail(String type, Integer fromID, Integer toID) throws MailException, IOException,
            MessagingException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException {
        //TODO: Здесь будем отправлять оповещения пользователю.
        // Подгружаем настройки
        host_name = SettingsLoader.getSetting("host_name");
        String port = SettingsLoader.getSetting("host_port");
        if (!port.equals("80")) host_port = ":" + port;
        //
        try (GenericXmlApplicationContext context = new GenericXmlApplicationContext()) {
            context.load("classpath:applicationContext.xml");
            context.refresh();
            JavaMailSender mailSender = context.getBean("mailSender", JavaMailSender.class);


            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            DataObject toUser = doCache.get(toID);
            DataObject fromUser = doCache.get(fromID);
            User userTo = new Converter().ToUser(toUser);
            User userFrom = new Converter().ToUser(fromUser);
            String nickname = userTo.getLogin();

            message.setSubject(nickname, "UTF-8");
            //TODO: Сюда напишите e-mail получателя.
            helper.setTo(userTo.getEmail());
            helper.setFrom(new InternetAddress("netcracker.thesecondgroup@gmail.com", "NC", "UTF-8"));



            if ("newMessage".equals(type)) {
                String url = "http://" + host_name + host_port + "/sendMessage/" + userFrom.getId();
                helper.setText("У вас новое сообщение от " + userFrom.getLogin() + ". " +
                        "Перейдите по ссылке, чтобы прочитать его. " +
                        "<html><body><a href=" + url + ">" + "Войти в чат " + "</a></body></html>" +
                        "Это сообщение создано автоматически, на него не нужно отвечать!", true);
            } else if ("addFriend".equals(type)) {
                String url = "http://" + host_name + host_port + "/main-login";
                helper.setText("Пользователь " + userFrom.getLogin() + " хочет стать вашим другом. " +
                        "<html><body><a href=" + url + ">" + "Подробнее" + "</a></body></html>", true);
            } else if ("meetingInvite".equals(type)) {
                String url = "http://" + host_name + host_port + "/main-login";
                helper.setText("Пользователь " + userFrom.getLogin() + " приглашает вас на встречу. " +
                        "<html><body><a href=" + url + ">" + "Подробнее" + "</a></body></html>", true);
            }

            sendEmail(message);

        }
    }

    @Override
    public void sendEmail(MimeMessage message) {
        JavaMailSender mailSender = getContextEmail();
        try {
            mailSender.send(message);
            System.out.println("Mail sended");
        } catch (MailException mailException) {
            System.out.println("Mail send failed.");
            mailException.printStackTrace();
        }
    }

    private JavaMailSender getContextEmail() {
        JavaMailSender mailSender;
        try (GenericXmlApplicationContext context = new GenericXmlApplicationContext()) {
            context.load("classpath:applicationContext.xml");
            context.refresh();
            mailSender = context.getBean("mailSender", JavaMailSender.class);
        }
        return mailSender;
    }

    public void sendSmS(String type, Integer fromID, Integer toID) throws ExecutionException {
        DataObject dataObject = doCache.get(fromID);
        if (dataObject.getValue(17).equals("true")) {  // тут нужно будет проверять расширенные настройки потом
            SMSCSender sd = new SMSCSender("Netcracker", "q7Sq2O_VqLhh", "utf-8", true);   //после теста закомментируйте обратно!!!!!
            User userTo = new Converter().ToUser(doCache.get(toID));
            User userFrom = new Converter().ToUser(doCache.get(fromID));
            if ("newMessage".equals(type)) {
                sd.sendSms(userTo.getPhone(), "У вас новое сообщение от " + userFrom.getLogin() + ".", 0, "", "", 0, "NC", "");
            } else if ("addFriend".equals(type)) {
                sd.sendSms(userTo.getPhone(), "Пользователь " + userFrom.getLogin() + " хочет стать вашим другом.", 0, "", "", 0, "NC", "");
            } else if ("meetingInvite".equals(type)) {
                sd.sendSms(userTo.getPhone(), "Пользователь " + userFrom.getLogin() + " приглашает вас на встречу.", 0, "", "", 0, "NC", "");
            }
            sd.getBalance();
        }
    }
}
