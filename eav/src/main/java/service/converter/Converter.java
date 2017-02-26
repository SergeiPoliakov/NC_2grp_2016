package service.converter;
import entities.*;
/**
 * Created by Hroniko on 25.02.2017.
 * Класс для конвертирования из сущностей в датаобджекты и обратно
 */
public class Converter {

    Integer USER = 1001;
    Integer EVENT = 1002;
    Integer MESSAGE = 1003;
    Integer MEETING = 1004;

    public Message ToMessage (DataObject dataObject){
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
        }
        catch (Exception e){
            e.printStackTrace();
        }
        return message;
    }


    public User ToUser (DataObject dataObject){
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


        }
        catch (Exception e){
            e.printStackTrace();
        }
        return user;
    }

    // Конвертация в DataObbject'ы
    public DataObject toDO (BaseEntitie entitie){
        DataObject dataObject = new DataObject();

        if (entitie instanceof User) {
            // Работаем с Пользователями
            User user = (User) entitie;

            dataObject.setId(user.getId());
            dataObject.setObjectTypeId(USER);

            // И надо реализовать ...
        }
        else if (entitie instanceof Message) {
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

        }
        else if (entitie instanceof Event) {
            // Работаем с Событиями
            Event event = (Event) entitie;

            dataObject.setId(event.getId());
            dataObject.setObjectTypeId(EVENT);

            // И надо реализовать ...
        }
        else if (entitie instanceof Meeting) {
            // Работаем со Встречами
            Meeting meeting = (Meeting) entitie;

            dataObject.setId(meeting.getId());
            dataObject.setObjectTypeId(MEETING);

            // И надо реализовать ...
        }
        else if (entitie instanceof DataObject) {
            // Работаем со Датаобджектом, его конвертировать не надо
            dataObject = (DataObject)entitie;
        }
        else{ // иначе сами не понимаем, что конвертируем
            dataObject = null;
        }
        return dataObject;
    }

}
