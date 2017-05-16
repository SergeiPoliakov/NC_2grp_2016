package entities;

import dbHelp.DBHelp;
import service.LoadingServiceImp;
import service.converter.Converter;
import service.converter.DateConverter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Костя on 07.02.2017.
 */

public class Meeting extends BaseEntitie {

    public static final int objTypeID = 1004;

    private Integer id;
    private String title; // 301
    private String date_start; // 302
    private String date_end; // 303
    private String info; // 304
    private User organizer; // 305
    private StringBuilder tag; // 306
    private String allUsers; // 307 // Вобще все пользователи
    private ArrayList<User> users = new ArrayList<>(); // 307 // 2017-05-14 Все пользователи, так или иначе связанные со встречей (участники, приглашенные, удаленные и пр. - объединяющий список)
    private ArrayList<Event> events = new ArrayList<>(); // 308
    private ArrayList<Event> duplicates = new ArrayList<>(); // 313 // Копии задач-отображений встречи на расписание подписанных пользователей (участников встречи) // надо бы повесить загрузчик из базы // вроде бы есть уже
    private String status; // 309
    private String duration; //310
    // 311 - ссылка на удаленного (-ых) из встречи юзеров, в базе есть
    private String date_edit; // 312

    // 2017-05-14 Новые группы пользователей встречи:
    private ArrayList<User> beggingUsers = new ArrayList<>(); // 314 Желающие принять участие (самостоятельно подавшие запрос на участие (кнопка Участвовать на странице встречи))
    private ArrayList<User> invitedUsers = new ArrayList<>(); // 315 Приглашенные создателем встречи
    private ArrayList<User> acceptedUsers = new ArrayList<>(); // 316 Принявшие приглашение от создателя встречи (и они же автоматом становятся участниками)
    private ArrayList<User> refusedUsers = new ArrayList<>(); // 317 Отказавшиеся (отклонившие приглашение от создателя встречи)
    private ArrayList<User> memberUsers = new ArrayList<>(); // 318 Действительные участники
    private ArrayList<User> exitedUsers = new ArrayList<>(); // 319 Покинувшие встречу (уже после принятия - они могут еще передумать и вернуться)
    private ArrayList<User> blockedUsers = new ArrayList<>(); // 320 Заблокированные администратором - они не могут участвовать в встрече и подавать запрос на участие
    private ArrayList<User> deletedUsers = new ArrayList<>(); // 321 Удаленные администратором - они могут повторно подать зарос на участие и участвовать в встрече



    public ArrayList<Event> getEvents() {
        return events;
    }

    public void setEvents(ArrayList<Event> events) {
        this.events = events;
    }

