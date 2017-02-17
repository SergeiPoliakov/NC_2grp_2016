package entities;

import java.util.ArrayList;

/**
 * Created by Lawrence on 29.01.2017.
 */

public class User {

    private Integer id;

    private String name;

    private String surname;

    private String middleName;

    private String login;

    private String ageDate;

    private String email;

    private String password;

    private String city;

    private String sex;

    private String additional_field;

    private ArrayList<Event> eventsUser;

    private ArrayList<User> friends;

    private ArrayList<User> message;

    public ArrayList<User> getFriends() {
        return friends;
    }

    public void setFriends(ArrayList<User> friends) {
        this.friends = friends;
    }

    public ArrayList<User> getMessage() {
        return message;
    }

    public void setMessage(ArrayList<User> message) {
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

    public String getAdditional_field() {
        return additional_field;
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

    public User() {}


}