package service;

import dao.UserProfileDAO;
import domain.UserProfile;
import exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileDAO userProfileDAO;

    public void registerUser(UserProfile user) throws CustomException {
        if (getUserByEmail(user.getEmail()) == null) {
            userProfileDAO.save(user);
        } else {
            throw new CustomException("Пользователь с таким email'ом уже существует");
        }
    }

    public UserProfile loginUser(String nickname, String password) throws CustomException {
        List<UserProfile> users = userProfileDAO.readAll();
        for (UserProfile user : users) {
            if (user.getNickname().equals(nickname) && user.getPassword().equals(password))
                return user;
        }

        throw new CustomException("Указано неверное имя пользователя или пароль");
    }


    public UserProfile getUserByEmail(String email) {
        return userProfileDAO.findUserWithEmail(email);
    }

    public UserProfile updateUserProfile(UserProfile userProfile) {
        return userProfileDAO.update(userProfile);
    }

}
