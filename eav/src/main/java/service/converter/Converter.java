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
        } catch (Exception e) {
            e.printStackTrace();
        }
        return user;
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
            event.setInfo(dataObject.getParameter(104));
            event.setPriority(dataObject.getParameter(105));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return event;
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
            // 2017-02-28 Работаем с Событиями
            Event event = (Event) entitie;

            dataObject.setId(event.getId());
            dataObject.setObjectTypeId(EVENT);
            dataObject.setName(event.getName());
            //
            dataObject.setParams(101, event.getDate_begin());
            dataObject.setParams(102, event.getDate_end());
            dataObject.setParams(104, event.getInfo());
            dataObject.setParams(105, event.getPriority());
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
        } else { // иначе сами не понимаем, что конвертируем
            dataObject = null;
        }
        return dataObject;
    }

    // 2017-03-07 Конвертер для массива событий в массив датаобджектов
    public ArrayList<DataObject> toDO(ArrayList<Event> events) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        ArrayList<DataObject> dataObjects = new ArrayList<>();
        for (Event event : events) {
            dataObjects.add(toDO(event));
        }
        return dataObjects;
    }

}
