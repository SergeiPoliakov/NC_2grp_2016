package web;


import com.google.common.cache.LoadingCache;
import dbHelp.DBHelp;
import entities.DataObject;
import entities.Event;
import entities.User;
import exception.CustomException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import service.*;
import service.cache.DataObjectCache;
import service.converter.Converter;
import service.id_filters.EventFilter;
import service.id_filters.UserFilter;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Lawrence on 20.01.2017.
 */
@Controller
public class UserController {


    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();

    private LoadingServiceImp loadingService = new LoadingServiceImp();

    private UserServiceImp userService = UserServiceImp.getInstance();

    private Converter converter = new Converter();

    private String code = "";

    private ArrayList<DataObject> getListDataObject(Map<Integer, DataObject> map) {
        ArrayList<DataObject> list = new ArrayList<>();

        for(Map.Entry<Integer, DataObject> e : map.entrySet()) {
            list.add(e.getValue());
        }

        return list;
    }


    @RequestMapping(value = {"/", "main"})
    public ModelAndView index() {

        return new ModelAndView("main");
    }


    @RequestMapping(value = "/main-login", method = RequestMethod.GET)
    public String getUserPage(HttpServletRequest request, HttpServletResponse response, ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ExecutionException, CustomException {

        DataObject currentUser = loadingService.getDataObjectByIdAlternative(userService.getObjID(userService.getCurrentUsername()));
        if (currentUser.getValue(15).equals("false")) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null){
                new SecurityContextLogoutHandler().logout(request, response, auth);
            }
            throw new CustomException("Вы еще не подтвердили свой email!");

        }

        System.out.println("Размер кэша до обновления страницы " + doCache.size());
        try {
            DataObject dataObject = doCache.get(userService.getObjID(userService.getCurrentUsername()));
            System.out.println("Размер кэша после добавления " + doCache.size());
            User user = converter.ToUser(dataObject);
            m.addAttribute(user);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
        ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_CURRENT_USER));
            System.out.println("Ищем в кэше список событий данной пользователя ");
            Map<Integer, DataObject> map = doCache.getAll(il);
            ArrayList<DataObject> list = getListDataObject(map);
            ArrayList<Event> events = new ArrayList<>(list.size());
            ArrayList<String> eventword = new ArrayList<>();
            for (DataObject dataObject : list) {
                Event event = new Event(dataObject);
                events.add(event);

                // Проба подготовить данные для Tree Word (чтобы потом вывести на jsp дерево событий)
                String eventstring = "+ ";
                String begin = event.getDate_begin();
                SimpleDateFormat df1 = new SimpleDateFormat();
                df1.applyPattern("dd.MM.yyyy HH:mm");
                Date Date_begin = df1.parse(begin);
                SimpleDateFormat df2 = new SimpleDateFormat("yyyy MM.dd HH:mm");
                begin = df2.format(Date_begin);
                String end = event.getDate_end();
                Date Date_end = df1.parse(end);
                end = df2.format(Date_end);
                //eventstring += begin + " " + end + " " + event.getName().replaceAll(" ", "_");
                eventstring += begin + " " + end + " " + "[ccылка]"; // Потом можно и ссылки прикрутить
                //eventstring = eventstring.replaceAll(".", " ");
                eventstring = "[\'" + eventstring + "\'],";
                System.out.println(eventstring);
                eventword.add(eventstring);


            }
            System.out.println("Размер кэша после добавления " + doCache.size());

            m.addAttribute("allEvents", events);
            m.addAttribute("eventword", eventword);

        } catch (ExecutionException | ParseException e) {
            e.printStackTrace();
        }

        return "main-login";
    }



    @RequestMapping(value = { "/login" }, method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            model.addObject("error", "Invalid username or password!");
        }
        model.setViewName("main");

        return model;

    }


    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
        return "redirect:/main?logout";
    }


    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String mainPage() {
        return "main";
    }

    @RequestMapping(value = "/searchUser", method = RequestMethod.POST)
    public String searchUser(@RequestParam("name") String name, Map<String, Object> mapObjects) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new UserFilter(UserFilter.SEARCH_USER, name));

        try {
            System.out.println("Ищем в кэше список пользователей, подходящих под запрос");
            Map<Integer, DataObject> map = doCache.getAll(il);
            ArrayList<DataObject> list = getListDataObject(map);
            ArrayList<User> users = new ArrayList<>(list.size());
            for (DataObject dataObject : list) {
                User user = converter.ToUser(dataObject);
                users.add(user);
            }
            System.out.println("Размер кэша после добавления " + doCache.size());

            mapObjects.put("allUsers", users);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "/searchUser";
    }

