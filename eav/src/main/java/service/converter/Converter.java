package service.converter;

import entities.*;
import service.LoadingServiceImp;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Hroniko on 25.02.2017.
 * Класс для конвертирования из сущностей в датаобджекты и обратно
 */
public class Converter {

    Integer USER = 1001;
    Integer EVENT = 1002;
    Integer MESSAGE = 1003;
    Integer MEETING = 1004;
    Integer SETTINGS = 1006;
    Integer NOTIFICATIONS = 1007;
    Integer LOG = 1008;
    Integer FILE = 1009;
    Integer TAG = 1010;  // c 700-ми атрибутами

    private LoadingServiceImp loadingService = new LoadingServiceImp();

    public Message ToMessage(DataObject dataObject) {
        Message message = new Message();
        try {
            message.setId(dataObject.getId());  // 1
            message.setFrom_id(Integer.parseInt(dataObject.getParameter(201))); // 201
            message.setTo_id(Integer.parseInt(dataObject.getParameter(202))); // 202
            message.setDate_send(dataObject.getParameter(203));  // 203
            message.setRead_status(Integer.parseInt(dataObject.getParameter(204))); // 204
            message.setText(dataObject.getParameter(205));  // 205
            message.setFrom_name(dataObject.getParameter(206));  // 206
            message.setTo_name(dataObject.getParameter(207));  // 207
        } catch (Exception e) {
            e.printStackTrace();
        }
        return message;
    }