    public User getOrganizer() {
        return organizer;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public ArrayList<User> getUsers() {
        return users;
    }

    public void setUsers(ArrayList<User> users) {
        this.users = users;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDate_start() {
        return date_start;
    }

    public void setDate_start(String date_start) {
        this.date_start = date_start;
    }

    public String getDate_end() {
        return date_end;
    }

    public void setDate_end(String date_end) {
        this.date_end = date_end;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public StringBuilder getTag() {
        return tag;
    }

    public void setTag(StringBuilder tag) {
        this.tag = tag;
    }

    public String getAllUsers() {
        return allUsers;
    }

    public void setAllUsers(String allUsers) {
        this.allUsers = allUsers;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static int getObjTypeID() {
        return objTypeID;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getDate_edit() {
        return date_edit;
    }

    public void setDate_edit(String date_edit) {
        this.date_edit = date_edit;
    }

    public ArrayList<Event> getDuplicates() {
        return duplicates;
    }

    public void setDuplicates(ArrayList<Event> duplicates) {
        this.duplicates = duplicates;
    }

    public void setDuplicates(Event duplicate) {
        this.duplicates.add(duplicate);
    }

    // 2017-04-11 Метод создания дубликата встречи (события) как отображения в пользовательское расписание:
    public void createDuplicate(Integer user_id) throws SQLException, NoSuchMethodException, IllegalAccessException, ParseException, InvocationTargetException {

        // Небольшая проверка, чтобы не делать несколько дубликатов у одного и того же пользователя и не давать дубликаты запрещенному пользователю
        // 1 Если пользователь заблокирован, удален или покинул встречу, никаких ему дублиатов! А тем более если его нет в общем списке users
        User find_user = null;
        for (User user : this.users){
            if (user.getId().equals(user_id)){
                find_user = user;
                break;
            }
        }
        if (find_user == null) return; // Если нет такого пользователя, никаких ему дубликатов!
        // 2 А если есть, проверяем его права:
        if (this.blockedUsers.contains(find_user) || this.deletedUsers.contains(find_user) || this.exitedUsers.contains(find_user)) return; // Нет прав - никаких дубликатов
        // 3 Если же не было ограничений, то нужны разрешения - а именно членство в группе memberUsers
        if (! this.memberUsers.contains(find_user)) return; // Нет членства - тоже никаких дубликатов!
        // 4 Если же есть членство в memberUsers, надо еще проверить, может, уже есть дубликат:
        //for(Event event : this.getDuplicates()){
        //    if (event.getHost_id().equals(user_id)) return; // Если есть дубликат, больше никаких дубликатов!               Ломает работу, пока закомментрил
        //}
        // 5 Иначе можно продолжить и выдать пользователю дубликат как действующему участнику встречи:
        //

        Event duplicate = new Event();
        duplicate.setId(new DBHelp().generationID(1002));
        duplicate.setHost_id(user_id);
        duplicate.setName(this.title);
        // duplicate.setDate_begin(this.date_start);
        // duplicate.setDate_end(this.date_end);
        duplicate.setPriority("Style4"); // 4-ый приоритет, соотвествующий дубликату встречи, надо сделать для него другой цвет (и полупрозрачность) на страничке
        duplicate.setInfo(this.info);
        duplicate.setType_event(Event.DUPLICATE_EVENT);
        // И проверяем, что у нас за встреча:
        if (this.date_edit == null){
            // имеем дело со встречей с фиксированными границами
            duplicate.setEditable(Event.UNEDITABLE);
            duplicate.setDate_begin(this.date_start);
            duplicate.setDate_end(this.date_end);
        }
        else{
            // иначе имеем дело со встречей с плавающими границами
            duplicate.setDuration(this.duration);
            duplicate.setType_event(Event.DUPLICATE_EVENT);
            duplicate.setEditable(Event.EDITABLE);
            // Копируем плавающие границы
            duplicate.setFloating_date_begin(this.date_start);
            duplicate.setFloating_date_end(this.date_end);

            // Рассчитываем и переносим действительные границы в зависимости от продолжительности встречи:
            duplicate.setDate_begin(this.date_start);
            LocalDateTime end = DateConverter.stringToDate(this.date_start);
            end = end.plus(Duration.ofMinutes(new Long(this.getDuration())));
            String s_end = DateConverter.dateToString(end);
            duplicate.setDate_end(s_end);
        }

        // Привешиваем дубликат к нашей встрече
        this.setDuplicates(duplicate);
        // и сохраняем в базу:
        DataObject dataObject = new Converter().toDO(duplicate);
        new LoadingServiceImp().setDataObjectToDB(dataObject);
    }

    // 2017-05-14 Метод удаления дубликата встречи (события), отвечающего пользователю с переданным айди:
    public void deleteDuplicate(Integer user_id){
        for(int i = 0; i < this.duplicates.size(); i++){
            if (duplicates.get(i).getHost_id().equals(user_id)){
                duplicates.remove(i);
                return;
            }
        }
    }


    public Meeting(){}

    public Meeting(int id, String title, String date_start, String date_end, String info, User organizer, StringBuilder tag, String allUsers, String duration) {
        this.id = id;
        this.title = title;
        this.date_start = date_start;
        this.date_end = date_end;
        this.info = info;
        this.organizer = organizer;
        this.tag = tag;
        this.allUsers = allUsers;
        this.status = "active";
        this.duration = duration;
    }

    public Meeting(String title, String date_start, String date_end, String info, User organizer, StringBuilder tag, String allUsers, String duration) {
        this.title = title;
        this.date_start = date_start;
        this.date_end = date_end;
        this.info = info;
        this.organizer = organizer;
        this.tag = tag;
        this.allUsers = allUsers;
        this.status = "active";
        this.duration = duration;
    }

    // 2017-04-09 21-52 Самое то для новых встреч
    public Meeting(String title, String date_start, String date_end, String info, User organizer, StringBuilder tag, String allUsers, String duration, String date_edit) {
        this.title = title;
        this.date_start = date_start;
        this.date_end = date_end;
        this.info = info;
        this.organizer = organizer;
        this.tag = tag;
        this.allUsers = allUsers;
        this.status = "active";
        this.duration = duration;
        this.date_edit = date_edit;
    }


    public Meeting(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.users = new ArrayList<>();
        this.organizer = new User();
        this.events = new ArrayList<>();
        this.id = dataObject.getId();
        // Поле params
        for (Map.Entry<Integer, String> param : dataObject.getParams().entrySet() ) {
            switch (param.getKey()){
                case (301):
                    this.title = param.getValue();
                    break;
                case (302):
                    this.date_start = param.getValue();
                    break;
                case (303):
                    this.date_end = param.getValue();
                    break;
                case (304):
                    this.info = param.getValue();
                    break;
                case (305):
                    this.organizer = new User(new DBHelp().getObjectsByIdAlternative(Integer.parseInt(param.getValue())));
                    break;
                case (306):
                    this.tag = new StringBuilder(param.getValue());
                    break;
                case (309):
                    this.status = param.getValue();
                    break;
                case (310):
                    this.duration = param.getValue();
                    break;
                case (312):
                    this.date_edit = param.getValue();
                    break;
            }
        }
        // Поле ссылок
        for (Map.Entry<Integer, ArrayList<Integer>> reference : dataObject.getRefParams().entrySet() ) {
            switch (reference.getKey()){
                // Users
                case (307):
                    for (Integer refValue: reference.getValue()) {

                        this.users.add(new User(new DBHelp().getObjectsByIdAlternative(refValue)));
                    }
                    break;
                // Events
                case (308):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.events.add(new Event(new DBHelp().getObjectsByIdAlternative(refValue)));
                        }
                    }
                    break;

                // Events duplicates
                case (313):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.duplicates.add(new Event(new DBHelp().getObjectsByIdAlternative(refValue)));
                        }
                    }
                    break;


                // beggingUsers // 314 Желающие принять участие
                case (314):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.beggingUsers.add(new User(new DBHelp().getObjectsByIdAlternative(refValue)));
                        }
                    }
                    break;


                // invitedUsers // 315 Приглашенные создателем встречи
                case (315):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.invitedUsers.add(new User(new DBHelp().getObjectsByIdAlternative(refValue)));
                        }
                    }
                    break;


