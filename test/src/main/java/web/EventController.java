package web;

import domain.Event;
import domain.UserProfile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import service.EventService;

import javax.servlet.http.HttpSession;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Lawrence on 20.12.2016.
 */

@Controller
@SessionAttributes("event")
public class EventController {

    @Autowired
    EventService eventService;

    private java.sql.Date strToDate(String str) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        java.sql.Date date = null;
        try {
            date = new java.sql.Date(dateFormat.parse(str).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    @RequestMapping(value = "/event", method = RequestMethod.GET)
    public String getRegistrationUserPage() {
        return "/event";
    }

    @RequestMapping(value = "/event", method = RequestMethod.POST)
    public String createEvent(@ModelAttribute("userEvent") Event event,
                                @RequestParam("date_beginStr") String date_begin,
                                @RequestParam("date_endStr") String date_end,
                              HttpSession session
                               )
    {

        UserProfile host = (UserProfile) session.getAttribute("user");

        if (host != null) {
            event.setHost_id(host.getId());
        } else {
            return "/notLogIn";
        }


        java.sql.Date beginDate = strToDate(date_begin);
        java.sql.Date endDate = strToDate(date_end);

        event.setDate_begin(beginDate);
        event.setDate_end(endDate);


        eventService.createEvent(event);

        return "/main-login";
    }

}
