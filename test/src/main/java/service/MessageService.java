package service;

import dao.MessageDAO;
import domain.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Hroniko (Anatoly Bedarev) on 25.12.2016.
 */
@Service
public class MessageService {
    @Autowired
    private MessageDAO messageDAO;

    public void createMessage(Message message){
        messageDAO.save(message);
    }
}
