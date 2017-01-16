package web;

import dbHelp.DBHelp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Lawrence on 14.01.2017.
 */
@Controller
public class MainController {


    @RequestMapping(value = {"/", "test"})
    public ModelAndView index() {

            return new ModelAndView("test");

    }

    @Deprecated
    @RequestMapping("/allObject")
    public String listApartments(Map<String, Object> map) throws SQLException {
        map.put("allObject", new DBHelp().getObjectsIDbyObjectTypeID(1001));
        return "allObj";
    }





}