/*
    // 2017-02-14 Анатолий, Проба работы фильтров и альтернативного лоадера
    @RequestMapping("/allUser")
    public String listObjects(Map<String, Object> map) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        map.put("allObject", loadingService.getListDataObjectFiltered(Filter.OBJECT_TYPE, Filter.OBJECT_TYPE_USER)); // loadingService.getListDataObjectFiltered(Filter.OBJECT_TYPE, Filter.OBJECT_TYPE_USER));
        // map.put("allObject",userService.getUserList());
        return "allUser";
    }
*/

    // 2017-02-16 Анатолий, Проба работы новых фильтров и альтернативного лоадера
    @RequestMapping("/allUser")
    public String listObjects(Map<String, Object> mapObjects) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new UserFilter(UserFilter.ALL));

        try {
            System.out.println("Ищем в кэше список пользователей");
            Map<Integer, DataObject> map = doCache.getAll(il);
            ArrayList<DataObject> list = getListDataObject(map);
            ArrayList<User> users = new ArrayList<>(list.size());
            for (DataObject dataObject : list) {
                User user = converter.ToUser(dataObject);
                users.add(user);
            }
            System.out.println("Размер кэша после добавления " + doCache.size());

            mapObjects.put("allUsers", users);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return "allUser";
    }

    @RequestMapping(value = "/addUser", method = RequestMethod.GET)
    public String getRegistrationUserPage() { return "addUser"; }


    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("surname") String surname,
                               @RequestParam("middle_name") String middle_name,
                               @RequestParam("nickname") String nickname,
                               @RequestParam("ageUser") String ageDate,
                               @RequestParam("email") String email,
                               @RequestParam("phone") String phone,
                               @RequestParam("password") String password
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, CustomException, MessagingException, UnsupportedEncodingException {

        String full_name = name + " " + surname + " " + middle_name;

        String bcryptPass = new BCryptPasswordEncoder().encode(password);

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();
        mapAttr.put(1, name);
        mapAttr.put(2, surname);
        mapAttr.put(3, middle_name);
        mapAttr.put(4, nickname);
        mapAttr.put(5, ageDate);
        mapAttr.put(6, email);
        mapAttr.put(7, bcryptPass);
        mapAttr.put(8, null);
        mapAttr.put(9, null);
        mapAttr.put(10, null);
        mapAttr.put(11, "http://nc2.hop.ru/upload/default/avatar.png");
        mapAttr.put(15, "true");  //изначально должно быть false
        mapAttr.put(16, phone);
        mapAttr.put(17, "true");  //изначально должно быть false
        // mapAttr.put(12, null);
        // mapAttr.put(13, null); не нужно, иначе потом пустая ссылка на событие висит, и при добавлении новой задачи она так и остается висеть. Иначе надо будет при добавлении эту обновлять




        DataObject dataObject = loadingService.createDataObject(full_name, 1001, mapAttr);

        if (userService.getEmail(dataObject.getParams().get(6)).isEmpty()) {
            loadingService.setDataObjectToDB(dataObject);
            try (GenericXmlApplicationContext context = new GenericXmlApplicationContext()) {
                context.load("classpath:applicationContext.xml");
                context.refresh();
                JavaMailSender mailSender = context.getBean("mailSender", JavaMailSender.class);


                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper  =  new  MimeMessageHelper(message,  true);

                message.setSubject(nickname, "UTF-8");

                //TODO: Сюда напишите e-mail получателя.
                helper.setTo(email);
                helper.setFrom(new InternetAddress("netcracker.thesecondgroup@gmail.com", "NC", "UTF-8"));

                String url = "http://localhost:8081/" + dataObject.getId() + "/varification_token/" + userService.generateEmailToken(20);

                helper.setText("Добро пожаловать! \n"+
                        "Перейдите по ссылке, чтобы завершить регистрацию и получить полный доступ к приложению \n"+
                        "<html><body><a href="+url+">"+"Завершение регистрации"+"</a></body></html> \n"+
                        "Это сообщение создано автоматически, на него не нужно отвечать!", true) ;

                try {
                    mailSender.send(message);
                    System.out.println("Mail sended");
                } catch (MailException mailException) {
                    System.out.println("Mail send failed.");
                    mailException.printStackTrace();
                }
            }
            doCache.invalidate(dataObject.getId());
            System.out.println("Размер кэша после добавления " + doCache.size());
        } else {
            throw new CustomException("Пользователь с таким email'ом уже существует");
        }


        return "/main";
    }

    //Завершение регистрации пользователя
    @RequestMapping(value="/{id}/varification_token/{token}", method=RequestMethod.GET)
    public String verificationToken(@PathVariable("token") String token,
                                    @PathVariable("id") Integer id) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

            DataObject dataObject = loadingService.getDataObjectByIdAlternative(id);
            String confirmedEmail = "true";
            dataObject.setValue(15, confirmedEmail);
            loadingService.updateDataObject(dataObject);

        return "/main";
    }


    // Выводим данные о пользователе на форму редактирования профиля
    @RequestMapping("/profile")
    public String getProfileUserPage(ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ExecutionException {

        try {
            DataObject dataObject = doCache.get(userService.getObjID(userService.getCurrentUsername()));
            User user = converter.ToUser(dataObject);
            m.addAttribute(user);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "/profile";
    }

    @RequestMapping(value = "/advancedSettings", method = RequestMethod.GET)
    public String getAdvancedSettingsPage(ModelMap m)  {
        try {
            DataObject dataObject = doCache.get(userService.getObjID(userService.getCurrentUsername()));
            User user = converter.ToUser(dataObject);
            m.addAttribute(user);
        } catch (ExecutionException | SQLException e) {
            e.printStackTrace();
        }
        return "advancedSettings";
    }


    @RequestMapping(value = "/generatePhoneCode", method = RequestMethod.GET)
    public String generatePhoneCode() {
        String code = userService.generatePhoneToken();
        this.code = code;
        System.out.println("Сгенированыый код " + code);

         //SMSCSender sd= new SMSCSender("Netcracker", "q7Sq2O_VqLhh", "utf-8", true);   //после теста закомментируйте обратно!!!!!
         //sd.sendSms("7**********", "Код подтверждения: " + code, 0, "", "", 0, "NC", "");  // тут нужно указать ваш номер телефона
         //sd.getBalance();

        return "redirect:/advancedSettings";
    }

    @RequestMapping(value = "/confirmedPhone", method = RequestMethod.POST)
    public String getAdvancedSettingsPage(@RequestParam("codeUser") String codeUser) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        System.out.println("Код " + code);
        System.out.println("Код юзера " + codeUser);

        if (codeUser.equals(code)) {
            DataObject dataObject = loadingService.getDataObjectByIdAlternative(userService.getObjID(userService.getCurrentUsername()));
            String confirmedPhone = "true";
            dataObject.setValue(17, confirmedPhone);
            loadingService.updateDataObject(dataObject);
            return "redirect:/advancedSettings";
        } else System.out.println("Неверный код подтверждения!");

        return "redirect:/advancedSettings";
    }


    @RequestMapping(value = "/changeProfile/{userId}", method = RequestMethod.POST)
    public String changeUser(@PathVariable("userId") Integer userId,
                             @RequestParam("name") String name,
                             @RequestParam("surname") String surname,
                             @RequestParam("middle_name") String middle_name,
                             @RequestParam("ageDate") String ageDate,
                             @RequestParam("sex") String sex,
                             @RequestParam("city") String city,
                             @RequestParam("info") String additional_field) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        String full_name = name + " " + surname + " " + middle_name;

        TreeMap<Integer, Object> mapAttr = new TreeMap<>();
        mapAttr.put(1, name);
        mapAttr.put(2, surname);
        mapAttr.put(3, middle_name);
        //mapAttr.put(4, nickname); убрал возможность пользователя менять свой ник, а то жирно. Будет платной функцией) На самом деле просто из-за добавление поля с телефеном у меня кнопка "Сохранить"
                                    // уехала вниз футера и я не мог на нее нажать
                                    // Вот тут надо сделать прокрутку на странице, а то ее нет. Тогда все будет помещаться
        mapAttr.put(5, ageDate);

        mapAttr.put(8, sex);
        mapAttr.put(9, city);
        mapAttr.put(10, additional_field);

        DataObject dataObject = new DataObject(userId, full_name, 1001, mapAttr);
        loadingService.updateDataObject(dataObject);
        doCache.refresh(userId);
        System.out.println("Обновляем в кэше текущего пользователя");

        return "redirect:/main-login";
    }

    @RequestMapping("/allFriends")
    public String friendList(Map<String, Object> mapObjects) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Integer idUser = userService.getObjID(userService.getCurrentUsername());
        ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new UserFilter(UserFilter.ALL_FRIENDS_FOR_USER_WITH_ID, String.valueOf(idUser)));
        try {
            System.out.println("Ищем в кэше список друзей");
            Map<Integer, DataObject> map = doCache.getAll(il);
            ArrayList<DataObject> list = getListDataObject(map);
            ArrayList<User> friends = new ArrayList<>(list.size());
            for (DataObject dataObject : list) {
                User user = converter.ToUser(dataObject);
                friends.add(user);
            }
            System.out.println("Размер кэша после добавления " + doCache.size());

            mapObjects.put("allObject", friends);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return "allFriends";
    }

    // Добавление пользователя в друзья (по его ID)
    @RequestMapping("/addFriend/{objectId}")
    public String addFriend(@PathVariable("objectId") Integer objectId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        userService.setFriend(objectId);
        return "/addFriend";
    }

    // Удаление пользователя из друзей (по его ID)
    @RequestMapping("/deleteFriend/{objectId}")
    public String deleteFriend(@PathVariable("objectId") Integer objectId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        userService.deleteFriend(objectId);
        return "/deleteFriend";
    }


    @RequestMapping(value = "/viewProfile/{id}")
    public String viewUser(@PathVariable("id") int userId,
                           ModelMap m) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        try {
            DataObject dataObject = doCache.get(userId);
            User user = converter.ToUser(dataObject);
            m.addAttribute(user);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        try {
            ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_USER_WITH_ID, String.valueOf(userId)));
            Map<Integer, DataObject> map = doCache.getAll(il);
            ArrayList<DataObject> list = getListDataObject(map);
            System.out.println("Размер кэша после добавления " + doCache.size());
            ArrayList<Event> events = new ArrayList<>(list.size());
            for (DataObject dataObject: list
                    ) {
                Event event = new Event(dataObject);
                events.add(event);
            }
            m.addAttribute("allObject", events);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        return "/viewProfile";
    }


    @RequestMapping(value = "/accessDenied", method = RequestMethod.GET)
    public ModelAndView accesssDenied(Principal user) {

        ModelAndView model = new ModelAndView();

        if (user != null) {
            model.addObject("errorMsg", user.getName() + ", у вас нет доступа к этой странице!");
        } else {
            model.addObject("errorMsg", "У вас нет доступа к этой странице!");
        }

        model.setViewName("/accessDenied");
        return model;

    }

    @RequestMapping(value = "/meeting", method = RequestMethod.GET)
    public String getMeeting() throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        return "meeting";
    }


}
