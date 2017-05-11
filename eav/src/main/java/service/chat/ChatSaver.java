package service.chat;

import com.google.common.cache.LoadingCache;
import entities.DataObject;
import entities.Meeting;
import entities.Message;
import entities.User;
import service.LoadingServiceImp;
import service.UserServiceImp;
import service.cache.DataObjectCache;
import service.converter.Converter;
import service.id_filters.EventFilter;
import service.id_filters.MessageFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 10.05.2017.
 */
// Класс-сейвер для хранения истории чатов для разных встреч (чтобы не лезть все время в базу или не вылавливать из кэша датаобджекты - вроде как кэш первого уровня)
public class ChatSaver {

    private static volatile ChatSaver instance;

    private LoadingServiceImp loadingService = new LoadingServiceImp();
    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();
    private UserServiceImp userService = new UserServiceImp();

    // 0) Счетчик временного айди для создаваемых сообщений (для того, чтобы не путать, берем с обратным знаком):
    private static Integer tmp_id = -1;

    // 1) 2017-05-09 Мапа для хранения истории сообщений чата (для конкретной встречи)
    public static final Map<Integer, ArrayList<Message>> messageMap = new ConcurrentHashMap<>();

    // 2) 2017-05-09 Мапа для хранения очередности пользователей чата (для конкретной встречи)
    public static final Map<Integer, ArrayList<User>> userMap = new ConcurrentHashMap<>();

    // 3) 2017-05-09 Мапа для хранения встреч
    public static final Map<Integer, Meeting> meetingMap = new ConcurrentHashMap<>();

    public static ChatSaver getInstance() {
        if (instance == null)
            synchronized (ChatSaver.class) {
                if (instance == null)
                    instance = new ChatSaver();
            }
        return instance;
    }

    // Конструктор:
    public ChatSaver() {
    }

    // Генератор временного айди сообщения (до сброса в базу)
    public static Integer genereteTempId(){
        return (tmp_id - 1);
    }

    // 2017-05-10 Метод добавления в сейвер нового сообщения по айдишнику встречи
    synchronized public static void addMessage(Integer meeting_id, Message message) throws ParseException, NoSuchMethodException, ExecutionException, IllegalAccessException, SQLException, InvocationTargetException {
        // ключ для мапы:
        Integer key = meeting_id; // то-то типа "2"

        // 1 Проверяем мапу встреч на наличие этой встречи, если ее нет, то подгружаем из базы (но это вряд ли, уже все должно быть)
        if (meetingMap.get(key) == null) { // если нет, то подвешиваем встречу
            loadHistoryForMeeting(meeting_id);

        }

        // 2 (МОЖНО И БЕЗ ЭТОГО, Т.К. ТАКОГО НЕ ДОЛЖНО ПРОИЗОЙТИ - список к этому моменту должен быть) Проверяем мапу встреч на наличие списка сообщений для данного чата, если его нет, то создаем
        if (messageMap.get(key) == null) { // если нет, то создаем ячейку
            messageMap.put(key, new ArrayList<>());

        }
        // И заносим в нее сообщение
        messageMap.get(key).add(message);

        // 3 А теперь вещаем пользователя
        // для этого сначала ищем пользователя
        // Среди пользователей, прикрепленных ко встрече (так быстрее)
        Integer user_id = message.getFrom_id();
        // Вот тут получаем пользователя (участника)
        User user = meetingMap.get(key).getMemberByMemberId(user_id);
        // Если все хорошо (а так и должно быть! Не участник не может написать в чат встречи по сути)
        // то вешаем этого пользователя к мапе очередности пользователей:
        if (user == null){
            // Иначе случилось невозможное, но на всякий случай подстрахуемся и подгрузим юзера из базы
            DataObject dataObject =  new LoadingServiceImp().getDataObjectByIdAlternative(user_id);
            user = new Converter().ToUser(dataObject);
        }
        // вешаем этого пользователя к мапе очередности пользователей:
        userMap.get(key).add(user);

    }


    // 2017-05-10 Метод подгрузки из базы истории сообщений по айди встречи (должен выполняться в самом начале,
    // когда подгружается страница встречи и если до этого не бала подгружена история
    // (страница грузится первый раз с момента старта приложения)
    synchronized private static void loadHistoryForMeeting(Integer meeting_id) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        // ключ для мапы:
        Integer key = meeting_id; // то-то типа "2

        // 1 Проверяем мапу встреч на наличие этой встречи, если она есть, то ничего не делаем
        if (meetingMap.get(key) != null) {
            return;
        }

        // 2 Если же ее нет в сейвере, подгружаем из базы
        Meeting meeting = getMeetingById(meeting_id);
        // и вешаем в сейвер к мапе
        meetingMap.put(key, meeting);

        // 3 Получаем все сообщения из базы, сортируя их в хронологическом порядке:
        ArrayList<Integer> idss = new LoadingServiceImp().getListIdFilteredAlternative(new MessageFilter(MessageFilter.FOR_USER_WITH_ID, meeting_id.toString())); // тут такая хитрость, что юзером-получателем явдяется Встреча (ее айди)
        // Сортируем в порядке увеличения айди (т.к. большему айди соответсвует более поздняя дата создания)
        Collections.sort(idss);
        ArrayList<DataObject> aldo = new LoadingServiceImp().getListDataObjectByListIdAlternative(idss);
        ArrayList<Message> messages = new Converter().ToMessage(aldo);

        // 4 Готовим для сообщений список юзеров:
        ArrayList<User> users = getUsersForMessagesList(meeting_id, messages);

