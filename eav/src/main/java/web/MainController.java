package web;

import dbHelp.DBHelp;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

/**
 * Created by Lawrence on 14.01.2017.
 */
@Controller
public class MainController {

    @RequestMapping(value = {"/", "main"})
    public ModelAndView index() {

        return new ModelAndView("main");

    }
}