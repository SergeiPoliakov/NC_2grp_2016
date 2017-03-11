package entities;

import dbHelp.DBHelp;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Lawrence on 29.01.2017.
 */

public class User extends BaseEntitie{

    private Integer id;

    private String name; // 1

    private String surname; // 2

    private String middleName; // 3

    private String login; // 4

    private String ageDate; // 5

    private String email;  // 6

    private String password;  // 7

    private String sex; // 8

    private String city; // 9

    private String additional_field; // 10

    private String picture; // 11

    private ArrayList<User> friends; // 12

    private ArrayList<Event> eventsUser; // 14

    private String phone; // 16

    private CalendarCredential calendarIdentificator; // 18 // идетификационнный файл для календаря

    private ArrayList<Message> message; // 30

    private Integer settingsUD; //19

    public CalendarCredential getCalendarIdentificator() {
        return calendarIdentificator;
    }

    public void setCalendarIdentificator(CalendarCredential calendarIdentificator) {
        this.calendarIdentificator = calendarIdentificator;
    }

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    public ArrayList<Message> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<Message> message) {
        this.message = message;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public String getAgeDate() {
        return ageDate;
    }

    public void setAgeDate(String ageDate) {
        this.ageDate = ageDate;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAdditional_field() {
        return additional_field;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public void setAdditional_field(String additional_field) {
        this.additional_field = additional_field;
    }

    public ArrayList<Event> getEventsUser() {
        return eventsUser;
    }

    public void setEventsUser(ArrayList<Event> eventsUser) {
        this.eventsUser = eventsUser;
    }

    public Integer getSettingsUD() {
        return settingsUD;
    }

    public void setSettingsUD(Integer settingsUD) {
        this.settingsUD = settingsUD;
    }

    // 2017-02-28
    public void addToEventList(ArrayList<Event> newEvents){
        this.eventsUser.addAll(newEvents);
    }
    public void addToFriendList(ArrayList<User> newFriends){
        this.friends.addAll(newFriends);
    }
    public void addToMessageList(ArrayList<Message> newMessages){
        this.message.addAll(newMessages);
    }
    //


    public User() {}

    public User(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.eventsUser = new ArrayList<>();
        this.friends = new ArrayList<>();
        this.message = new ArrayList<>();
        this.id = dataObject.getId();
        // Поле params
        for (Map.Entry<Integer, String> param : dataObject.getParams().entrySet() ) {
            switch (param.getKey()){
                case (1):
                    this.name = param.getValue();
                    break;
                case (2):
                    this.surname = param.getValue();
                    break;
                case (3):
                    this.middleName = param.getValue();
                    break;
                case (4):
                    this.login = param.getValue();
                    break;
                case (5):
                    this.ageDate = param.getValue();
                    break;
                case (6):
                    this.email = param.getValue();
                    break;
                case (7):
                    // password
                    break;
                case (8):
                    this.sex = param.getValue();
                    break;
                case (9):
                    this.city = param.getValue();
                    break;
                case (10):
                    this.additional_field = param.getValue();
                    break;
                case (11):
                    this.picture = param.getValue();
                    break;
                case (14):
                    // events, хотя есть поле Tasks
                    break;
                case (16):
                    this.phone = param.getValue();
                    break;
                case (19):
                    this.settingsUD = Integer.parseInt(param.getValue());
                    break;
            }
        }
        // Поле ссылок
        for (Map.Entry<Integer, ArrayList<Integer>> reference : dataObject.getRefParams().entrySet() ) {
            switch (reference.getKey()){
                case (12): // friends
                    for (Integer refValue: reference.getValue()) {
                        // говнокод, работает
                        this.friends.add(this.setFriend(new DBHelp().getObjectsByIdAlternative(refValue)));
                    }
                    break;
                case (13): // tasks
                    for (Integer refValue: reference.getValue()) {
                        Event event = new Event(new DBHelp().getObjectsByIdAlternative(refValue));
                        this.eventsUser.add(event);
                    }
                    break;
            }
        }
    }

    private User setFriend(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        User user = new User();
        user.eventsUser = new ArrayList<>();
        user.friends = new ArrayList<>();
        user.message = new ArrayList<>();
        user.id = dataObject.getId();
        // Поле params
        for (Map.Entry<Integer, String> param : dataObject.getParams().entrySet() ) {
            switch (param.getKey()){
                case (1):
                    user.name = param.getValue();
                    break;
                case (2):
                    user.surname = param.getValue();
                    break;
                case (3):
                    user.middleName = param.getValue();
                    break;
                case (4):
                    user.login = param.getValue();
                    break;
                case (5):
                    user.ageDate = param.getValue();
                    break;
                case (6):
                    user.email = param.getValue();
                    break;
                case (7):
                    // password
                    break;
                case (8):
                    user.sex = param.getValue();
                    break;
                case (9):
                    user.city = param.getValue();
                    break;
                case (10):
                    user.additional_field = param.getValue();
                    break;
                case (11):
                    user.picture = param.getValue();
                    break;
                case (14):
                    // events, хотя есть поле Tasks
                    break;
                case (16):
                    this.phone = param.getValue();
                    break;
            }
        }
        // Поле ссылок
        for (Map.Entry<Integer, ArrayList<Integer>> reference : dataObject.getRefParams().entrySet() ) {
            switch (reference.getKey()){
                case (12): // friends
                    for (Integer refValue: reference.getValue()) {
                        // друзей друзей получать не надо
                    }
                    break;
                case (13): // tasks
                    for (Integer refValue: reference.getValue()) {
                        Event event = new Event(new DBHelp().getObjectsByIdAlternative(refValue));
                        user.eventsUser.add(event);
                    }
                    break;
            }
        }
        return user;
    }

    @Override
    public boolean equals(Object anObject) {
        if (!(anObject instanceof User)) {
            return false;
        }
        User otherUser = (User)anObject;
        return otherUser.getId().equals(this.getId());
    }

}