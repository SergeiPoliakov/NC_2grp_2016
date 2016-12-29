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

    public UserProfile findUserWithEmail(String email) {
        TypedQuery<UserProfile> query = em
                .createQuery("SELECT u " +
                                "FROM UserProfile u " +
                                "WHERE u.email = :email",
                        UserProfile.class);

        query.setParameter("email", email);

        UserProfile userProfile;
        try {
            userProfile = query.getSingleResult();
        } catch (NoResultException e) {
            return null;
        }
        return userProfile;
    }

}
