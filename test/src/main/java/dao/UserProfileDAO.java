package dao;

import domain.UserProfile;
import org.springframework.stereotype.Repository;

import javax.persistence.NoResultException;
import javax.persistence.TypedQuery;

@Repository
public  class UserProfileDAO extends AbstractDAOImpl<UserProfile> {

    public UserProfileDAO() {
        super(UserProfile.class);
    }

}
