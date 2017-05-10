package web;

import WebSocket.SocketMessage;
import com.google.gson.Gson;
import entities.*;
import org.springframework.web.bind.annotation.*;
import service.application_settings.SettingsLoader;
import service.id_filters.NotificationFilter;
import service.notifications.NotificationService;
import service.search.FinderLogic;
import service.search.FinderTagRequest;
import service.search.FinderTagResponse;
import service.statistics.StatisticLogger;
import com.google.common.cache.LoadingCache;
import exception.CustomException;
import org.springframework.context.support.GenericXmlApplicationContext;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.servlet.ModelAndView;
import service.*;
import service.cache.DataObjectCache;
import service.converter.Converter;
import service.id_filters.EventFilter;
import service.id_filters.UserFilter;
import service.tags.NameNodeTree;
import service.tags.TagTreeManager;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.Console;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.InvocationTargetException;
import java.security.Principal;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutionException;
import service.notifications.UsersNotifications;

/**
 * Created by Lawrence on 20.01.2017.
 */
@Controller
public class UserController {
    // Собственный внутренний логгер для контроллера
    private StatisticLogger loggerLog = new StatisticLogger();

    private TagTreeManager tagTreeManager = new TagTreeManager();

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();

    private LoadingServiceImp loadingService = new LoadingServiceImp();

    private UserServiceImp userService = new UserServiceImp();

    private Converter converter = new Converter();

    private String code = "";

    private static String host_name = "";

    private static String host_port = "";

    private static String ftp_server = "";


    public UserController() throws IOException {
    }

    private ArrayList<DataObject> getListDataObject(Map<Integer, DataObject> map) {
        ArrayList<DataObject> list = new ArrayList<>();

        for(Map.Entry<Integer, DataObject> e : map.entrySet()) {
            list.add(e.getValue());
        }

        return list;
    }

    private String convertParameters(String value) {
        if (value == null) {
            value = "false";
        } else value = "true";
        return value;
    }


    @RequestMapping(value = {"/", "main"})
    public ModelAndView index() throws IOException, ParseException {

        // Подгружаем настройки
        SettingsLoader settingsLoader = new SettingsLoader();
        host_name = settingsLoader.getSetting("host_name");

        String port = settingsLoader.getSetting("host_port");
        if (!port.equals("80")) host_port = ":" + port;

        ftp_server = settingsLoader.getSetting("ftp_server");

        return new ModelAndView("main");
    }


    @RequestMapping(value = "/main-login", method = RequestMethod.GET)
    public String getUserPage(HttpServletRequest request, HttpServletResponse response, ModelMap m) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException, ExecutionException, CustomException, ParseException {

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
           // ArrayList<String> eventword = new ArrayList<>();
            for (DataObject dataObject : list) {
                Event event = new Event(dataObject);
                events.add(event);
            }
            System.out.println("Размер кэша после добавления " + doCache.size());

            m.addAttribute("allEvents", events);
            //m.addAttribute("eventword", eventword);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // Логирование
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "main-login", idUser); // Посещение страницы

        // 2017-03-23 Просто тест дерева тегов:
        TagTreeManager ttm = new TagTreeManager();
        ttm.test();

        NameNodeTree nnt = new NameNodeTree();