        // 5 Теперь у нас есть и список сообщений, и список пользователей-отправителей к ним, вешаем все на соответствующие мапы:
        messageMap.put(key, messages);
        userMap.put(key, users);
    }



    // 2017-05-10 Получение встречи по ее айди
    public static Meeting getMeetingById(Integer meeting_id) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        DataObject dataObject =  new LoadingServiceImp().getDataObjectByIdAlternative(meeting_id);
        return new Meeting(dataObject);
    }


    // getAllMessages -- получить вообще все сообщения чата данной встречи

    // 2017-05-11 Метод получения всех сообщений чата данной встречи
    public static ArrayList<Message> getAllMessages(Integer meeting_id){
        return messageMap.get(meeting_id);
    }

    // 2017-05-11 Парный с методом getAllMessages метод получения всех юзеров-отправителей:
    public static ArrayList<User> getUsersForAllMessages(Integer meeting_id){
        return userMap.get(meeting_id);
    }

    // getMessagesAfterId  -- получить все сообщения после сообщения с айди таким-то

    // 2015-07-11 Метод получения сех сообщений начиная с такого-то номера (айди) из сейвера
    public static ArrayList<Message> getMessagesAfterId(Integer meeting_id, Integer message_id){
        // 1 Сначала получаем все сообщения по ключу (айди встречи):
        ArrayList<Message> messages = messageMap.get(meeting_id);

        // 2 Обходим весь список и переносим только те сообщения, айди которых больше переданного:
        // При этом надо помнить, что если передали отрицательный айди, то это означает, что нам нужны только сообщения
        // из текущей сессии, а те что в базе уже не нужны, а для отрацительных каждый следующий будет МЕНЬШЕ предыдущего
        // Поэтому тут вот такое ветвление на две части:

        ArrayList<Message> afterMessages = new ArrayList<>();

        if (message_id > 0){
            // То надо подгружать все что больше, а также все отрицательные
            for(Message message : messages){
                if ( (message.getId() != null) && ( ( message.getId() > message_id) || ( message.getId() < 0) ) ){
                    afterMessages.add(message);
                }
            }
        }
        else {
            // То надо подгружать только ОТРИЦАТЕЛЬНЫЕ те, что меньше по айди, чем message_id
            for(Message message : messages){
                if ( (message.getId() != null) && (message.getId() < message_id) ){
                    afterMessages.add(message);
                }
            }
        }

        return afterMessages;
    }


    // 2017-05-11 Парный с методом getMessagesAfterId метод получения всех юзеров-отправителей:
    public static ArrayList<User> getUsersForMessagesAfterId(Integer meeting_id, Integer message_id) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        // 1 Сначала получаем все последующие интересующие сообщения
        ArrayList<Message> afterMessages = getMessagesAfterId(meeting_id, message_id);

        // 2 А затем всех отправителей для данного списка сообщений:
        ArrayList<User> users = getUsersForMessagesList(meeting_id, afterMessages);

        return users;
    }



    // 2017-05-11 Альтернативный (вспомогательный) метод получания всех отправителей по переданному списку сообщений
    // наверное, его предпочтительнее использовать, если до него уже вызывался метод getMessagesAfterId
    public static ArrayList<User> getUsersForMessagesList(Integer meeting_id, ArrayList<Message> messages) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        // 1 Получаем нужную встречу:
        Meeting meeting = meetingMap.get(meeting_id);

        // 2 Обходим все сообщения и готовим для них список юзеров:
        ArrayList<User> users = new ArrayList<>();

        for (Message mess : messages){
            Integer user_id = mess.getFrom_id();
            User user = meeting.getMemberByMemberId(user_id);
            if (user == null){
                // Иначе случилось невозможное, но на всякий случай подстрахуемся и подгрузим юзера из базы
                DataObject dataObject =  new LoadingServiceImp().getDataObjectByIdAlternative(user_id);
                user = new Converter().ToUser(dataObject);
            }
            if (user != null){
                users.add(user);
            }
        }


        return users;
    }


    // еще нужно организовать сброс в базу (тех, у которых отрицательные айдишники
    // (с последующим выставлением правильных айдишников) - так и будут отличаться сообщения, которые сохранили в базу, от тех, которые еще в базу не сохранили

    // 2017-05-11 Метод обновления айди у сообщений, (например, нужен при сбросе сообщений в базу, когда у них временные айди отрицательные меняются на постоянные положительные)
    public static void updateMessageId(Integer meeting_id, Integer old_message_id, Integer new_message_id){
        // Обходим все сообщения данной встречи в поисках нужного (с нужным айдишником)
        for (int i = 0; i < messageMap.get(meeting_id).size(); i++){
            Integer current_message_id = messageMap.get(meeting_id).get(i).getId();
            if (current_message_id != null && current_message_id.equals(old_message_id)){
                // и если нашли то что нужно, меняем ему айди
                messageMap.get(meeting_id).get(i).setId(new_message_id);
            }
        }
    }

    // 2017-05-11 Метод получения всех сообщений (для заданной встречи) с отрицательными айди (то есть всех новых сообщений, которые еще не продублированы в базу):
    public static ArrayList<Message> getAllNewMessages(Integer meeting_id){
        // 1 Сначала получаем все сообщения по ключу (айди встречи):
        ArrayList<Message> messages = messageMap.get(meeting_id);

        // 2 Обходим весь список и переносим только те сообщения, айди которых отрицательные:
        ArrayList<Message> newMessages = new ArrayList<>();
        for(Message message : messages){
            if ( (message.getId() != null) && (message.getId() < 0) ){
                newMessages.add(message);
            }
        }
        return newMessages;
    }







}
