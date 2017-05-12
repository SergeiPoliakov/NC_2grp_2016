package service.chat;

import entities.Message;
import entities.User;
import service.UserServiceImp;
import service.converter.DateConverter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 11.05.2017.
 */
// Класс для взаимодействия контроллера чата и действий с чат-сервером
public class ChatManager {

    private UserServiceImp userService = new UserServiceImp();

    // 2017-05-11 Вспомогательный метод превращения списка сообщений и списка ользователей в список response
    private ArrayList<ChatResponse> toResponses(ArrayList<Message> messages, ArrayList<User> users){
        // 1 Создаем список ответов
        ArrayList<ChatResponse> responses = new ArrayList<>();
        // 2 А затем обходим все сообщения и подготавливаем ChatResponse:
        for(int i = 0; i < messages.size(); i++){
            Message message = messages.get(i);
            User user = users.get(i);
            responses.add(new ChatResponse(message.getId(), message.getDate_send(), message.getText(), message.getFrom_id(), message.getFrom_name(), user.getPicture()));
        }
        return responses;

    }


    // 2017-05-11 Метод получения из сейвера всех сообщений для данной встречи
    public ArrayList<ChatResponse> getAll(ChatRequest chatRequest) {
        // 1 Получаем айдишник встречи
        Integer meeting_id = chatRequest.getMeeting_id();
        // 2 и подгружаем весь список сообщений
        ArrayList<Message> messages = ChatSaver.getAllMessages(meeting_id);
        // 3 и весь список отправителей:
        ArrayList<User> users = ChatSaver.getUsersForAllMessages(meeting_id);
        // 4 И возвращаем сконвертированный список ответов:
        return toResponses(messages, users);
    }



    // 2017-05-11 Метод получения из сейвера сообщений начиная с определенного айди
    public ArrayList<ChatResponse> getMessagesAfterId(ChatRequest chatRequest) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // 0 Создаем список ответов
        ArrayList<ChatResponse> responses = new ArrayList<>();
        // 1 Получаем айдишник встречи
        Integer meeting_id = chatRequest.getMeeting_id();
        // 2 Получаем айдишник сообщения, начиная с которого надо вытащать сообщения
        Integer message_id = chatRequest.getMessage_id();
        // 3 Если айдишник null, ничего не подгружаем
        if (message_id == null) return responses;
        // 4 Иначе все норм, подгружаем список сообщений начиная с этого айди:
        ArrayList<Message> messages = ChatSaver.getMessagesAfterId(meeting_id, message_id);
        // 5 И весь список отправителей:
        ArrayList<User> users = ChatSaver.getUsersForMessagesList(meeting_id, messages);
        // 6 И возвращаем сконвертированный список ответов:
        return toResponses(messages, users);
    }

    // 2017-05-11 Метод отправки нового сообщения в сейвер
    public void sendMessage(ChatRequest chatRequest) throws SQLException, InvocationTargetException, NoSuchMethodException, ExecutionException, ParseException, IllegalAccessException {

        if (chatRequest.getText().length() < 1) return; // Проверка и защита от записи пустого сообщения

        // 0 Выставляем айди сообщения - пока что временно ноль:
        Integer id = 0;
        // 1 Определяем айди отправителя как айди текущего юзера
        User user = userService.getCurrentUser();
        Integer from_id = user.getId();
        // 2 Определяем айди получателя как айди текущей встречи (это ведь чат, а не личка)
        Integer to_id = chatRequest.getMeeting_id();
        // 3 Получаем текущую дату-время в нужном вормате в виде строки "dd.MM.yyyy HH:mm:ss":
        String date_send = DateConverter.getNowSS();
        // 4 Статус прочтения:
        Integer read_status = 0;
        // 5 Текст сообщения
        String text = chatRequest.getText();
        // 6 Имя отправителя
        String from_name = user.getName();
        // 7 Имя получателя
        String to_name = "chat_" + to_id;
        // 8 Создаем новое сообщение
        Message message = new Message(id, from_id, to_id, date_send, read_status, text, from_name, to_name);
        // 9 И отдаем его в сейвер:
        ChatSaver.addMessage(to_id, message);
    }

}