        return "main-login";
    }

    // Spring sec редиректит сюда при авторизации
    @RequestMapping(value = "/username", method = RequestMethod.GET)
    public String currentUserName(Authentication authentication) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // Сохранение авторизованного пользователя в память
        String userName = authentication.getName();
        DataObject currentUser = loadingService.getDataObjectByIdAlternative(userService.getObjID(userName));
        ArrayList<SocketMessage> notifications = new ArrayList<>();

        /* Тут черпает уведомления из БД, только не работает
        ArrayList<Integer> al = loadingService.getListIdFilteredAlternative(new NotificationFilter(NotificationFilter.FOR_CURRENT_USER, NotificationFilter.UNSEEN));
        for(Integer id : al){
            DataObject dataObject = loadingService.getDataObjectByIdAlternative(id);
            Notification notification = new Converter().ToNotification(dataObject);
            notification.setSender( new Converter().ToUser(
                    loadingService.getDataObjectByIdAlternative(
                            notification.getSenderID())));
            notifications.add(notification);
        }
        */
        // Добавление уведомлений в глобальный список
        UsersNotifications usersNotifications = UsersNotifications.getInstance();
        if (usersNotifications.getNotifications(currentUser.getId()) == null)
            usersNotifications.setNotifications(currentUser.getId(), notifications);
        return "redirect:/main-login";
    }

    @RequestMapping(value = { "/login" }, method = RequestMethod.GET)
    public ModelAndView login(@RequestParam(value = "error", required = false) String error) throws SQLException, CustomException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        ModelAndView model = new ModelAndView();
        if (error != null) {
            throw new CustomException("Неправильно введен логин или пароль!");  //Временное решение
        }
        model.setViewName("main");

        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.LOGIN, "login", idUser); // Авторизация

        return model;

    }

    @RequestMapping(value="/logout", method = RequestMethod.GET)
    public String logoutPage (HttpServletRequest request, HttpServletResponse response) throws SQLException {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.LOGOUT, "logout", idUser);  // чуть переставил, иначе NullPointerException
        if (auth != null){
            new SecurityContextLogoutHandler().logout(request, response, auth);

        }

        return "redirect:/main?logout";
    }


    @RequestMapping(value = "/main", method = RequestMethod.GET)
    public String mainPage() throws SQLException {
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "main", idUser); // Посещение страницы           // в консоле вылетает ошибка "violated - parent key not found"
        return "main";
    }


    @RequestMapping(value = "/searchUser", method = RequestMethod.GET)
    public String searchUser(HttpServletRequest request, Map<String, Object> mapObjects) throws CustomException {

        HttpSession session = request.getSession();
        if (session.getAttribute("finder") != null) {
            FinderTagRequest finder = (FinderTagRequest) session.getAttribute("finder");
            System.out.println("finder пришел из сессии!!!" + finder.getText());

            if ("user".equals(finder.getType())) {
                ArrayList<FinderTagResponse> finderTagResponseList = FinderLogic.getWithLogic(finder);
                Set<Integer> usersID = new HashSet<>();
                ArrayList<Integer> userListWithTag;


                assert finderTagResponseList != null;
                for (FinderTagResponse tag : finderTagResponseList
                        ) {

                    String value = tag.getText();
                    userListWithTag = tagTreeManager.getUserListWithTag(value);
                    usersID.addAll(userListWithTag);
                }

                for (int index : usersID
                        ) {
                    System.out.println("ЭТИ ОБЪЕКТЫ СЕЙЧАС БУДУТ ВЫВЕДЕНЫ!!!" + index);
                }

                try {
                    Map<Integer, DataObject> map = doCache.getAll(usersID);
                    ArrayList<DataObject> list = getListDataObject(map);
                    ArrayList<User> users = new ArrayList<>(list.size());
                    for (DataObject dataObject : list) {
                        User user = converter.ToUser(dataObject);
                        users.add(user);
                    }

                    for (User user : users
                            ) {
                        System.out.println("В ТЕГЕ НАХОДИТСЯ ЮЗЕР С ID " + user.getId());
                    }

                    mapObjects.put("allUsers", users);

                    session.removeAttribute("finder");

                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                throw new CustomException("Неизвестная ошибка!");
            }
        }
        return "/searchUser";
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
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.SEARCH_USER, name, idUser); // Поиск юзера
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

        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "allUser", idUser); // Посещение страницы
        return "allUser";
    }


    // 2017-03-05 Вывод всех неподтвержденных друзей
    @RequestMapping("/allUnconfirmedFriends")
    public String listUnconfirmedFriends(Map<String, Object> mapObjects) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        // String current_user_id = userService.getCurrentUser().getId().toString(); // айди текущего юзера
        // Вытаскиваем айди всех неподтвержденных текущим пользователем друзей:
        ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new UserFilter(UserFilter.ALL_FRIENDS_UNCONFIRMED_FRIENDSHIP));

        try {
            System.out.println("Ищем в кэше список пользователей, подавших заявку в друзья");
            Map<Integer, DataObject> map = doCache.getAll(il);
            ArrayList<DataObject> list = getListDataObject(map);
            ArrayList<User> users = new ArrayList<>(list.size());
            for (DataObject dataObject : list) {
                User user = converter.ToUser(dataObject);
                users.add(user);
            }
            System.out.println("Размер кэша после добавления " + doCache.size());

            mapObjects.put("allUnconfirmedFriends", users);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "allUnconfirmedFriends", idUser); // Посещение страницы
        return "allUnconfirmedFriends";
    }


    @RequestMapping(value = "/addUser", method = RequestMethod.GET)
    public String getRegistrationUserPage() throws SQLException {
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "addUser", idUser); // Посещение страницы
        return "addUser";
    }


    @RequestMapping(value = "/addUser", method = RequestMethod.POST)
    public String registerUser(@RequestParam("name") String name,
                               @RequestParam("surname") String surname,
                               @RequestParam("middle_name") String middle_name,
                               @RequestParam("nickname") String nickname,
                               @RequestParam("ageUser") String ageDate,
                               @RequestParam("email") String email,
                               @RequestParam("phone") String phone,
                               @RequestParam("password") String password
    ) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, CustomException, MessagingException, UnsupportedEncodingException, ParseException {

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
        mapAttr.put(8, "не выбран");
        mapAttr.put(9, "");
        mapAttr.put(10, "");
        mapAttr.put(11, "/resources/img/avatar.png"); // mapAttr.put(11, "ftp://" + this.ftp_server +"/upload/default/avatar.png"); // mapAttr.put(11, "http://nc2.hop.ru/upload/default/avatar.png");
        mapAttr.put(15, "true");  //изначально должно быть false
        mapAttr.put(16, phone);
        mapAttr.put(17, "true");  //изначально должно быть false
        // mapAttr.put(12, null);
        // mapAttr.put(13, null); не нужно, иначе потом пустая ссылка на событие висит, и при добавлении новой задачи она так и остается висеть. Иначе надо будет при добавлении эту обновлять


        DataObject dataObject = loadingService.createDataObject(full_name, 1001, mapAttr);

        Settings settingsUser = new Settings(userService.generationID(1006), dataObject.getId());

        dataObject.setValue(19, String.valueOf(settingsUser.getId()));

        if (userService.getEmail(dataObject.getParams().get(6)).isEmpty()) {
            loadingService.setDataObjectToDB(dataObject);
            DataObject dataObjectSettings = converter.toDO(settingsUser);
            dataObjectSettings.setRefParams(19, settingsUser.getUser_id());
            loadingService.setDataObjectToDB(dataObjectSettings);
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

                String url = "http://"+ this.host_name + this.host_port +"/" + dataObject.getId() + "/varification_token/" + userService.generateEmailToken(20); // String url = "http://localhost:8081/" + dataObject.getId() + "/varification_token/" + userService.generateEmailToken(20);

                helper.setText("Добро пожаловать! \n"+
                        "Перейдите по ссылке, чтобы завершить регистрацию и получить полный доступ к приложению \n"+
                        "<html><body><a href="+url+">"+"Завершение регистрации"+"</a></body></html> \n"+
                        "Это сообщение создано автоматически, на него не нужно отвечать!", true) ;

                userService.sendEmail(message);

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
            DataObject dataObjectSettings = doCache.get(user.getSettingsID());
            Settings settings = converter.ToSettings(dataObjectSettings);
            m.addAttribute(user);
            m.addAttribute("settings", settings);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "profile", idUser); // Посещение страницы
        return "/profile";
    }

    @RequestMapping(value = "/generatePhoneCode", method = RequestMethod.GET)
    public String generatePhoneCode() throws SQLException {
        String code = userService.generatePhoneToken();
        this.code = code;
        System.out.println("Сгенированыый код " + code);

        //SMSCSender sd= new SMSCSender("Netcracker", "q7Sq2O_VqLhh", "utf-8", true);   //после теста закомментируйте обратно!!!!!
        //sd.sendSms("7**********", "Код подтверждения: " + code, 0, "", "", 0, "NC", "");  // тут нужно указать ваш номер телефона
        //sd.getBalance();
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.EDIT_SETTINGS, "generatePhoneCode", idUser); // Изменение настроек
        return "redirect:/profile";
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
            return "redirect:/profile";
        } else System.out.println("Неверный код подтверждения!");

        return "redirect:/profile";
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
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.EDIT_SETTINGS, "changeProfile", idUser); // Изменение настроек
        return "redirect:/main-login";
    }

    @RequestMapping("/allFriends")
    public String friendList(Map<String, Object> mapObjects) throws SQLException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        int current_user_id = userService.getObjID(userService.getCurrentUsername());
        ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new UserFilter(UserFilter.ALL_FRIENDS_FOR_USER_WITH_ID, String.valueOf(current_user_id)));
        try {
            System.out.println("Ищем в кэше список друзей");
            Map<Integer, DataObject> map = doCache.getAll(il);
            ArrayList<DataObject> list = getListDataObject(map);
            ArrayList<User> friends = new ArrayList<>(list.size());
            for (DataObject dataObject : list) {
                User user = converter.ToUser(dataObject);
                friends.add(user);
                System.out.println("Список друзей: " + user.getLogin());
            }
            System.out.println("Размер кэша после добавления " + doCache.size());

            mapObjects.put("allObject", friends);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "allFriends", idUser); // Посещение страницы
        return "allFriends";
    }

    // Добавление пользователя в друзья (по его ID)
    @RequestMapping("/addFriend/{objectId}/{type}")
    public String addFriend(@PathVariable("objectId") Integer objectId,
                            @PathVariable("type") String type,
                            ModelMap m) throws InvocationTargetException,
            NoSuchMethodException, SQLException, IllegalAccessException, ExecutionException, IOException, MessagingException, CustomException, ParseException {

        DataObject dataObjectTo = doCache.get(objectId);
        User user = converter.ToUser(dataObjectTo);
        Settings settings = converter.ToSettings(doCache.get(user.getSettingsID()));

        if ("nobody".equals((settings.getPrivateAddFriend())) && !"acceptFriend".equals(type)) {
            throw new CustomException("Пользователь ограничил список пользователей, которые могут добавлять его в друзья");
        } else {
            userService.setFriend(objectId);
        }

        if ("addFriend".equals(type)) {
            DataObject dataObjectFrom = doCache.get(userService.getObjID(userService.getCurrentUsername()));

            if ("true".equals(settings.getEmailNewFriend())) {
                userService.fittingEmail("addFriend", dataObjectFrom.getId(), objectId);
            }
            if ("true".equals(settings.getPhoneNewFriend())) {
                // userService.sendSmS("addFriend" ,dataObject.getId(), objectId);  //отправка смс
            }
        }
        int idUser = userService.getObjID(userService.getCurrentUsername());
        
        loggerLog.add(Log.ADD_FRIEND, objectId, idUser); // Добавление пользователя в друзья
        return "/addFriend";
    }

    // Удаление пользователя из друзей (по его ID)
    @RequestMapping("/deleteFriend/{objectId}")
    public String deleteFriend(@PathVariable("objectId") Integer objectId) throws InvocationTargetException, NoSuchMethodException, SQLException, IllegalAccessException {
        userService.deleteFriend(objectId);
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.DEL_FRIEND, objectId, idUser); // Удаления пользователя из друзей
        return "/deleteFriend";
    }


    @RequestMapping(value = "/user{id}")
    public String viewUser(@PathVariable("id") int userId,
                           ModelMap m) throws InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException, CustomException {
        DataObject dataObject = doCache.get(userId);
        User user = converter.ToUser(dataObject);
        Settings settings = converter.ToSettings(doCache.get(user.getSettingsID()));

        String flagProfile = "false";
        String flagMessage = "false";

        // проверяем, есть ли текущий пользователь в друзьях, чтобы дать ему доступ
        int current_user_id = userService.getObjID(userService.getCurrentUsername());
        ArrayList<Integer> ilFriend = loadingService.getListIdFilteredAlternative(new UserFilter(UserFilter.ALL_FRIENDS_FOR_USER_WITH_ID, String.valueOf(userId)));
        try {
            Map<Integer, DataObject> map = doCache.getAll(ilFriend);
            ArrayList<DataObject> list = getListDataObject(map);
            for (DataObject dataObjectFriend : list) {
                User userFriend = converter.ToUser(dataObjectFriend);
                if (current_user_id == userFriend.getId()) {
                    flagMessage = "true";
                    flagProfile = "true";
                }
            }
            m.addAttribute(user);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        // вариант с ошибкой
        if ("onlyFriend".equals(settings.getPrivateProfile()) && flagProfile.equals("true")) {
            m.addAttribute("flagProfile", flagProfile);
        } else  if ("onlyFriend".equals(settings.getPrivateProfile()) && flagProfile.equals("false") || "nobody".equals((settings.getPrivateProfile()))) {
            throw new CustomException("Пользователь ограничил доступ к странице");
        } else {
            flagProfile = "true";
            m.addAttribute("flagProfile", flagProfile);
        }

        // вариант с блокировкой кнопки
        if ("onlyFriend".equals(settings.getPrivateMessage()) && flagMessage.equals("true")){
            m.addAttribute("flagMessage", flagMessage);
        } else if ("onlyFriend".equals(settings.getPrivateMessage()) && flagMessage.equals("false") || "nobody".equals((settings.getPrivateMessage()))) {
            flagMessage = "false";
            m.addAttribute("flagMessage", flagMessage);
        } else {
            flagMessage = "true";
            m.addAttribute("flagMessage", flagMessage);
        }

        try {
            ArrayList<Integer> il = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_USER_WITH_ID, String.valueOf(userId)));
            Map<Integer, DataObject> map = doCache.getAll(il);
            ArrayList<DataObject> list = getListDataObject(map);
            System.out.println("Размер кэша после добавления " + doCache.size());
            ArrayList<Event> events = new ArrayList<>(list.size());
            for (DataObject dataObjectEvent : list
                    ) {
                Event event = new Event(dataObjectEvent);
                events.add(event);
            }
            m.addAttribute("allObject", events);

        } catch (ExecutionException e) {
            e.printStackTrace();
        }
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.VIEW_PROFILE, userId, idUser); // Просмотр пользователя
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
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "meeting", idUser); // Посещение страницы
        return "meeting";
    }

    @RequestMapping(value = "/updateSettings/{settingsID}", method = RequestMethod.POST)
    public String updateSettings(HttpServletRequest request,
                                 HttpServletResponse res,
                                 @PathVariable("settingsID") Integer settingsID) throws SQLException, NoSuchMethodException,
            IllegalAccessException, ParseException, InvocationTargetException, ExecutionException {


        String emailNewMessage = request.getParameter("emailNewMessage");
        emailNewMessage = convertParameters(emailNewMessage);
        String emailNewFriend = request.getParameter("emailNewFriend");
        emailNewFriend = convertParameters(emailNewFriend);
        String emailMeetingInvite = request.getParameter("emailMeetingInvite");
        emailMeetingInvite = convertParameters(emailMeetingInvite);
        String phoneNewMessage = request.getParameter("phoneNewMessage");
        phoneNewMessage = convertParameters(phoneNewMessage);
        String phoneNewFriend = request.getParameter("phoneNewFriend");
        phoneNewFriend = convertParameters(phoneNewFriend);
        String phoneMeetingInvite = request.getParameter("phoneMeetingInvite");
        phoneMeetingInvite = convertParameters(phoneMeetingInvite);
        String privateProfile = request.getParameter("privateProfile");
        String privateMessage = request.getParameter("privateMessage");
        String privateAddFriend = request.getParameter("privateAddFriend");
        String privateLookFriend = request.getParameter("privateLookFriend");
        String privateMeetingInvite = request.getParameter("privateMeetingInvite");

        Settings settings = new Settings(settingsID, userService.getObjID(userService.getCurrentUsername()),
                emailNewMessage, emailNewFriend, emailMeetingInvite, phoneNewMessage, phoneNewFriend, phoneMeetingInvite,
                privateProfile, privateMessage, privateAddFriend, privateLookFriend, privateMeetingInvite);

        DataObject dataObject = converter.toDO(settings);
        loadingService.updateDataObject(dataObject);
        doCache.invalidate(dataObject.getId());
        int idUser = userService.getObjID(userService.getCurrentUsername());
        loggerLog.add(Log.PAGE, "profile", idUser); // Посещение страницы
        return "redirect:/profile";
    }

    //Сброс пароля
    @RequestMapping(value="/resetPassword", method=RequestMethod.POST)
    public String resetPassword(@RequestParam("email") String email) throws SQLException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException, ExecutionException, MessagingException, IOException, ParseException {

        if (!userService.getEmail(email).isEmpty()) {
            int userID = userService.getObjID(email);
            User user = converter.ToUser(doCache.get(userID));
            String password = userService.generateEmailToken(8);
            String passBcrypt = new BCryptPasswordEncoder().encode(password);
            user.setPassword(passBcrypt);
            loadingService.updateDataObject(converter.toDO(user));
            doCache.invalidate(converter.toDO(user));
            try (GenericXmlApplicationContext context = new GenericXmlApplicationContext()) {
                context.load("classpath:applicationContext.xml");
                context.refresh();
                JavaMailSender mailSender = context.getBean("mailSender", JavaMailSender.class);


                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper  =  new  MimeMessageHelper(message,  true);

                message.setSubject("Сброс пароля", "UTF-8");

                //TODO: Сюда напишите e-mail получателя.
                helper.setTo(email);
                helper.setFrom(new InternetAddress("netcracker.thesecondgroup@gmail.com", "NC", "UTF-8"));

                String url = "http://"+ this.host_name + this.host_port +"/profile";

                helper.setText("Уважаемый пользователь! \n"+
                        "Уведомляем вас, что ваш пароль был сброшен. Ваш новый пароль:  \n"+
                        password + " Пароль всегда можно поменять в настройках вашего профиля. " +
                        "<html><body><a href="+url+">"+"Профиль"+"</a></body></html> \n", true) ;

                userService.sendEmail(message);

            }
        }

        return "/main";
    }

    @RequestMapping(value="/changePassword", method=RequestMethod.POST)
    public String changePassword(@RequestParam("password1") String password) throws SQLException, ExecutionException,
            NoSuchMethodException, IllegalAccessException, ParseException, InvocationTargetException {

        User user = converter.ToUser(doCache.get(userService.getObjID(userService.getCurrentUsername())));
        String passBcrypt = new BCryptPasswordEncoder().encode(password);
        user.setPassword(passBcrypt);
        loadingService.updateDataObject(converter.toDO(user));
        doCache.invalidate(converter.toDO(user));

        return "redirect:/profile";
    }

    @RequestMapping(value="/sendUsMessage", method=RequestMethod.POST)
    public String sendUsMessage(@RequestParam("nameUser") String nameUser,
                                 @RequestParam("email") String email,
                                 @RequestParam("text") String text) throws SQLException, ExecutionException,
            NoSuchMethodException, IllegalAccessException, ParseException, InvocationTargetException, MessagingException, UnsupportedEncodingException {

        try (GenericXmlApplicationContext context = new GenericXmlApplicationContext()) {
            context.load("classpath:applicationContext.xml");
            context.refresh();
            JavaMailSender mailSender = context.getBean("mailSender", JavaMailSender.class);


            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper  =  new  MimeMessageHelper(message,  true);

            message.setSubject("Для NC. 2 группа", "UTF-8");

            //TODO: Сюда напишите e-mail получателя.
            helper.setTo("netcracker.thesecondgroup@gmail.com");
            helper.setFrom(new InternetAddress(email, nameUser, "UTF-8"));

            helper.setText(text + ". С уважением " + "<html><body><a href=\"mailto:" + email +"?subject=Спасибо за ваше письмо\">\n" +
                    "  Ответить пользователю</a></body></html>", true) ;

            userService.sendEmail(message);

        }
        return "redirect:/main-login";
    }
}
