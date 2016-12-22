package dao;

import domain.Event;
import org.springframework.stereotype.Repository;

/**
 * Created by Lawrence on 19.12.2016.
 */

@Repository
public class EventDAO extends AbstractDAOImpl<Event> {

    public  EventDAO() {
        super( Event.class);
    }

}
