package validation;

public class Validation {

    public static boolean isValidNickName(String nickname) {
        return nickname.toLowerCase().matches("^[а-яё a-z]+$");
    }

    public static boolean isValidName(String name) {
        return name.toLowerCase().matches("^[а-яё a-z]+$");
    }

    public static boolean isValidCity(String city) {
        return city.toLowerCase().matches("^[а-яё a-z]+$");
    }

    public static boolean isValidEmail(String email) {
        return email.toLowerCase().matches("^[a-z0-9._-]+@[a-z0-9]+\\.[a-z]+$");
    }



}