                // acceptedUsers // 316 Принявшие приглашение от создателя встречи
                case (316):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.acceptedUsers.add(new User(new DBHelp().getObjectsByIdAlternative(refValue)));
                        }
                    }
                    break;

                // refusedUsers // 317 Отказавшиеся (отклонившие приглашение от создателя встречи)
                case (317):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.refusedUsers.add(new User(new DBHelp().getObjectsByIdAlternative(refValue)));
                        }
                    }
                    break;

                // memberUsers // 318 Действительные участники
                case (318):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.memberUsers.add(new User(new DBHelp().getObjectsByIdAlternative(refValue)));
                        }
                    }
                    break;

                // exitedUsers // 319 Покинувшие встречу
                case (319):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.exitedUsers.add(new User(new DBHelp().getObjectsByIdAlternative(refValue)));
                        }
                    }
                    break;

                // blockedUsers // 320 Заблокированные администратором
                case (320):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.blockedUsers.add(new User(new DBHelp().getObjectsByIdAlternative(refValue)));
                        }
                    }
                    break;

                // deletedUsers // 321 Удаленные администратором
                case (321):
                    if (reference != null) {
                        for (Integer refValue : reference.getValue()) {
                            this.deletedUsers.add(new User(new DBHelp().getObjectsByIdAlternative(refValue)));
                        }
                    }
                    break;
            }
        }
    }

    public DataObject toDataObject(){
        DataObject dataObject = new DataObject();
        dataObject.setId(this.id);
        dataObject.setName(this.title);
        dataObject.setObjectTypeId(1004);
        dataObject.setParams(301, this.title);
        dataObject.setParams(302, this.date_start);
        dataObject.setParams(303, this.date_end);
        dataObject.setParams(304, this.info);
        dataObject.setParams(305, this.organizer.getId().toString());
        dataObject.setParams(306, new String(this.tag));
        dataObject.setParams(309, this.status);
        dataObject.setParams(310, this.duration);
        dataObject.setParams(312, this.date_edit);

        for (User user: this.users) {
            dataObject.setRefParams(307, user.getId());
        }

        if (this.events != null) {
            for (Event event : this.events) {
                dataObject.setRefParams(308, event.getId());
            }
        }

        if (this.duplicates != null) {
            for (Event duplicate : this.duplicates) {
                dataObject.setRefParams(313, duplicate.getId());
            }
        }

        /// 2017-05-14 Перенос групп пользователей:

        if (this.beggingUsers != null) {
            for (User user : this.beggingUsers) {
                dataObject.setRefParams(314, user.getId());
            }
        }

        if (this.invitedUsers != null) {
            for (User user : this.invitedUsers) {
                dataObject.setRefParams(315, user.getId());
            }
        }

        if (this.acceptedUsers != null) {
            for (User user : this.acceptedUsers) {
                dataObject.setRefParams(316, user.getId());
            }
        }

        if (this.refusedUsers != null) {
            for (User user : this.refusedUsers) {
                dataObject.setRefParams(317, user.getId());
            }
        }

        if (this.memberUsers != null) {
            for (User user : this.memberUsers) {
                dataObject.setRefParams(318, user.getId());
            }
        }

        if (this.exitedUsers != null) {
            for (User user : this.exitedUsers) {
                dataObject.setRefParams(319, user.getId());
            }
        }

        if (this.blockedUsers != null) {
            for (User user : this.blockedUsers) {
                dataObject.setRefParams(320, user.getId());
            }
        }

        if (this.deletedUsers != null) {
            for (User user : this.deletedUsers) {
                dataObject.setRefParams(321, user.getId());
            }
        }
        ///

        return dataObject;
    }

    public TreeMap<Integer, Object> getArrayWithAttributes(){
        TreeMap<Integer, Object> map = new TreeMap<>();
        map.put(301, title);
        map.put(302, date_start);
        map.put(303, date_end);
        map.put(304, info);
        map.put(305, organizer.getId());
        map.put(306, tag);
        map.put(307, allUsers);
        map.put(309, status);
        map.put(310, duration);
        map.put(312, date_edit);
        return map;
    }

    // Метод получения айдишников дубликатов встречи - ее отображений в виде события на расписание юзеров
    public ArrayList<Integer> getDuplicateIDs(){
        ArrayList<Integer> ids = new ArrayList<>();
        for (Event dublicate: this.duplicates) {
            ids.add(dublicate.getId());
        }
        return ids;
    }

    // 2017-05-07 Переопределяем метод клонирования
    @Override
    public Object clone() throws CloneNotSupportedException {
        Meeting copyMeeting = new Meeting();
        copyMeeting.setId(this.getId()); // 1
        copyMeeting.setTitle(this.getTitle()); // 301
        copyMeeting.setDate_start(this.getDate_start()); // 302
        copyMeeting.setDate_end(this.getDate_end()); // 303
        copyMeeting.setInfo(this.getInfo()); // 304
        copyMeeting.setOrganizer(this.getOrganizer()); // 305
        copyMeeting.setTag(this.getTag()); // 306
        copyMeeting.setAllUsers(this.getAllUsers()); // 307
        copyMeeting.setUsers(this.getUsers()); // 307
        copyMeeting.setEvents(this.getEvents()); // 308

        // А вот дупликаты встреч лучше клонировать, так как их мы будем редактировать потом (по крайней мере одно точно)
        ArrayList<Event> dupls = new ArrayList<>();
        if (this.getDuplicates() != null && this.getDuplicates().size() > 0){
            for(Event ev : this.getDuplicates()){
                dupls.add((Event) ev.clone());
            }
        }
        copyMeeting.setDuplicates(dupls); // 313

        copyMeeting.setStatus(this.getStatus()); // 309
        copyMeeting.setDuration(this.getDuration()); // 310
        copyMeeting.setDate_edit(this.getDate_edit()); // 312

        // 2017-05-14 Копирование групп юзеров
        copyMeeting.setBeggingUsers(this.getBeggingUsers()); // 314
        copyMeeting.setInvitedUsers(this.getInvitedUsers()); // 315
        copyMeeting.setAcceptedUsers(this.getAcceptedUsers()); // 316
        copyMeeting.setRefusedUsers(this.getRefusedUsers()); // 317
        copyMeeting.setMemberUsers(this.getMemberUsers()); // 318
        copyMeeting.setExitedUsers(this.getExitedUsers()); // 319
        copyMeeting.setBlockedUsers(this.getBlockedUsers()); // 320
        copyMeeting.setDeletedUsers(this.getDeletedUsers()); // 321

        return copyMeeting;
    }

    // 2017-05-07 Переопределяем метод приведения к строке
    @Override
    public String toString() {
        return "Встреча " + "{id=" + id
                + " : title=" + title
                + " : date_start=" + date_start
                + " : date_end=" + date_end
                + " : info=" + info
                + " : organizer=" + organizer.getId()
                + " : tag=" + tag
                + " : allUsers=" + allUsers

                + " : users=" + users
                + " : events=" + events
                + " : duplicates=" + duplicates
                + " : status=" + status
                + " : duration=" + duration
                + " : date_edit=" + date_edit +"}";
    }

    // 2017-05-10 Метод получения сущности юзера-участника по переданному id
    public User getMemberByMemberId(Integer user_id){
        // 1 Проверяем, может быть этим пользователем является организатор:
        if (this.organizer != null && this.organizer.getId() != null && this.organizer.getId().equals(user_id)) return this.organizer;
        // 2 если нет, начинаем искать среди Users списка:
        User find_user = null;
        for (User user : this.getUsers()){
            if (user.getId() != null && user.getId().equals(user_id)){
                find_user = user;
                break;
            }
        }
        return find_user;
    }

    // 2017-05-14 Методы получения и редактирования групп пользователей встречи:

    // 1-1 Метод получения группы пользователей, отправивших запрос на участие
    public ArrayList<User> getBeggingUsers() {
        return beggingUsers;
    }
    // 1-2 Метод задания группы пользователей, отправивших запрос на участие
    public void setBeggingUsers(ArrayList<User> beggingUsers) {
        this.beggingUsers = beggingUsers;
    }

    // 1-3 Метод добавления нового пользователя, запросившего участия во встрече (со всеми проверками)
    // Запросивший участия может одновременно состоять в группах 314 beggingUsers, 318 memberUsers, 321 deletedUsers, 319 exitedUsers и 320 blockedUsers
    // в соответствии с одной из траекторий смены членства в группах:
    // 314 beggingUsers -> 321 deletedUsers -> 320 blockedUsers -> отмена 320
    // 314 beggingUsers -> 318 memberUsers -> 321 deletedUsers -> 320 blockedUsers -> отмена 320
    // 314 beggingUsers -> 318 memberUsers -> 319 exitedUsers -> 320 blockedUsers
    // 314 beggingUsers -> 318 memberUsers -> 321 deletedUsers -> отмена 321
    // 314 beggingUsers -> 318 memberUsers -> 319 exitedUsers -> отмена 319
    // А разблокировка (отмена членства в 320 blockedUsers лишь открывает членство в остальных группах, в которых уже к моменту блокировки пользователь состоял
    public void addBeggingUsers(User beggingUser) throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        if (this.blockedUsers.contains(beggingUser)) return; // Если создатель встречи заблочил юзера, выходим
        if (this.beggingUsers.contains(beggingUser)) return; // Если он уже висит в отправивших запрос, выходим
        if (this.memberUsers.contains(beggingUser)) return; // Если он уже висит в участниках, выходим
        if (this.invitedUsers.contains(beggingUser)){  // Если он висит в приглашенных, то
            // this.beggingUsers.add(beggingUser); // Добавляем его в список запросивших // это лишнее, будет только вводить в заблуждение, приглашение создателя встречи в большем приоритете
            this.acceptedUsers.add(beggingUser); // Поскольку намерения создателя встречи и желание прользователя совпали, автоматически причисляем пользователя к заочно принявшим приглашение
            this.memberUsers.add(beggingUser); // И автоматически превращаем его в участника встречи
            // И тут потребуется создать ему дубликат встречи, раз он уже стал участником:
          //  this.createDuplicate(beggingUser.getId());

            // и на всякий случай удалем противоречивую информацию, а именно возможность состоять в группах, которые взаимоисключают текущее состояние:
            this.refusedUsers.remove(beggingUser); // Удаляем из отказавшихся
            this.exitedUsers.remove(beggingUser); // Удаляем из покинувших встречу
            this.deletedUsers.remove(beggingUser); // Удаляем из удаленных
            return;
        }
        if (this.acceptedUsers.contains(beggingUser)) return; // Если он уже висит в принявших приглашение, выходим
        if (this.refusedUsers.contains(beggingUser)){ // Если он уже висит в отказавшихся от приглашения, он уже получал приглашение и его надо перекинуть в принявшие приглашение
            this.acceptedUsers.add(beggingUser); // Причисляем его к принявшим приглашение
            this.memberUsers.add(beggingUser); // И автоматически превращаем его в участника встречи
            // this.createDuplicate(beggingUser.getId()); // Возвращаем дубликат
            this.refusedUsers.remove(beggingUser); // И удаляем из отказавшихся
            return;
        }
        if (this.exitedUsers.contains(beggingUser)) { // Если он уже висит в покинувших встречу,
            this.memberUsers.add(beggingUser); // Возвращаем его во встречу
            // И тут потребуется создать ему дубликат встречи, раз он уже стал участником:
          //  this.createDuplicate(beggingUser.getId());
            this.exitedUsers.remove(beggingUser); // И удаляем его из покинувших встречу
            return;
        }
        if (this.deletedUsers.contains(beggingUser)){ // Если он находится среди удаленных администратором,
            this.memberUsers.add(beggingUser); // Возвращаем его во встречу
            // И тут потребуется создать ему дубликат встречи, раз он уже стал участником:
            //  this.createDuplicate(beggingUser.getId());
            this.deletedUsers.remove(beggingUser); // И удаляем из списка удаленных
            return;
        }

        // Если после всех этих проверок добрались сюда, то имеем дело с совершенно новым юзером, просто вешаем его в список запросивших:
        this.beggingUsers.add(beggingUser);
        // А заодно и в общий список всех заинтересованных пользователей
        this.users.add(beggingUser); // это общий список ВСЕХ юзеров, а не список только одних участников встречи
    }

    // 2-1 Метод получения группы пользователей, которых создатель встречи пригласил участвовать во встрече
    public ArrayList<User> getInvitedUsers() {
        return invitedUsers;
    }

    // 2-2 Метод задания группы пользователей, которых создатель встречи пригласил участвовать во встрече
    public void setInvitedUsers(ArrayList<User> invitedUsers) {
        this.invitedUsers = invitedUsers;
    }

    // 2-3 Метод добавления нового пользователя, которого создатель встречи пригласил участвовать во встрече
    // Запросивший участия может одновременно состоять в группах 315 invitedUsers, 316 acceptedUsers, 317 refusedUsers (316 взаимоисключает 317),
    // 318 memberUsers, 321 deletedUsers, 319 exitedUsers (319 взаимоисключает 321) и 320 blockedUsers
    // в соответствии с одной из траекторий смены членства в группах
    // А разблокировка (отмена членства в 320 blockedUsers лишь открывает членство в остальных группах, в которых уже к моменту блокировки пользователь состоял
    public void addInvitedUsers(User user) throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        if (this.blockedUsers.contains(user)){ // Если создатель встречи до этого заблочил юзера,
            this.blockedUsers.remove(user); // разблокируем его (а дальше смотрим, к каким группам он уже принадлежал)
            // this.invitedUsers.add(user); // И добавляем к списку приглашенных
            // return;
        }
        if (this.beggingUsers.contains(user)){ // Если он уже висит в отправивших запрос,
            this.invitedUsers.add(user); // добавляем к списку приглашенных
            this.acceptedUsers.add(user); // Поскольку намерения создателя встречи и желание прользователя совпали, автоматически причисляем пользователя к заочно принявшим приглашение
            this.memberUsers.add(user); // И автоматически превращаем его в участника встречи
            // this.beggingUsers.remove(user); // И удаляем из запросивших участия, так как создатель встречи его сам пригласил, приоритет этого выше, а 314 и 315 взаимоисключающие
            // И тут потребуется создать ему дубликат встречи, раз он уже стал участником:
            // this.createDuplicate(user.getId());

            // и на всякий случай удалем противоречивую информацию, а именно возможность состоять в группах, которые взаимоисключают текущее состояние:
            this.beggingUsers.remove(user); // удаляем из отправивших запрос, так как он уже вступил на встречу
            this.refusedUsers.remove(user); // Удаляем из отказавшихся
            this.exitedUsers.remove(user); // Удаляем из покинувших встречу
            this.deletedUsers.remove(user); // Удаляем из удаленных
            return;
        }

        if (this.memberUsers.contains(user)) return; // Если он уже висит в участниках, выходим
        if (this.invitedUsers.contains(user)) return;  // Если он висит в приглашенных, то выходим
        if (this.acceptedUsers.contains(user)) { // Если он уже висит в принявших приглашение, делаем его участником
            this.memberUsers.add(user);
            // this.createDuplicate(user.getId()); // Возвращаем дубликат
            return;
        }

        if (this.refusedUsers.contains(user)){ // Если он уже висит в отказавшихся от приглашения,
            this.acceptedUsers.add(user); // перекидываем его в принявших приглашение
            this.memberUsers.add(user); // И автоматически превращаем его в участника встречи
            // this.createDuplicate(user.getId()); // Возвращаем дубликат
            this.refusedUsers.remove(user); // И удаляем из отказавшихся
            return;
        }

        if (this.exitedUsers.contains(user)) { // Если он уже висит в покинувших встречу, возвращаем его в группу участников встречи
            this.memberUsers.add(user); // Возвращаем его во встречу
            // this.createDuplicate(user.getId()); // Возвращаем дубликат
            this.exitedUsers.remove(user); // И удаляем его из покинувших встречу
            return;
        }

        if (this.deletedUsers.contains(user)){ // Если он находится среди удаленных администратором, возвращаем его в группу участников встречи
            this.memberUsers.add(user); // Возвращаем его во встречу
            // this.createDuplicate(user.getId()); // Возвращаем дубликат
            this.deletedUsers.remove(user); // И удаляем из списка удаленных
            return;
        }

        // Если после всех этих проверок добрались сюда, то имеем дело с совершенно новым юзером, просто вещаем его в список приглашенных:
        this.invitedUsers.add(user); // Причисляем его к приглашенным
        // А заодно и в общий список всех заинтересованных пользователей
        this.users.add(user);

    }


    // 3-1 Метод получения группы пользователей, принявших приглашение на встречу
    public ArrayList<User> getAcceptedUsers() {
        return acceptedUsers;
    }

    // 3-2 Метод задания группы пользователей, принявших приглашение на встречу
    public void setAcceptedUsers(ArrayList<User> acceptedUsers) {
        this.acceptedUsers = acceptedUsers;
    }

    // 3-3 Метод добавления нового пользователя в группу пользователей, принявших приглашение на встречу
    // Запросивший участия может одновременно состоять в группах 315 invitedUsers, 316 acceptedUsers, 317 refusedUsers (316 взаимоисключает 317),
    // 318 memberUsers, 321 deletedUsers, 319 exitedUsers (319 взаимоисключает 321) и 320 blockedUsers
    // в соответствии с одной из траекторий смены членства в группах
    // А разблокировка (отмена членства в 320 blockedUsers лишь открывает членство в остальных группах, в которых уже к моменту блокировки пользователь состоял
    public void addAcceptedUsers(User user) throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        if (this.blockedUsers.contains(user)) return; // Если создатель встречи заблочил юзера, выходим
        if (this.deletedUsers.contains(user)) return; // Если он находится среди удаленных администратором, то, что он согласился на встречу, уже не играет роли
        if (this.memberUsers.contains(user)) return; // Если он уже висит среди участников, выходим

        if (this.acceptedUsers.contains(user)){ // Если он уже висит среди принявших приглашение, делаем его участником
            this.memberUsers.add(user); // И автоматически превращаем его в участника встречи
            // И тут потребуется создать ему дубликат встречи, раз он уже стал участником:
          //  this.createDuplicate(user.getId());
            return;
        }

        if (this.invitedUsers.contains(user)){ // Если он уже висит в приглашенных,
            this.acceptedUsers.add(user); // Причисляем к группе принявших приглашение
            this.memberUsers.add(user); // И автоматически превращаем его в участника встречи
            // И тут потребуется создать ему дубликат встречи, раз он уже стал участником:
         //   this.createDuplicate(user.getId());

            // и на всякий случай удалем противоречивую информацию, а именно возможность состоять в группах, которые взаимоисключают текущее состояние:
            this.refusedUsers.remove(user); // Удаляем из отказавшихся
            this.exitedUsers.remove(user); // Удаляем из покинувших встречу
            this.deletedUsers.remove(user); // Удаляем из удаленных
            return;
        }

        if (! this.users.contains(user)) this.users.add(user); // Если этого юзера нет среди общего списка, вешаем его туда

    }

    // 4-1 Метод получения группы пользователей, отказавшихся от приглашения на встречу
    public ArrayList<User> getRefusedUsers() {
        return refusedUsers;
    }

    // 4-2 Метод задания группы пользователей, отказавшихся от приглашения на встречу
    public void setRefusedUsers(ArrayList<User> refusedUsers) {
        this.refusedUsers = refusedUsers;
    }

    // 4-3 Метод добавления нового пользователя в группу пользователей, отказавшихся от приглашения на встречу
    public void addRefusedUsers(User user){
        if (this.blockedUsers.contains(user)) return; // Если создатель встречи заблочил юзера, выходим
        if (this.deletedUsers.contains(user)) return; // Если он находится среди удаленных администратором, то, что он отказался от встречи, уже не играет роли (он опоздал с отказом)
        if (this.exitedUsers.contains(user)) return; // Если он находится среди покинувших встречу, выходим
        if (this.refusedUsers.contains(user)) return; // Если он уже висит в отказавшихся от приглашения, выходим

        if (this.memberUsers.contains(user)) return; // Если он уже висит в участниках, он не может отказаться, это не та траектория смены группы (может только покинуть или его админ может удалить)


        // this.invitedUsers.remove(user);  // если пользователь отказывается, то и приглашение сгорает. // ОСТАВЛЯЕМ его в приглашенных, чтобы сохранить траекторию членства в группах
        this.refusedUsers.add(user); // Добавляем в отказавшиеся от приглашения
        if (! this.users.contains(user)) this.users.add(user); // Если этого юзера нет среди общего списка, вешаем его туда

    }




    // 5-1 Метод получения группы пользователей, являющихся действующими участниками встречи
    public ArrayList<User> getMemberUsers() {
        return memberUsers;
    }

    // 5-2 Метод задания группы пользователей, являющихся действующими участниками встречи
    public void setMemberUsers(ArrayList<User> memberUsers) {
        this.memberUsers = memberUsers;
    }

    // 5-3 Метод добавления нового пользователя в группу пользователей, являющихся действующими участниками встречи
    public void addMemberUsers(User user) throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        if (this.blockedUsers.contains(user)) return; // Если создатель встречи заблочил юзера, выходим
        if (this.deletedUsers.contains(user)) { // Если он находится среди удаленных администратором, возвращаем его в участники
            this.memberUsers.add(user); // Превращаем его в участника встречи
            // this.createDuplicate(user.getId()); // Возвращаем дубликат
            this.deletedUsers.remove(user); // И удаляем из удаленных
            return;
        }

        if (this.exitedUsers.contains(user)) { // Если он уже висит в покинувших встречу, возвращаем его в группу участников встречи
            this.memberUsers.add(user); // Возвращаем его во встречу
            // this.createDuplicate(user.getId()); // Возвращаем дубликат
            this.exitedUsers.remove(user); // И удаляем его из покинувших встречу
            return;
        }

        if (this.memberUsers.contains(user)) return; // Если он находится среди участников, выходим

        if (this.refusedUsers.contains(user)) { // Если он уже висит в отказавшихся от приглашения,
            this.acceptedUsers.add(user); // Делаем его принявшим приглашение
            this.memberUsers.add(user); // и автоматом превращаем его в участника встречи
            this.refusedUsers.remove(user); // И удаляем из отказавшихся
            return;
        }

        if (this.acceptedUsers.contains(user)){ // Если он уже висит среди принявших приглашение, делаем его участником
            this.memberUsers.add(user); // Превращаем его в участника встречи
            // И тут потребуется создать ему дубликат встречи, раз он уже стал участником:
            // this.createDuplicate(user.getId());
            return;
        }

        if (this.beggingUsers.contains(user)){ // Если он уже висит в отправивших запрос,
            this.memberUsers.add(user); // Превращаем его в участника встречи
            // И тут потребуется создать ему дубликат встречи, раз он уже стал участником:
           // this.createDuplicate(user.getId());

            // и на всякий случай удалем противоречивую информацию, а именно возможность состоять в группах, которые взаимоисключают текущее состояние:
            this.refusedUsers.remove(user); // Удаляем из отказавшихся
            this.exitedUsers.remove(user); // Удаляем из покинувших встречу
            this.deletedUsers.remove(user); // Удаляем из удаленных
            return;
        }

        this.memberUsers.add(user);
        if (! this.users.contains(user)) this.users.add(user); // Если этого юзера нет среди общего списка, вешаем его туда

    }


    // 6-1 Метод получения группы пользователей, покинувших встречу уже после согласия принять участие
    public ArrayList<User> getExitedUsers() {
        return exitedUsers;
    }

    // 6-2 Метод задания группы пользователей, покинувших встречу уже после согласия принять участие
    public void setExitedUsers(ArrayList<User> exitedUsers) {
        this.exitedUsers = exitedUsers;
    }

    // 6-3 Метод добавления нового пользователя в группу пользователей, покинувших встречу уже после согласия принять участие
    public void addExitedUsers(User user){
        if (this.blockedUsers.contains(user)) return; // Если создатель встречи заблочил юзера, выходим
        if (this.deletedUsers.contains(user)) return; // Если он находится среди удаленных администратором, выходим (он не может перепрыгнуть на соседнюю траекторию, его уже удалил админ)
        if (this.exitedUsers.contains(user)) return; // Если он уже висит в покинувших встречу, выходим

        if (this.memberUsers.contains(user)) { // Если он находится среди участников,
            this.memberUsers.remove(user); // Удаляем его из участников
            // this.deleteDuplicate(user.getId()); // Удаляем дубликат встречи данного пользователя
            this.exitedUsers.add(user); // и добавляем пользователя к отказавшимся
            return;
        }

        // Во всех остальных случаях он не может стать членом группа покинувших встречу, не став участником, поэтому больше ничего не делаем
        // this.exitedUsers.add(user); // и добавляем пользователя к покинувшим встречу по собственному желанию
       if (! this.users.contains(user)) this.users.add(user); // Если этого юзера нет среди общего списка, вешаем его туда
    }



    // 7-1 Метод получения группы пользователей, заблокированных создателем встречи
    public ArrayList<User> getBlockedUsers() {
        return blockedUsers;
    }

    // 7-2 Метод задания группы пользователей, заблокированных создателем встречи
    public void setBlockedUsers(ArrayList<User> blockedUsers) {
        this.blockedUsers = blockedUsers;
    }

    // 7-3 Метод добавления нового пользователя в группу пользователей, заблокированных создателем встречи
    public void addBlockedUsers(User user){
        if (this.blockedUsers.contains(user)) return; // Если создатель встречи заблочил юзера, выходим

        // Во всех остальных случаях НЕ убираем пользователя из всех групп, а просто добавляем к группе блокированных
        this.blockedUsers.add(user);
        // this.deleteDuplicate(user.getId()); // Удаляем дубликат встречи данного пользователя
        if (! this.users.contains(user)) this.users.add(user); // Если этого юзера нет среди общего списка, вешаем его туда
    }

    // 7-4 Метод блокировки пользователя
    public void blockedUser(User user){
        this.addBlockedUsers(user);
    }

    // 7-5 Метод разблокировки пользователя
    public void unBlockedUser(User user) throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        if (this.blockedUsers.contains(user)){ // Если создатель встречи заблочил юзера, разблокируем
            this.blockedUsers.remove(user);

            if (this.deletedUsers.contains(user)) return; // Если он был в удаленных, выходим
            if (this.exitedUsers.contains(user)) return; // Если он был в покинувших встречу, выходим

            if (this.memberUsers.contains(user)){ // А вот если он был в действующих участниках встречи, возвращаем ему дубликат
                // this.createDuplicate(user.getId());
                return;
            }

            // Все остальные случаи просто игнорируем, сняв блокировку ранее

        }
    }

    // 8-1 Метод получения группы пользователей, удаленных создателем встречи
    public ArrayList<User> getDeletedUsers() {
        return deletedUsers;
    }

    // 8-2 Метод задания группы пользователей, удаленных создателем встречи
    public void setDeletedUsers(ArrayList<User> deletedUsers) {
        this.deletedUsers = deletedUsers;
    }

    // 8-3 Метод добавления нового пользователя в группу пользователей, удаленных создателем встречи
    public void addDeletedUsers(User user) {
        if (this.blockedUsers.contains(user)) return; // Если создатель встречи заблочил юзера, выходим
        if (this.deletedUsers.contains(user)) return; // Если создатель встречи уже удалил юзера, выходим

        if (this.memberUsers.contains(user)) { // Если он находится среди участников,
            this.memberUsers.remove(user); // Удаляем его из участников
            this.deleteDuplicate(user.getId()); // Удаляем дубликат встречи данного пользователя
            this.deletedUsers.add(user); // и добавляем пользователя к удаленным
            return;
        }

        if (this.invitedUsers.contains(user)) { // Если он находится среди приглашенных,
            this.deletedUsers.add(user); // добавляем пользователя к удаленным
            return;
        }

        if (this.beggingUsers.contains(user)) { // Если он находится среди подавших заявку на участие,
            this.deletedUsers.add(user); // добавляем пользователя к удаленным
            return;
        }

        // Во всех остальных случаях он не может попасть в группу удаленных, это нарушает траекторию
        if (! this.users.contains(user)) this.users.add(user); // Если этого юзера нет среди общего списка, вешаем его туда

    }
}
