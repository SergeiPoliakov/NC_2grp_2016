package service;

import dao.EventDAO;
import domain.Event;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * Created by Lawrence on 19.12.2016.
 */

@Service
public class EventService {

    @Autowired
    private EventDAO eventDAO;

    public void createEvent(Event event)  {

        eventDAO.save(event);

    }

}