    public User ToUser(DataObject dataObject) {
        User user = new User();
        try {
            user.setId(dataObject.getId());
            user.setName(dataObject.getParameter(1));
            user.setSurname(dataObject.getParameter(2));
            user.setMiddleName(dataObject.getParameter(3));
            user.setLogin(dataObject.getParameter(4));
            user.setAgeDate(dataObject.getParameter(5));
            user.setEmail(dataObject.getParameter(6));
            user.setPassword(dataObject.getParameter(7));
            user.setSex(dataObject.getParameter(8));
            user.setCity(dataObject.getParameter(9));
            user.setAdditional_field(dataObject.getParameter(10));
            user.setPicture(dataObject.getParameter(11));
            user.setPhone(dataObject.getParameter(16));
            user.setSettingsID(Integer.parseInt(dataObject.getParameter(19)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
    }

    // 2017-03-24
    public Notification ToNotification(DataObject dataObject) {
        Notification notification = new Notification();
        try {
            notification.setId(dataObject.getId());
            notification.setName(dataObject.getName());

            notification.setType(dataObject.getParameter(505));
            notification.setDate(dataObject.getParameter(506));
            notification.setIsSeen(dataObject.getParameter(507));

            notification.setSenderID(dataObject.getReference(502).get(0));
            notification.setRecieverID(dataObject.getReference(503).get(0));
            notification.setAdditionalID(dataObject.getReference(504).get(0));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return notification;
    }

    // 2017-03-13
    public Log ToLog(DataObject dataObject) {
        Log log = new Log();
        try {
            log.setId(dataObject.getId());
            log.setName(dataObject.getName());
            log.setDate(dataObject.getParameter(600));
            int i = 601;
            String params = null;
            for (; i < 700; i++) {
                params = dataObject.getParameter(i);
                if (params != null) { // ищем валидный параметр
                    log.setType(i);
                    log.setInfo(params);
                    break;
                }
            }
            i = 601;
            Integer referens = null;
            for (; i < 700; i++) { // Ищем валидную ссылку
                referens = dataObject.getReference(i).get(0);
                if (referens != null) {
                    log.setType(i);
                    log.setId(referens);
                    break;
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return log;
    }

    // 2017-03-24 Конвертер для массива датаобджектов в массив уведомлений
    public ArrayList<Notification> ToNotification(ArrayList<DataObject> aldo) {
        ArrayList<Notification> notifications = new ArrayList<>();
        for (DataObject DO : aldo) {
            notifications.add(ToNotification(DO));
        }
        return notifications;
    }

    // 2017-03-13 Конвертер для массива датаобджектов в массив логов
    public ArrayList<Log> ToLog(ArrayList<DataObject> aldo) {
        ArrayList<Log> logs = new ArrayList<>();
        for (DataObject DO : aldo) {
            logs.add(ToLog(DO));
        }
        return logs;
    }


    // 2017-03-05 Конвертер для массива датаобджектов в массив юзеров
    public ArrayList<Message> ToMessage(ArrayList<DataObject> aldo) {
        ArrayList<Message> messages = new ArrayList<>();
        for (DataObject DO : aldo) {
            messages.add(ToMessage(DO));
        }
        return messages;
    }

    // 2017-02-28 Конвертер для массива датаобджектов в массив юзеров
    public ArrayList<User> ToUser(ArrayList<DataObject> aldo) {
        ArrayList<User> users = new ArrayList<>();
        for (DataObject DO : aldo) {
            users.add(ToUser(DO));
        }
        return users;
    }

    // 2017-02-28 Конвертер для массива датаобджектов в массив событий
    public ArrayList<Event> ToEvent(ArrayList<DataObject> aldo) {
        ArrayList<Event> events = new ArrayList<>();
        for (DataObject DO : aldo) {
            events.add(ToEvent(DO));
        }
        return events;
    }

    // 2017-02-28
    public Event ToEvent(DataObject dataObject) {
        Event event = new Event();
        try {
            event.setId(dataObject.getId()); // 1
            event.setName(dataObject.getName()); // 3
            event.setHost_id(dataObject.getReference(141).get(0)); // 141 Ссылка на Юзера-Создателя
            event.setDate_begin(dataObject.getParameter(101));
            event.setDate_end(dataObject.getParameter(102));
            event.setDuration(dataObject.getParameter(103));
            event.setInfo(dataObject.getParameter(104));
            event.setPriority(dataObject.getParameter(105));

            event.setType_event(dataObject.getParameter(106));
            event.setEditable(dataObject.getParameter(107));
            event.setFloating_date_begin(dataObject.getParameter(108));
            event.setFloating_date_end(dataObject.getParameter(109));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
    }

    public Settings ToSettings(DataObject dataObject) {
        Settings settings = new Settings();
        try {
            settings.setId(dataObject.getId());
            settings.setUser_id(Integer.parseInt(dataObject.getParameter(401)));
            settings.setEmailNewMessage(dataObject.getParameter(402));
            settings.setEmailNewFriend(dataObject.getParameter(403));
            settings.setEmailMeetingInvite(dataObject.getParameter(404));
            settings.setPhoneNewMessage(dataObject.getParameter(405));
            settings.setPhoneNewFriend(dataObject.getParameter(406));
            settings.setPhoneMeetingInvite(dataObject.getParameter(407));
            settings.setPrivateProfile(dataObject.getParameter(408));
            settings.setPrivateMessage(dataObject.getParameter(409));
            settings.setPrivateAddFriend(dataObject.getParameter(410));
            settings.setPrivateLookFriend(dataObject.getParameter(411));
            settings.setPrivateMeetingInvite(dataObject.getParameter(412));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return settings;
    }

    // Конвертация в DataObbject'ы
    public DataObject toDO(BaseEntitie entitie) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        DataObject dataObject = new DataObject();

        if (entitie instanceof User) {
            // Работаем с Пользователями
            User user = (User) entitie;

            dataObject.setId(user.getId());
            dataObject.setObjectTypeId(USER);
            dataObject.setName(user.getName());

            dataObject.setParams(1, user.getName());
            dataObject.setParams(2, user.getSurname());
            dataObject.setParams(3, user.getMiddleName());
            dataObject.setParams(4, user.getLogin());
            dataObject.setParams(5, user.getAgeDate());
            dataObject.setParams(6, user.getEmail());
            dataObject.setParams(7, user.getPassword());
            dataObject.setParams(8, user.getSex());
            dataObject.setParams(9, user.getCity());
            dataObject.setParams(10, user.getAdditional_field());
            dataObject.setParams(11, user.getPicture());
            dataObject.setParams(16, user.getPhone());
            dataObject.setParams(19, String.valueOf(user.getSettingsID()));

            for (Map.Entry<Integer, ArrayList<Integer>> reference : dataObject.getRefParams().entrySet()) {
                switch (reference.getKey()) {
                    case (12): // friends
                        // Получаем массив друзей как массив датаобджектов:
                        ArrayList<DataObject> aldoFriends = loadingService.getListDataObjectByListIdAlternative(reference.getValue());
                        // Конвертируем в юзеры:
                        ArrayList<User> users = this.ToUser(aldoFriends);
                        // И добавляем в объект:
                        user.addToFriendList(users);
                        break;
                    case (13): // tasks
                        // собрал все в кучу
                        user.addToEventList(this.ToEvent(loadingService.getListDataObjectByListIdAlternative(reference.getValue())));
                        break;
                }
            }
        } else if (entitie instanceof Message) {
            // Работаем с Сообщениями
            Message message = (Message) entitie;

            dataObject.setId(message.getId());
            dataObject.setObjectTypeId(MESSAGE);
            dataObject.setName("Message_" + message.getId());

            dataObject.setParams(201, String.valueOf(message.getFrom_id()));
            dataObject.setParams(202, String.valueOf(message.getTo_id()));
            dataObject.setParams(203, message.getDate_send());
            dataObject.setParams(204, String.valueOf(message.getRead_status()));
            dataObject.setParams(205, message.getText());
            dataObject.setParams(206, message.getFrom_name());
            dataObject.setParams(207, message.getTo_name());

        } else if (entitie instanceof Event) {
            // 2017-02-28 Работаем с Событиями, 2017-04-11 Новые поля для отображения встречи на пользовательские события
            Event event = (Event) entitie;

            dataObject.setId(event.getId());
            dataObject.setObjectTypeId(EVENT);
            dataObject.setName(event.getName());
            //
            dataObject.setParams(101, event.getDate_begin());
            dataObject.setParams(102, event.getDate_end());
            dataObject.setParams(103, event.getDuration());
            dataObject.setParams(104, event.getInfo());
            dataObject.setParams(105, event.getPriority());

            if (event.getType_event() != null) dataObject.setParams(106, event.getType_event());
            if (event.getEditable() != null) dataObject.setParams(107, event.getEditable());
            if (event.getFloating_date_begin() != null) dataObject.setParams(108, event.getFloating_date_begin());
            if (event.getFloating_date_end() != null) dataObject.setParams(109, event.getFloating_date_end());
            //
            dataObject.setRefParams(141, event.getHost_id());
        } else if (entitie instanceof Meeting) {
            // Работаем со Встречами
            Meeting meeting = (Meeting) entitie;

            dataObject.setId(meeting.getId());
            dataObject.setObjectTypeId(MEETING);

            // И надо реализовать ...
        } else if (entitie instanceof DataObject) {
            // Работаем со Датаобджектом, его конвертировать не надо
            dataObject = (DataObject) entitie;
        } else if (entitie instanceof Settings) {
            Settings settings = (Settings) entitie;

            dataObject.setId(settings.getId());
            dataObject.setObjectTypeId(SETTINGS);
            dataObject.setName("Settings_User_" + settings.getUser_id());
            //
            dataObject.setParams(401, String.valueOf(settings.getUser_id()));
            dataObject.setParams(402, settings.getEmailNewMessage());
            dataObject.setParams(403, settings.getEmailNewFriend());
            dataObject.setParams(404, settings.getEmailMeetingInvite());
            dataObject.setParams(405, settings.getPhoneNewMessage());
            dataObject.setParams(406, settings.getPhoneNewFriend());
            dataObject.setParams(407, settings.getPhoneMeetingInvite());
            dataObject.setParams(408, settings.getPrivateProfile());
            dataObject.setParams(409, settings.getPrivateMessage());
            dataObject.setParams(410, settings.getPrivateAddFriend());
            dataObject.setParams(411, settings.getPrivateLookFriend());
            dataObject.setParams(412, settings.getPrivateMeetingInvite());
        } else if (entitie instanceof Notification) {
            // Работаем с Уведомлениями
            Notification notification = (Notification) entitie;
            //
            dataObject.setId(notification.getId());
            dataObject.setObjectTypeId(NOTIFICATIONS);
            dataObject.setName("Notif_" + notification.getId());
            //
            dataObject.setRefParams(502, notification.getSenderID());
            dataObject.setRefParams(503, notification.getRecieverID());
            ///////dataObject.setRefParams(504, notification.getAdditionalID());
            try{
                dataObject.setRefParams(504, notification.getAdditionalID());
            }
            catch (Exception e){
                System.out.println("Пустое поле с");
            }
            dataObject.setParams(505, notification.getType());
            dataObject.setParams(506, notification.getDate());
            dataObject.setParams(507, notification.getIsSeen());
        } else if (entitie instanceof Log) {
            // Работаем с логами
            Log log = (Log) entitie;

            // dataObject.setId(log.getId()); // это не надо, так как логи долго храниться могут в памяти, а айдишников еще не заняли
            dataObject.setObjectTypeId(LOG);
            dataObject.setName(log.getName());

            dataObject.setParams(600, log.getDate());

            // Замечание! В отличие от других сущностей, логи могут храниться либо как ссылки на объекты в базе,
            // либо как записи в параметрах, поэтому либо то, либо то (вместе не храним, зачем лишнее дублирование):
            if (log.getInfo() != null) {
                dataObject.setParams(log.getType(), log.getInfo());
            }
            if (log.getLinkId() != null) {
                dataObject.setRefParams(log.getType(), log.getLinkId());
            }
        } else if (entitie instanceof TagNode) {
            // Работаем с нодами тега
            TagNode node = (TagNode) entitie;
            dataObject.setId(node.getId());
            dataObject.setObjectTypeId(TAG);
            dataObject.setName(node.getName());
            dataObject.setParams(701, String.valueOf(node.getValue()));
            dataObject.setParams(702, String.valueOf(node.getUsage_count()));
            dataObject.setParams(707, String.valueOf(node.getMeetings_count()));

            if (node.getRoot() != null) dataObject.setRefParams(703, node.getRoot().getId());

            for (int i = 0; i < node.getParents().size(); i++) {
                TagNode parent = node.getParents(i);
                if (parent != null) dataObject.setRefParams(704, parent.getId());
            }
            for (int i = 0; i < node.getUsers().size(); i++) {
                Integer user_id = node.getUsers(i);
                if (user_id != null) dataObject.setRefParams(705, user_id);
            }
            for (int i = 0; i < node.getMeetings().size(); i++) {
                Integer meeting_id = node.getMeetings(i);
                if (meeting_id != null) dataObject.setRefParams(706, meeting_id);
            }


        } else { // иначе сами не понимаем, что конвертируем
            dataObject = null;
        }
        return dataObject;
    }

    // 2017-03-14 Конвертер для массива сущностей в массив датаобджектов
    public ArrayList<DataObject> toDO(ArrayList<BaseEntitie> entities) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        ArrayList<DataObject> dataObjects = new ArrayList<>();
        for (BaseEntitie entitie : entities) {
            dataObjects.add(toDO(entitie));
        }
        return dataObjects;
    }


}
