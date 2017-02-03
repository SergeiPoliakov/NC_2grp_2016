package entities;

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

    private String country;

    private String sex;

    private String additional_field;

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
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

    public User() {}

    public User(String name, String surname, String middleName, String login, String ageDate, String email,
                String password,  String sex, String country, String additional_field) {
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
        this.ageDate = ageDate;
        this.sex = sex;
        this.country = country;
        this.login = login;
        this.email = email;
        this.password = password;
        this.additional_field = additional_field;
    }

    public User(String name, String surname, String middleName, String login, String ageDate,
                 String email, String password) {
        this.name = name;
        this.surname = surname;
        this.middleName = middleName;
        this.ageDate = ageDate;
        this.login = login;
        this.email = email;
        this.password = password;
    }


}