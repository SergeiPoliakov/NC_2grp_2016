package service;

import dao.UserProfileDAO;
import domain.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class UserProfileService {

    @Autowired
    private UserProfileDAO userProfileDAO;

    public void registerUser(UserProfile user)  {
            userProfileDAO.save(user);

    }


}
