package dao;

import domain.Message;
import org.springframework.stereotype.Repository;

/**
 * Created by Hroniko (Anatoly Bedarev) on 25.12.2016.
 */
@Repository
public class MessageDAO extends AbstractDAOImpl<Message> {
    public MessageDAO() {
        super(Message.class);
    }
}
