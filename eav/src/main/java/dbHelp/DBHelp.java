package dbHelp;

import entities.*;
import service.UserServiceImp;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.sql.*;
import java.text.ParseException;
import java.util.*;

import service.calendar.CalendarSettings;
import service.converter.Converter;
import service.id_filters.*;
import service.partition_filters.*;
import service.tags.RootNode;

/**
 * Created by Lawrence on 14.01.2017.
 */

public class DBHelp {

    private final int USER = 1001;
    private final int EVENT = 1002;
    private final int MESSAGE = 1003;
    private final int MEETING = 1004;
    private final int CALENDAR = 1005;
    private final int SETTINGS = 1006;
    private final int NOTIFICATION = 1007;
    private final int LOG = 1008;
    private final int FILE = 1009;
    private final int TAG = 1010;

    private final int START_ID_USER = 10_000;
    private final int START_ID_EVENT = 20_000;
    private final int START_ID_MESSAGE = 30_000;
    private final int START_ID_MEETING = 0;
    private final int START_ID_SETTINGS = 40_000;
    private final int START_ID_CALENDAR = 50_000;
    private final int START_ID_NOTIFICATION = 60_000;
    private final int START_ID_LOG = 70_000;
    private final int START_ID_FILE = 80_000;
    private final int START_ID_TAG = 90_000;


    private UserServiceImp userService = new UserServiceImp();
    private int attrId;

    private static Connection getConnection() throws SQLException {
        Locale.setDefault(Locale.ENGLISH);
        try {
            InitialContext initContext = new InitialContext();
            Context envContext = (Context) initContext.lookup("java:comp/env");
            DataSource ds = (DataSource) envContext.lookup("jdbc/myoracle");
            return ds.getConnection();
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static void CloseConnection(Connection Con) throws SQLException {
        Con.close();
    }

    // 2017-03-02 Поправил генератор
    public int generationID(int objTypeID) throws SQLException {
        int objID = 0;

        try (Connection Con = getConnection(); PreparedStatement PS = Con
                .prepareStatement("SELECT MAX(OBJECT_ID) FROM OBJECTS WHERE OBJECT_TYPE_ID = ?")) {

            PS.setInt(1, objTypeID);
            ResultSet RS = PS.executeQuery();

            while (RS.next()) {
                objID = RS.getInt(1);
                // Если нет ни одной записи в таблице для данного типа датаобджекта:
                if (objID == 0) {
                    if (objTypeID == USER) {
                        objID = START_ID_USER;
                    } else if (objTypeID == EVENT) {
                        objID = START_ID_EVENT;
                    } else if (objTypeID == MESSAGE) {
                        objID = START_ID_MESSAGE;
                    } else if (objTypeID == MEETING) {
                        objID = START_ID_MEETING;
                    } else if (objTypeID == SETTINGS) {
                        objID = START_ID_SETTINGS;
                    } else if (objTypeID == CALENDAR) {
                        objID = START_ID_CALENDAR;
                    } else if (objTypeID == NOTIFICATION) {
                        objID = START_ID_NOTIFICATION;
                    } else if (objTypeID == LOG) {
                        objID = START_ID_LOG;
                    } else if (objTypeID == FILE) {
                        objID = START_ID_FILE; // не обязательно было, но для единообразности
                    } else if (objTypeID == TAG) {
                        objID = START_ID_TAG;
                    } else {
                        System.out.println("Генератор id: Задан неизвестный тип объекта! [" + objTypeID + "]");
                        objID = -1;
                        break;
                    }
                }
                ++objID;
                // Проверка на попадение в интервал выделенных айди:
                if ((objTypeID == USER) & (objID >= START_ID_EVENT) ||
                        (objTypeID == EVENT) & (objID >= START_ID_MESSAGE) ||
                        (objTypeID == MESSAGE) & (objID >= START_ID_SETTINGS) ||
                        (objTypeID == SETTINGS) & (objID >= START_ID_CALENDAR) ||
                        (objTypeID == CALENDAR) & (objID >= START_ID_NOTIFICATION) ||
                        (objTypeID == NOTIFICATION) & (objID >= START_ID_LOG) ||
                        (objTypeID == LOG) & (objID >= START_ID_FILE) ||
                        (objTypeID == FILE) & (objID >= START_ID_TAG) ||
                        (objTypeID == TAG) & (objID >= 100_000) ||
                        (objTypeID == MEETING) & (objID >= START_ID_USER)) {
                    System.out.println("Генератор id: Выход за пределы диапазона выделенных IDs! [id=" + objID + "]");
                    objID = -2;
                }
                break;
            }
            RS.close();
            return objID;
        }
    }


    public int getObjID(String username) throws SQLException {
        int objID = 0;
        try (Connection Con = getConnection();
             PreparedStatement PS = Con
                     .prepareStatement("SELECT OBJECT_ID FROM PARAMS WHERE VALUE = ?");) {
            PS.setString(1, username);
            ResultSet RS = PS.executeQuery();

            while (RS.next()) {
                objID = RS.getInt(1);
            }
            RS.close();
            return objID;
        }

    }


    public ArrayList<Object> getEmail(String email)
            throws SQLException {
        ArrayList<Object> Res = new ArrayList<>();
        try (Connection Con = getConnection();
             PreparedStatement PS = Con
                     .prepareStatement("SELECT p.VALUE " +
                             "FROM PARAMS p " +
                             "WHERE p.ATTR_ID = 6 and p.VALUE = ?");) {
            PS.setString(1, email);
            ResultSet RS = PS.executeQuery();
            while (RS.next()) {
                Res.add(RS.getObject(1));
            }
            RS.close();
            return Res;
        }
    }





    //region Calendar to BD

    // 2017-03-06 // Получение из базы идетификационного файла календаря гугл // !!! Пока что вытаскивает и сохраняет в файл в локальной папке юзера
    public String getCalendarFile(int userId) throws SQLException, IOException, GeneralSecurityException {
        CalendarSettings calendarSettings = new CalendarSettings(userId); // Получаем настройки текущего юзера
        InputStream inputStream = null;
        try (Connection Con = getConnection();
             PreparedStatement PS = Con.prepareStatement("SELECT re.OBJECT_BODY FROM REPOSITORY re " +
                     "WHERE re.OBJECT_ID IN (SELECT MAX(ob.OBJECT_ID) FROM OBJECTS ob " +
                     "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE AND re.ATTR_ID = 18 AND re.OBJECT_ID = ? ) ");) {
            PS.setInt(1, userId);
            ResultSet RS = PS.executeQuery();

            byte[] buffer = new byte[1];
            while (RS.next()) {
                inputStream = RS.getBinaryStream(1);
                if (inputStream != null) {
                    File credential_file = new File(calendarSettings.getDATA_STORE_DIR().getAbsolutePath().toString() + "/StoredCredential");
                    FileOutputStream fileOutputStream = new FileOutputStream(credential_file);
                    while (inputStream.read(buffer) > 0) {
                        fileOutputStream.write(buffer);
                    }
                    fileOutputStream.close();
                }
            }
            RS.close();
            return (inputStream == null) ? null : "OK";
        }
    }


    // 2017-03-06 // Загрузка в репозиторий базы идетификационного файла календаря гугл
    public void setCalendarFile(String nameFile) throws SQLException, IOException, GeneralSecurityException {
        int user_id = new DBHelp().getCurrentUser().getId(); // Получаем пользовательский айдишник (текущего юзера)
        CalendarSettings calendarSettings = new CalendarSettings(user_id); // Получаем настройки текущего юзера

        int file_id = generationID(CALENDAR); // Генерируем новый id для файла в репозитории
        try (Connection connection = getConnection();
             PreparedStatement PS1 = connection.prepareStatement("INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES (?,?,?)");
             PreparedStatement PS2 = connection.prepareStatement("INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES (?,?,?)");
             PreparedStatement PS3 = connection.prepareStatement("INSERT INTO Repository (OBJECT_ID, OBJECT_BODY) VALUES (?,?)");) {


            // 1) Добавляем заголовок датаобджекта в OBJECTS для данного файла:

            //например INSERT INTO Objects (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES ('50001', '1005', 'file.json');
            PS1.setInt(1, file_id);
            PS1.setInt(2, CALENDAR);
            PS1.setString(3, nameFile);
            PS1.executeQuery();


            // 2) Добавляем ссылку от датаобджекта юзера к датаобджекту данного файла:

            //например INSERT INTO References (OBJECT_ID, ATTR_ID, reference) VALUES ('10001', '18', '50001'); -- file google
            PS2.setInt(1, user_id);
            PS2.setInt(2, 18);
            PS2.setInt(3, file_id);
            PS2.executeQuery();


            // 3) Добавляем сам файл в репозиторий базы как BLOB
            connection.setAutoCommit(false); // выключаем автокоммиты

            PS3.setInt(1, file_id);
            File credential_file = new File(calendarSettings.getDATA_STORE_DIR().getAbsolutePath().toString() + "/" + nameFile);
            FileInputStream fileInputStream = new FileInputStream(credential_file);
            PS3.setBinaryStream(2, fileInputStream, (int) credential_file.length());
            fileInputStream.close();
            PS3.execute();
            connection.commit();

            System.out.println("Права доступа сохранены в Repository в базу: " + file_id + ", " + "BLOB[" + nameFile + "]");
        }
    }
    //endregion


    public ArrayList<User> getFriendListCurrentUser() throws SQLException {
        Integer userID = new DBHelp().getObjID(userService.getCurrentUsername());
        return getFriendListByUserId(userID);
    }

    public ArrayList<User> getFriendListByUserId(int userID) throws SQLException {
        ArrayList<User> Res = new ArrayList<>();
        Integer userTypeID = USER; // ID типа Пользователь
        int attrId = 12; // ID атрибута в базе, соответствующий друзьям пользователя
        try (Connection Con = getConnection();
             PreparedStatement PS = Con.
                     prepareStatement("SELECT friend.OBJECT_ID, pa1.VALUE, pa2.VALUE, pa3.VALUE, pa4.VALUE, pa5.VALUE, " +
                             "pa6.VALUE, pa7.VALUE, pa8.VALUE, pa9.VALUE, pa10.VALUE FROM OBJECTS ob " +
                             "LEFT JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID AND ATTR_ID = ? " + // Только ссылки на друзей attrId = 12
                             "LEFT JOIN OBJECTS friend ON re.REFERENCE = friend.OBJECT_ID " +
                             "LEFT JOIN PARAMS pa1 ON friend.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 1 " + // -- name
                             "LEFT JOIN PARAMS pa2 ON friend.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 2 " + // -- suname
                             "LEFT JOIN PARAMS pa3 ON friend.OBJECT_ID = pa3.OBJECT_ID AND pa3.ATTR_ID = 3 " + // -- middleName
                             "LEFT JOIN PARAMS pa4 ON friend.OBJECT_ID = pa4.OBJECT_ID AND pa4.ATTR_ID = 4 " + // -- nickname
                             "LEFT JOIN PARAMS pa5 ON friend.OBJECT_ID = pa5.OBJECT_ID AND pa5.ATTR_ID = 5 " + // -- ageDate
                             "LEFT JOIN PARAMS pa6 ON friend.OBJECT_ID = pa6.OBJECT_ID AND pa6.ATTR_ID = 6 " + // -- email
                             "LEFT JOIN PARAMS pa7 ON friend.OBJECT_ID = pa7.OBJECT_ID AND pa7.ATTR_ID = 7 " + // -- bcryptPass
                             "LEFT JOIN PARAMS pa8 ON friend.OBJECT_ID = pa8.OBJECT_ID AND pa8.ATTR_ID = 8 " + // -- sex
                             "LEFT JOIN PARAMS pa9 ON friend.OBJECT_ID = pa9.OBJECT_ID AND pa9.ATTR_ID = 9 " + // -- country
                             "LEFT JOIN PARAMS pa10 ON friend.OBJECT_ID = pa10.OBJECT_ID AND pa10.ATTR_ID = 10 " + // -- additional_field
                             "LEFT JOIN PARAMS pa11 ON friend.OBJECT_ID = pa11.OBJECT_ID AND pa10.ATTR_ID = 11 " + // -- picture
                             "WHERE ob.OBJECT_TYPE_ID = ? " + // -- Только тип Пользователи userTypeID = 1001
                             "AND ob.OBJECT_ID = ? ORDER BY ob.OBJECT_ID")) { // id юзера, кому ищем друзей)

            PS.setInt(1, attrId); // В качестве параметра id ссылки на друга
            PS.setInt(2, userTypeID); // В качестве параметра id типа Пользователь
            PS.setInt(3, userID); // В качестве параметра id юзера, для которого ищем друзей
            ResultSet RS = PS.executeQuery(); // System.out.println(RS);
            while (RS.next()) {
                User friend = new User();
                friend.setId(RS.getInt(1));
                friend.setName(RS.getString(2));
                friend.setSurname(RS.getString(3));
                friend.setMiddleName(RS.getString(4));
                friend.setLogin(RS.getString(5));
                friend.setAgeDate(RS.getString(6));
                friend.setEmail(RS.getString(7));
                friend.setPassword(RS.getString(8));
                friend.setSex(RS.getString(9));
                friend.setCity(RS.getString(10));
                friend.setAdditional_field(RS.getString(11));
                Res.add(friend);
            }
            RS.close();
            return Res;
        }
    }


    public User getCurrentUser() throws SQLException {
        Integer userID = new DBHelp().getObjID(userService.getCurrentUsername());
        return getUserByUserID(userID);
    }


    public User getUserByUserID(int userID) throws SQLException {
        Integer userTypeID = USER; // ID типа Пользователь
        try (Connection Con = getConnection();
             PreparedStatement PS = Con.
                     prepareStatement("SELECT ob.OBJECT_ID, pa1.VALUE, pa2.VALUE, pa3.VALUE, pa4.VALUE, pa5.VALUE, " +
                             "pa6.VALUE, pa7.VALUE, pa8.VALUE, pa9.VALUE, pa10.VALUE FROM OBJECTS ob " +
                             "LEFT JOIN PARAMS pa1 ON ob.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 1 " +
                             "LEFT JOIN PARAMS pa2 ON ob.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 2 " +
                             "LEFT JOIN PARAMS pa3 ON ob.OBJECT_ID = pa3.OBJECT_ID AND pa3.ATTR_ID = 3 " +
                             "LEFT JOIN PARAMS pa4 ON ob.OBJECT_ID = pa4.OBJECT_ID AND pa4.ATTR_ID = 4 " +
                             "LEFT JOIN PARAMS pa5 ON ob.OBJECT_ID = pa5.OBJECT_ID AND pa5.ATTR_ID = 5 " +
                             "LEFT JOIN PARAMS pa6 ON ob.OBJECT_ID = pa6.OBJECT_ID AND pa6.ATTR_ID = 6 " +
                             "LEFT JOIN PARAMS pa7 ON ob.OBJECT_ID = pa7.OBJECT_ID AND pa7.ATTR_ID = 7 " +
                             "LEFT JOIN PARAMS pa8 ON ob.OBJECT_ID = pa8.OBJECT_ID AND pa8.ATTR_ID = 8 " +
                             "LEFT JOIN PARAMS pa9 ON ob.OBJECT_ID = pa9.OBJECT_ID AND pa9.ATTR_ID = 9 " +
                             "LEFT JOIN PARAMS pa10 ON ob.OBJECT_ID = pa10.OBJECT_ID AND pa10.ATTR_ID = 10 " +
                             "WHERE ob.OBJECT_TYPE_ID = ? AND ob.OBJECT_ID = ? ORDER BY ob.OBJECT_ID");) {


            PS.setInt(1, userTypeID); // В качестве параметра id типа Пользователь
            PS.setInt(2, userID); // В качестве параметра id пользователя
            ResultSet RS = PS.executeQuery(); // System.out.println(RS);
            User user = null;
            while (RS.next()) {
                user = new User();
                user.setId(RS.getInt(1));
                user.setName(RS.getString(2));
                user.setSurname(RS.getString(3));
                user.setMiddleName(RS.getString(4));
                user.setLogin(RS.getString(5));
                user.setAgeDate(RS.getString(6));
                user.setEmail(RS.getString(7));
                user.setPassword(RS.getString(8));
                user.setSex(RS.getString(9));
                user.setCity(RS.getString(10));
                user.setAdditional_field(RS.getString(11));
            }
            RS.close();
            return user;
        }
    }


    public User getUserAndEventByUserID(int userID) throws SQLException {
        Integer userTypeID = USER; // ID типа Пользователь
        try (Connection Con = getConnection();
             PreparedStatement PS = Con.
                     prepareStatement("SELECT ob.OBJECT_ID, pa1.VALUE, pa2.VALUE, pa3.VALUE, pa4.VALUE, pa5.VALUE, " +
                             "pa6.VALUE, pa7.VALUE, pa8.VALUE, pa9.VALUE, pa10.VALUE FROM OBJECTS ob " +
                             "LEFT JOIN PARAMS pa1 ON ob.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 1 " +
                             "LEFT JOIN PARAMS pa2 ON ob.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 2 " +
                             "LEFT JOIN PARAMS pa3 ON ob.OBJECT_ID = pa3.OBJECT_ID AND pa3.ATTR_ID = 3 " +
                             "LEFT JOIN PARAMS pa4 ON ob.OBJECT_ID = pa4.OBJECT_ID AND pa4.ATTR_ID = 4 " +
                             "LEFT JOIN PARAMS pa5 ON ob.OBJECT_ID = pa5.OBJECT_ID AND pa5.ATTR_ID = 5 " +
                             "LEFT JOIN PARAMS pa6 ON ob.OBJECT_ID = pa6.OBJECT_ID AND pa6.ATTR_ID = 6 " +
                             "LEFT JOIN PARAMS pa7 ON ob.OBJECT_ID = pa7.OBJECT_ID AND pa7.ATTR_ID = 7 " +
                             "LEFT JOIN PARAMS pa8 ON ob.OBJECT_ID = pa8.OBJECT_ID AND pa8.ATTR_ID = 8 " +
                             "LEFT JOIN PARAMS pa9 ON ob.OBJECT_ID = pa9.OBJECT_ID AND pa9.ATTR_ID = 9 " +
                             "LEFT JOIN PARAMS pa10 ON ob.OBJECT_ID = pa10.OBJECT_ID AND pa10.ATTR_ID = 10 " +
                             "WHERE ob.OBJECT_TYPE_ID = ? AND ob.OBJECT_ID = ? ORDER BY ob.OBJECT_ID");
             PreparedStatement PS1 = Con.prepareStatement("SELECT ev.OBJECT_ID, ob.OBJECT_ID, ev.OBJECT_NAME," +
                     "pa2.VALUE, pa3.VALUE, pa4.VALUE, pa5.VALUE FROM OBJECTS ob LEFT JOIN REFERENCES re " +
                     "ON ob.OBJECT_ID = re.OBJECT_ID LEFT JOIN OBJECTS ev  ON re.REFERENCE = ev.OBJECT_ID " +
                     "LEFT JOIN PARAMS pa1 ON ev.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 103 LEFT JOIN PARAMS pa2 " +
                     "ON ev.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 101   LEFT JOIN PARAMS pa3 " +
                     "ON ev.OBJECT_ID = pa3.OBJECT_ID AND  pa3.ATTR_ID = 102 LEFT JOIN PARAMS pa4 " +
                     "ON ev.OBJECT_ID = pa4.OBJECT_ID AND pa4.ATTR_ID = 105 LEFT JOIN PARAMS pa5 " +
                     "ON ev.OBJECT_ID = pa5.OBJECT_ID AND pa5.ATTR_ID = 104 WHERE ob.OBJECT_ID = ? AND re.ATTR_ID = 13 ORDER BY ev.OBJECT_ID");) {

            ArrayList<Event> events = new ArrayList<>();


            PS.setInt(1, userTypeID); // В качестве параметра id типа Пользователь
            PS.setInt(2, userID); // В качестве параметра id пользователя
            ResultSet RS = PS.executeQuery(); // System.out.println(RS);
            User user = null;
            while (RS.next()) {
                user = new User();
                user.setId(RS.getInt(1));
                user.setName(RS.getString(2));
                user.setSurname(RS.getString(3));
                user.setMiddleName(RS.getString(4));
                user.setLogin(RS.getString(5));
                user.setAgeDate(RS.getString(6));
                user.setEmail(RS.getString(7));
                user.setPassword(RS.getString(8));
                user.setSex(RS.getString(9));
                user.setCity(RS.getString(10));
                user.setAdditional_field(RS.getString(11));
            }
            RS.close();


            PS1.setInt(1, userID);
            ResultSet RS1 = PS1.executeQuery();
            while (RS1.next()) {
                Event event = new Event();
                event.setId(RS1.getInt(1));
                event.setHost_id(RS1.getInt(2));
                event.setName(RS1.getString(3));
                event.setDate_begin(RS1.getString(4));
                event.setDate_end(RS1.getString(5));
                event.setPriority(RS1.getString(6));
                event.setInfo(RS1.getString(7));

                events.add(event);
            }
            RS1.close();
            try {
                assert user != null;
                user.setEventsUser(events);
            } catch (NullPointerException e) {
                System.out.println("У данного пользователя нет событий или такой пользователь не найден");
            }

            return user;
        }
    }


    //region Meeting


    public ArrayList<Meeting> getUserMeetingsList(int userID) throws SQLException {
        ArrayList<Meeting> Res = new ArrayList<>();
        try (Connection Con = getConnection();
             PreparedStatement PS = Con.prepareStatement("SELECT  ev.OBJECT_ID, " +
                     "        pa1.VALUE as PA1," +
                     "        pa2.VALUE as PA2," +
                     "        pa3.VALUE as PA3," +
                     "        pa4.VALUE as PA4," +
                     "        pa5.VALUE as PA5," +
                     "        pa6.VALUE as PA6 " +
                     "FROM  OBJECTS ob " +
                     "      LEFT JOIN REFERENCES re " +
                     "        ON ob.OBJECT_ID = re.REFERENCE " +
                     "      LEFT JOIN OBJECTS ev " +
                     "        ON re.OBJECT_ID = ev.OBJECT_ID " +
                     "      LEFT JOIN PARAMS pa1 " +
                     "        ON ev.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 301 " +
                     "      LEFT JOIN PARAMS pa2 " +
                     "        ON ev.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 302 " +
                     "      LEFT JOIN PARAMS pa3 " +
                     "        ON ev.OBJECT_ID = pa3.OBJECT_ID AND pa3.ATTR_ID = 303 " +
                     "      LEFT JOIN PARAMS pa4 " +
                     "        ON ev.OBJECT_ID = pa4.OBJECT_ID AND pa4.ATTR_ID = 304 " +
                     "      LEFT JOIN PARAMS pa5 " +
                     "        ON ev.OBJECT_ID = pa5.OBJECT_ID AND pa5.ATTR_ID = 305 " +
                     "      LEFT JOIN PARAMS pa6 " +
                     "        ON ev.OBJECT_ID = pa6.OBJECT_ID AND pa6.ATTR_ID = 306 " +
                     "WHERE ob.OBJECT_ID = ? AND re.ATTR_ID = 307 ORDER BY ev.OBJECT_ID");) {

            // PreparedStatement PS = Con.prepareStatement("SELECT OBJECT_NAME FROM OBJECTS WHERE OBJECT_TYPE_ID = ?");


            PS.setInt(1, userID); // В качестве параметра id пользователя
            ResultSet RS = PS.executeQuery(); // System.out.println(RS);
            while (RS.next()) {
                Meeting meeting = new Meeting();
                meeting.setId(RS.getInt(1));
                meeting.setTitle(RS.getString(2));
                meeting.setDate_start(RS.getString(3));
                meeting.setDate_end(RS.getString(4));
                meeting.setInfo(RS.getString(5));
                meeting.setOrganizer(this.getUserByUserID(RS.getInt(6)));
                meeting.setTag(new StringBuilder(RS.getString(7)));
                Res.add(meeting);
            }
            RS.close();
            return Res;
        }
    }


    // Добавить встречу (id у обьекта Meeting указывать не нужно)
    public void setMeeting(Meeting meeting) throws SQLException {

        try (Connection connection = getConnection();
             Statement st = connection.createStatement();
             ResultSet RS = st.executeQuery("Select max(OBJECT_ID) from OBJECTS WHERE OBJECT_TYPE_ID = " + meeting.objTypeID);
             PreparedStatement PS = connection.prepareStatement("INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES (?,?,?)");
             PreparedStatement PS1 = connection.prepareStatement("INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES (?,?,?)");
             PreparedStatement PS2 = connection.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");) {

            int meetingID = 40000;
            TreeMap<Integer, Object> attributeArray = meeting.getArrayWithAttributes();

            while (RS.next()) {
                meetingID = RS.getInt(1) + 1;
            }

            PS.setInt(1, meetingID);
            PS.setInt(2, meeting.objTypeID);
            PS.setObject(3, "Met" + meetingID);
            PS.executeUpdate();

            // 2) Добавление атрибутов события (параметры со страницы создания события):

            while (!attributeArray.isEmpty()) {
                java.util.Map.Entry<Integer, Object> en = attributeArray.pollFirstEntry();
                PS1.setObject(1, en.getValue());
                PS1.setInt(2, meetingID);
                PS1.setInt(3, en.getKey());
                PS1.addBatch();
            }
            PS1.executeBatch();

            // 3) Добавление ссылки Встреча - Участники (админ в анном случае):
            Integer idUser = meeting.getOrganizer().getId();
            int referenceAttrId = 307; // Параметр-ссылка, в данном случае - список участников встречи

            PS2.setInt(1, meetingID); // ID встречи
            PS2.setInt(2, referenceAttrId); // ID параметра(307)
            PS2.setInt(3, idUser); // ID организатора
            PS2.executeQuery(); //PS2.executeBatch();
        }
    }


    public void setUsersToMeeting(int meetingID, String... userIDs) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement PS2 = connection.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");) {

            int referenceAttrId = 307; // Параметр-ссылка, в данном случае - список участников встречи

            for (String userID : userIDs) {
                PS2.setInt(1, meetingID); // ID встречи
                PS2.setInt(2, referenceAttrId); // ID параметра(307)
                PS2.setString(3, userID); // ID пользователя
                PS2.addBatch();
            }
            PS2.executeBatch();
        }
    }


    public void removeUsersFromMeeting(String meetingID, String... userIDs) throws SQLException {
        try (Connection connection = getConnection();
             PreparedStatement PS2 = connection.prepareStatement("DELETE FROM REFERENCES WHERE REFERENCE = ? AND OBJECT_ID = ?");) {

            for (String userID : userIDs) {
                PS2.setString(1, userID); // ID пользователя
                PS2.setString(2, meetingID); // ID встречи
                PS2.executeUpdate();
            }
        }
    }


    public ArrayList<User> getUsersAtMeeting(int meetingID) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException, SQLException, NullPointerException {
        ArrayList<User> Res = new ArrayList<>();
        try (Connection Con = getConnection();
             PreparedStatement PS = Con.prepareStatement("SELECT " +
                     "        re.REFERENCE " +
                     " FROM  OBJECTS ob " +
                     "      LEFT JOIN REFERENCES re " +
                     "        ON ob.OBJECT_ID = re.OBJECT_ID " +
                     "      LEFT JOIN PARAMS pa1 " +
                     "        ON re.REFERENCE = pa1.OBJECT_ID AND pa1.ATTR_ID = 1 " +
                     "WHERE ob.OBJECT_ID = ? AND re.ATTR_ID = 307 ORDER BY re.OBJECT_ID");) {

            // PreparedStatement PS = Con.prepareStatement("SELECT OBJECT_NAME FROM OBJECTS WHERE OBJECT_TYPE_ID = ?");

            PS.setInt(1, meetingID); // В качестве параметра id встречи
            ResultSet RS = PS.executeQuery(); // System.out.println(RS);
            while (RS.next()) {
                User user = this.getUserAndEventByUserID(RS.getInt(1));
                Res.add(user);
            }
            RS.close();
            return Res;
        }
    }


    //endregion


    //region DO Methods

    // 2017-02-20 Обновленный метод добавления в друзья (работа через DO)
    public void setFriend(int idFriend) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        int idUser = getObjID(userService.getCurrentUsername()); // Получаем id текущего авторизованного пользователя
        if (idUser != idFriend) { // если не пытаемся добавить самого себя в друзья
            int attrId = 12; // ID атрибута в базе, соответствующий друзьям пользователя

            DataObject currentUser = getObjectsByIdAlternative(idUser); // Получаем DO текущего пользователя
            DataObject friendUser = getObjectsByIdAlternative(idFriend); // Получаем DO добавляемого в друзья пользователя

            // Вытаскиваем нужную нам мапу со ссылками из текущего DO пользователя
            TreeMap<Integer, ArrayList<Integer>> currentRef = currentUser.getRefParams();
            // Вытаскиваем нужный лист со списком юзеров из мапы
            ArrayList<Integer> al = currentRef.get(attrId);
            // Если у текущего пользоателя нет списка друзей или такого пользователя нет в его списке друзей, то
            if ((al == null) || (!al.contains(idFriend))) {
                System.out.println("Добавили нового друга");
                // Добавляем к себе пользователя в друзья
                currentUser.setRefParams(attrId, idFriend);
                // Добавляем себя пользователю в друзья !!!!! 2017-02-04 !!! Вот этого не надо!!! Пусть юзер сам решит, добавлять ли нас в друзья, или нет
                // friendUser.setRefParams(attrId, idUser);
                // и обновляем DO обоих пользователей в базе:
                updateDataObject(currentUser);
                // updateDataObject(friendUser);
            }
        }
    }

    // 2017-02-20 Обновленный метод удаления из друзей (работа через DO)
    public void deleteFriend(int idFriend) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        int idUser = getObjID(userService.getCurrentUsername()); // Получаем id текущего авторизованного пользователя
        // if (idUser != idFriend) { // если не пытаемся удалить самого себя из друзья
        int attrId = 12; // ID атрибута в базе, соответствующий друзьям пользователя

        DataObject currentUser = getObjectsByIdAlternative(idUser); // Получаем DO текущего пользователя
        DataObject friendUser = getObjectsByIdAlternative(idFriend); // Получаем DO добавленного в друзья пользователя
        System.out.println("Удалили одного друга");
        // Удаляем у себя пользователя из друзей
        currentUser.deleteRefParams(attrId, idFriend);
        // Удаляем у пользователя себя из друзей
        // friendUser.deleteRefParams(attrId, idUser);
        updateDataObject(currentUser);
        // updateDataObject(friendUser);
    }


    // 2017-02-12 12-56 // Обновление ссылки на загруженный аватар:
    public void updateAvatar(int userId, String patch) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        //System.out.println(userId + " " + patch);
        try (Connection Con = getConnection();
             PreparedStatement PS = Con.prepareStatement("DELETE FROM PARAMS WHERE OBJECT_ID = ? AND ATTR_ID = ?"); // DELETE FROM PARAMS WHERE OBJECT_ID = '10005' AND ATTR_ID = '11';
             PreparedStatement PS1 = Con.prepareStatement("INSERT INTO PARAMS (OBJECT_ID, ATTR_ID, VALUE) VALUES (?, ?, ?)");) { // INSERT INTO PARAMS (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10005','11','\\upload\\10005\\avatar\\avatar_10005.png');

            // Удаляем ту ссылку, которая уже имеется:

            PS.setInt(1, userId);
            PS.setInt(2, 11);
            PS.executeUpdate();
            // И создаем новую:

            PS1.setInt(1, userId);
            PS1.setInt(2, 11);
            PS1.setString(3, patch);
            PS1.executeUpdate();
        }
    }

    // 2017-02-14 Альтернативный вспомогательный метод, вытаскивает все поля ДатаОбджекта, используя универсальный запрос в базу
    public DataObject getObjectsByIdAlternative(int objectId) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        try (Connection Con = getConnection();
             PreparedStatement PS = Con.
                     prepareStatement("(SELECT -2 AS KEY, CAST(OBJECT_ID AS VARCHAR(70)) AS VALUE, 0 AS REF FROM OBJECTS WHERE OBJECT_ID = ?) " +
                             "UNION (SELECT -1, OBJECT_NAME, 0 FROM OBJECTS WHERE OBJECT_ID = ?) " +
                             "UNION (SELECT 0, CAST(OBJECT_TYPE_ID AS VARCHAR(70)), 0 FROM OBJECTS WHERE OBJECT_ID = ?) " +
                             "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) " +
                             "AS VALUE_LIST, 0 FROM PARAMS pa WHERE OBJECT_ID = ? AND ATTR_ID != 12 AND ATTR_ID != 13) " +
                             "UNION (SELECT ATTR_ID, CAST(REFERENCE AS VARCHAR(70)), 1 FROM REFERENCES WHERE OBJECT_ID = ?)")) {


            // В качестве всех параметров id датаобджекта:
            PS.setInt(1, objectId);
            PS.setInt(2, objectId);
            PS.setInt(3, objectId);
            PS.setInt(4, objectId);
            PS.setInt(5, objectId);

            ResultSet RS = PS.executeQuery();
            DataObject dataObject = new DataObject();

            // Обходим всю полученную таблицу и формируем поля датаобджекта
            while (RS.next()) {
                Integer key = RS.getInt(1); // key


                String value = RS.getString(2); // value
                // Удаление дублирования строк (Вася Вася Вася):
                value = (((value != null) && (value.indexOf('~') > 0)) ? value.substring(0, value.indexOf('~')) : value);
                if (value != null) value = value.trim();
                Integer ref = RS.getInt(3); // ref (reference flag, 0 - not ref, 1 - ref)
                // System.out.println(key + " : " + value); // для отладки

                if (key == -2) { // Это пришел к нам айдишник
                    dataObject.setId(Integer.parseInt(value));
                    System.out.println(dataObject.getId());
                } else if (key == -1) { // Это пришло к нам имя
                    dataObject.setName(value);
                } else if (key == 0) { // Это пришел к нам тип
                    dataObject.setObjectTypeId(Integer.parseInt(value));
                } else { // Иначе пришли параматры или ссылки
                    if (ref == 0) { // Значит, это пришли параметры
                        dataObject.setParams(key, value);
                    } else { // Иначе пришли ссылки
                        dataObject.setRefParams(key, Integer.parseInt(value));
                    }
                }
            }

            RS.close();
            System.out.println("getObjectsByIdAlternative");
            return dataObject;
        }
    }

    // 2017-02-14 Альтернативный вспомогательный метод, вытаскивает список ДатаОбджектов по списку id, используя универсальный запрос в базу
    public ArrayList<DataObject> getListObjectsByListIdAlternative(ArrayList<Integer> objectIds) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ArrayList<DataObject> dataObjectList = new ArrayList<>();
        if (objectIds.size() > 0) {
            // Формируем вставку в запрос вида IN (10001, 10002, 10003) из переданного списка айдишников
            String set = "IN (";
            for (int i = 0; i < objectIds.size() - 1; i++) { // кроме последнего элемента
                set += objectIds.get(i) + ", ";
            }
            set += objectIds.get(objectIds.size() - 1) + ")";

            String sql = "SELECT * FROM((SELECT -2 AS KEY, CAST(OBJECT_ID AS VARCHAR(70)) AS VALUE, 0 AS REF, OBJECT_ID FROM OBJECTS WHERE OBJECT_ID " + set + ") " +
                    "UNION (SELECT -1, OBJECT_NAME, 0, OBJECT_ID FROM OBJECTS WHERE OBJECT_ID " + set + ") " +
                    "UNION (SELECT 0, CAST(OBJECT_TYPE_ID AS VARCHAR(70)), 0, OBJECT_ID FROM OBJECTS WHERE OBJECT_ID " + set + ") " +
                    "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) AS VALUE_LIST, 0, OBJECT_ID FROM PARAMS pa " +
                    "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13) " +
                    "UNION (SELECT ATTR_ID, CAST(REFERENCE AS VARCHAR(70)), 1, OBJECT_ID FROM REFERENCES WHERE OBJECT_ID " + set + ")) ORDER BY OBJECT_ID, KEY";
            try (Connection Con = getConnection();
                 PreparedStatement PS = Con.prepareStatement(sql);
                 ResultSet RS = PS.executeQuery();) {


                // Обходим всю полученную таблицу и формируем поля датаобджектов
                DataObject dataObject = null;
                while (RS.next()) {
                    Integer key = RS.getInt(1); // key
                    String value = RS.getString(2); // value
                    // Удаление дублирования строк (Вася Вася Вася):
                    value = (((value != null) && (value.indexOf('~') > 0)) ? value.substring(0, value.indexOf('~')) : value);
                    if (value != null) value = value.trim();
                    Integer ref = RS.getInt(3); // ref (reference flag, 0 - not ref, 1 - ref)
                    Integer id = RS.getInt(4); // object id

                    if (key == -2) { // Это пришел к нам айдишник
                        if (dataObject != null) {
                            dataObjectList.add(dataObject); // кладем предыдущий объект в лист
                        }
                        dataObject = new DataObject(); // создаем новый, и будем теперь в него писать
                        dataObject.setId(Integer.parseInt(value));
                    } else if (key == -1) { // Это пришло к нам имя
                        dataObject.setName(value);
                    } else if (key == 0) { // Это пришел к нам тип
                        dataObject.setObjectTypeId(Integer.parseInt(value));
                    } else { // Иначе пришли параматры или ссылки
                        if (ref == 0) { // Значит, это пришли параметры
                            dataObject.setParams(key, value);
                        } else { // Иначе пришли ссылки
                            dataObject.setRefParams(key, Integer.parseInt(value));
                        }
                    }
                }
                // и в конце надо дописать последний элемент, который из-за while не занесся в лист:
                if (dataObject != null) { // если успели прочитать поля в объект, то есть он не просто пустая заготовка
                    dataObjectList.add(dataObject); // кладем объект в лист
                }
                //dataObjectList.add(dataObject); // кладем объект в лист
            }
        }
        return dataObjectList;
    }


    // 2017-02-14 Альтернативный вспомогательный метод, вытаскивает список id подходящих под фильтры датаобджектов


    /* ................................................................................................................... */
    // 2017-02-16 Парсер-генератор строки SQL-запроса по переданному фильтру:
    public String parseGenerate(BaseFilter filter) throws SQLException {
        System.out.println("Запускаю parseGenerate");

        String sql = "SELECT ob.OBJECT_ID FROM OBJECTS ob ";
        TreeMap<String, ArrayList<String>> params = filter.getParams();

        // в зависимости от типа фильтра
        // 2017-04-01 Update, добавлены логи
        if (filter instanceof LogFilter) {
            //  начинаем вытаскивать параметры и формировать строку запроса:
            if (params.get(LogFilter.ALL) != null) { // если надо получить IDs всех логов,
                sql += "WHERE ob.OBJECT_TYPE_ID = " + LOG;
            } else if (params.get(LogFilter.FOR_CURRENT_USER) != null) { // если надо получить ID всех логов текущего пользователей,
                /*
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE AND re.ATTR_ID = 31 ";
                    sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + LOG + " ";
                sql += "AND ob2.OBJECT_NAME = " + "'" + userService.getCurrentUsername() + "'" + " ";
                */

                // Исправленный, 2017-04-04, выбор по айди пользователя
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + LOG + " ";
                sql += "AND ob2.OBJECT_ID = " + userService.getCurrentUser().getId() + " ";

                //sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE ";
                //sql += "AND re.ATTR_ID = 31 JOIN PARAMS pa ON pa.VALUE = " + "'" + userService.getCurrentUsername() + "'" + " AND pa.ATTR_ID = 4 ";

            } else if (params.get(LogFilter.FOR_USER_WITH_NAME) != null) { // если надо получить ID всех логов пользователя с конкретным именем,
                ArrayList<String> user_name = params.get(LogFilter.FOR_USER_WITH_NAME);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE AND re.ATTR_ID = 31 ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + LOG + " ";
                sql += "AND ob2.OBJECT_NAME = " + user_name.get(0) + " ";
            } else if (params.get(LogFilter.FOR_USER_WITH_ID) != null) { // если надо получить ID всех логов пользователя с конкретным id,
                ArrayList<String> user_id = params.get(LogFilter.FOR_USER_WITH_ID);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE AND re.ATTR_ID = 31 ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + LOG + " ";
                sql += "AND ob2.OBJECT_ID = " + user_id.get(0) + " ";
            } else {
                return null; // Иначе не нашли основного фильтра, не сможем составить запрос
            }
            sql += "ORDER BY ob.OBJECT_ID";

            // Прикручиваем промежуточные фильтры:
            if (params.get(LogFilter.WITH_TYPE) != null) { // если надо получить ID всех логов ДО какой-то даты,
                ArrayList<String> type_id = params.get(LogFilter.WITH_TYPE);
                String sql_1 = "";
                for (int i = 0; i < type_id.size(); i++) {
                    sql_1 += type_id.get(i) + ", ";
                }
                sql_1 += "0";

                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID ";
                sql += "WHERE pa.ATTR_ID IN (" + sql_1 + ") ";
                sql += "UNION ";
                sql += "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID ";
                sql += "WHERE re.ATTR_ID IN (" + sql_1 + ") ";
            }

            // Прикручиваем вспомогательные фильтры:
            if (params.get(LogFilter.BEFORE_DATE) != null) { // если надо получить ID всех логов ДО какой-то даты,
                ArrayList<String> before_date = params.get(LogFilter.BEFORE_DATE);
                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 600 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') < TO_DATE('" + before_date.get(0) + "', 'dd.mm.yyyy hh24:mi:ss'))";
            } else if (params.get(LogFilter.AFTER_DATE) != null) { // если надо получить ID всех логов ПОСЛЕ какой-то даты,
                ArrayList<String> after_date = params.get(LogFilter.AFTER_DATE);
                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 600 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') > TO_DATE('" + after_date.get(0) + "', 'dd.mm.yyyy hh24:mi:ss'))";
            } else if (params.get(LogFilter.BETWEEN_TWO_DATES) != null) { // если надо получить ID всех логов МЕЖДУ двумя датами,
                ArrayList<String> date = params.get(LogFilter.BETWEEN_TWO_DATES);
                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 600 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') " +
                        "BETWEEN TO_DATE('" + date.get(0) + "', 'dd.mm.yyyy hh24:mi:ss') AND TO_DATE('" + date.get(1) + "', 'dd.mm.yyyy hh24:mi:ss'))";
            }

        }

        else if (filter instanceof TagFilter) {
            //  начинаем вытаскивать параметры и формировать строку запроса:
            if (params.get(TagFilter.ALL) != null) { // если надо получить IDs всех нодов тегов,
                sql += "WHERE ob.OBJECT_TYPE_ID = " + TAG;
                //System.out.println("Формирую запрос " + sql);
            } else if (params.get(TagFilter.WITHOUT_ID) != null) { // если надо получить IDs сех тегок, кроме перечисленных,
                sql += "WHERE ob.OBJECT_TYPE_ID = " + TAG + " AND ob.OBJECT_ID NOT IN (";
                ArrayList<String> tag_id = params.get(TagFilter.WITHOUT_ID);
                int i;
                for (i = 0; i < tag_id.size() - 1; i++) {
                    sql += tag_id.get(i) + ", ";
                }
                sql += "0) ";

            } else {
                return null; // Иначе не нашли основного фильтра, не сможем составить запрос
            }
            sql += "ORDER BY ob.OBJECT_ID";

        } else if (filter instanceof UserFilter) {
            //  начинаем вытаскивать параметры и формировать строку запроса:
            if (params.get(UserFilter.ALL) != null) { // если надо получить IDs всех пользователей,
                sql += "WHERE ob.OBJECT_TYPE_ID = " + USER;
                System.out.println("Формирую запрос " + sql);
            } else if (params.get(UserFilter.CURRENT) != null) { // если надо получить ID текущего пользователей,

                sql += "JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID AND pa.ATTR_ID = 4 ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + USER + " ";
                sql += "AND pa.VALUE = \'" + userService.getCurrentUsername() + "\' ";
            } else if (params.get(UserFilter.WITH_NAME) != null) { // если надо получить ID пользователя по его имени,
                sql += "WHERE ob.OBJECT_TYPE_ID = " + USER + " ";
                sql += "AND ob.OBJECT_NAME IN (";
                ArrayList<String> names = params.get(UserFilter.WITH_NAME);
                int i;
                for (i = 0; i < names.size() - 1; i++) {
                    sql += names.get(i) + ", ";
                }
                sql += names.get(i) + ") ";
            } else if (params.get(UserFilter.SEARCH_USER) != null) { // если надо найти пользователя через поиск,
                sql += "WHERE ob.OBJECT_TYPE_ID = " + USER + " ";
                ArrayList<String> search = params.get(UserFilter.SEARCH_USER);
                String sqlName = "'%" + search.get(0) + "%'";
                sql += "AND (lower(ob.OBJECT_NAME) LIKE lower(" + sqlName + ")) ";
            } else if (params.get(UserFilter.WITH_EMAIL) != null) { // если надо получить ID пользователей по их e-mail'ам,
                sql = "SELECT ob.OBJECT_ID FROM PARAMS ob WHERE ob.ATTR_ID = 6 and ob.VALUE IN (";
                ArrayList<String> emails = params.get(UserFilter.WITH_NAME);
                int i;
                for (i = 0; i < emails.size() - 1; i++) {
                    sql += emails.get(i) + ", ";
                }
                sql += emails.get(i) + ") ";

            } else if (params.get(UserFilter.ALL_FRIENDS_FOR_USER_WITH_ID) != null) { // если надо получить ID друзей данного пользователя по его id,
                ArrayList<String> user_id = params.get(UserFilter.ALL_FRIENDS_FOR_USER_WITH_ID);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID AND re.ATTR_ID = 12 ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + USER + " AND ob2.OBJECT_ID = " + user_id.get(0) + " ";
            } else if (params.get(UserFilter.ALL_FRIENDS_CONFIRMED_FRIENDSHIP) != null) { // если надо получить ID друзей данного пользователя, подтвердивших дружбу,
                int current_user_id = userService.getObjID(userService.getCurrentUsername()); // userService.getCurrentUser().getId().toString(); // айди текущего юзера
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID ";
                sql += "JOIN OBJECTS ob2 ON re.REFERENCE = ob2.OBJECT_ID AND re.ATTR_ID = 12 ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + USER + " AND ob2.OBJECT_ID = " + current_user_id + " ";
                sql += "AND ob2.OBJECT_ID IN ";
                sql += "(SELECT obb.OBJECT_ID FROM OBJECTS obb ";
                sql += "JOIN REFERENCES reb ON obb.OBJECT_ID = reb.OBJECT_ID ";
                sql += "JOIN OBJECTS obb2 ON reb.REFERENCE = obb2.OBJECT_ID AND reb.ATTR_ID = 12 ";
                sql += "WHERE obb.OBJECT_TYPE_ID = " + USER + " AND obb2.OBJECT_ID = ob.OBJECT_ID) ";
            } else if (params.get(UserFilter.ALL_FRIENDS_UNCONFIRMED_FRIENDSHIP) != null) { // если надо получить ID друзей данного пользователя, еще НЕ подтвердивших дружбу,
                int current_user_id = userService.getObjID(userService.getCurrentUsername()); // userService.getCurrentUser().getId().toString(); // айди текущего юзера
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID ";
                sql += "JOIN OBJECTS ob2 ON re.REFERENCE = ob2.OBJECT_ID AND re.ATTR_ID = 12 ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + USER + " AND ob2.OBJECT_ID = " + current_user_id + " ";
                sql += "AND ob2.OBJECT_ID NOT IN ";
                sql += "(SELECT obb.OBJECT_ID FROM OBJECTS obb ";
                sql += "JOIN REFERENCES reb ON obb.OBJECT_ID = reb.OBJECT_ID ";
                sql += "JOIN OBJECTS obb2 ON reb.REFERENCE = obb2.OBJECT_ID AND reb.ATTR_ID = 12 ";
                sql += "WHERE obb.OBJECT_TYPE_ID = " + USER + " AND obb2.OBJECT_ID = ob.OBJECT_ID) ";
            } else {
                return null; // Иначе не нашли основного фильтра, не сможем составить запрос
            }
            // sql += "ORDER BY ob.OBJECT_ID";

            if (params.get(UserFilter.WITH_ALL_EVENTS) != null) { // если к тому же надо получить IDs пользователя и всех его событий,
                String sql1 = sql; // тут уже есть список пользовтельских айди
                String sql2 = "SELECT re.REFERENCE FROM REFERENCES re WHERE re.ATTR_ID = 13 and re.OBJECT_ID IN (" + sql + ") ";
                sql2 += "ORDER BY re.REFERENCE";
                sql = "(" + sql1 + ") UNION (" + sql2 + ")";
            }
        } else if (filter instanceof EventFilter) {
            // Работаем с событиями
            if (params.get(EventFilter.ALL) != null) { // если надо получить IDs всех событий в системе,
                sql += "WHERE ob.OBJECT_TYPE_ID = " + EVENT;
            } else if (params.get(EventFilter.FOR_CURRENT_USER) != null) { // если надо получить ID всех событий текущего пользователей,
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE AND re.ATTR_ID = 13 ";
                sql += "JOIN PARAMS pa ON re.OBJECT_ID = pa.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + EVENT + " ";
                sql += "AND pa.VALUE = " + "'" + userService.getCurrentUsername() + "'" + " ";
            } else if (params.get(EventFilter.FOR_USER_WITH_NAME) != null) { // если надо получить ID всех событий пользователя с конкретным именем,
                ArrayList<String> user_name = params.get(EventFilter.FOR_USER_WITH_NAME);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE AND re.ATTR_ID = 13 ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + EVENT + " ";
                sql += "AND ob2.OBJECT_NAME = " + user_name.get(0) + " ";
            } else if (params.get(EventFilter.FOR_USER_WITH_ID) != null) { // если надо получить ID всех событий пользователя с конкретным id,
                ArrayList<String> user_id = params.get(EventFilter.FOR_USER_WITH_ID);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE AND re.ATTR_ID = 13 ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + EVENT + " ";
                sql += "AND ob2.OBJECT_ID = " + user_id.get(0) + " ";
            } else if (params.get(EventFilter.BETWEEN_USERS_WITH_NAMES) != null) { // если надо получить ID всех событий между пользователями с конкретными именами (ПЕРЕСЕЧЕНИЕ),
                ArrayList<String> user_names = params.get(EventFilter.BETWEEN_USERS_WITH_NAMES);
                int i;
                sql = "SELECT * FROM (";
                for (i = 0; i < user_names.size(); i++) {
                    sql += "(SELECT ob.OBJECT_ID FROM OBJECTS ob ";
                    sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE AND re.ATTR_ID = 13 ";
                    sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                    sql += "WHERE ob.OBJECT_TYPE_ID = " + EVENT + " ";
                    sql += "AND ob2.OBJECT_NAME = " + user_names.get(i) + ") ";
                    if (i < user_names.size() - 1) {
                        sql += "INTERSECT ";
                    }
                }
                sql += sql + ") ";

            } else if (params.get(EventFilter.BETWEEN_USERS_WITH_IDS) != null) { // если надо получить ID всех событий пользователя с конкретным id (ПЕРЕСЕЧЕНИЕ),
                ArrayList<String> user_ids = params.get(EventFilter.BETWEEN_USERS_WITH_IDS);
                int i;
                sql = "SELECT * FROM (";
                for (i = 0; i < user_ids.size(); i++) {
                    sql += "(SELECT ob.OBJECT_ID FROM OBJECTS ob ";
                    sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE AND re.ATTR_ID = 13 ";
                    sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                    sql += "WHERE ob.OBJECT_TYPE_ID = " + EVENT + " ";
                    sql += "AND ob2.OBJECT_ID = " + user_ids.get(i) + ") ";
                    if (i < user_ids.size() - 1) {
                        sql += "INTERSECT ";
                    }
                }
                sql += sql + ") ";
            } else {
                return null; // Иначе не нашли основного фильтра, не сможем составить запрос
            }
            // Прикручиваем вспомогательные фильтры:
            if (params.get(EventFilter.BEFORE_DATE) != null) { // если надо получить ID всех событий ДО какой-то даты,
                ArrayList<String> before_date = params.get(EventFilter.BEFORE_DATE);
                sql += "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 101 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') < TO_DATE(" + before_date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss'))";
            } else if (params.get(EventFilter.AFTER_DATE) != null) { // если надо получить ID всех событий ПОСЛЕ какой-то даты,
                ArrayList<String> after_date = params.get(EventFilter.AFTER_DATE);
                sql += "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 101 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') > TO_DATE(" + after_date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss'))";
            } else if (params.get(EventFilter.BETWEEN_TWO_DATES) != null) { // если надо получить ID всех событий МЕЖДУ двумя датами,
                ArrayList<String> date = params.get(EventFilter.BETWEEN_TWO_DATES);
                sql += "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 101 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') " +
                        "BETWEEN TO_DATE(" + date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss') AND TO_DATE(" + date.get(1) + ", 'dd.mm.yyyy hh24:mi:ss'))";
            }

            //sql += "ORDER BY ob.OBJECT_ID"; // И группируем. Возможно придется сабрать в каждую else, если не сработает с INTERSECT

        } else if (filter instanceof MeetingFilter) {
            // Работаем со встречами
            if (params.get(MeetingFilter.ALL) != null) { // если надо получить IDs всех встреч в системе,
                sql += "WHERE ob.OBJECT_TYPE_ID = " + MEETING;
            } else if (params.get(MeetingFilter.FOR_CURRENT_USER) != null) { // поправил 2017-03-02 если надо получить ID всех встреч текущего пользователей,
                //-- Правильное получение списка айди всех встреч текущего пользователя
                //SELECT ob.OBJECT_ID FROM OBJECTS ob
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID AND re.ATTR_ID = 307 ";
                sql += "JOIN PARAMS pa ON re.REFERENCE = pa.OBJECT_ID AND pa.ATTR_ID = 4 ";
                sql += "WHERE ob.OBJECT_TYPE_ID =  " + MEETING + " ";
                sql += "AND pa.VALUE = " + "'" + userService.getCurrentUsername() + "'" + " ";
            } else if (params.get(MeetingFilter.FOR_USER_WITH_NAME) != null) { // если надо получить ID всех встреч пользователя по его имени,
                ArrayList<String> user_name = params.get(MeetingFilter.FOR_USER_WITH_NAME);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID AND re.ATTR_ID = 307 ";
                sql += "JOIN OBJECTS ob2 ON ob2.OBJECT_ID = re.REFERENCE ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + MEETING + " ";
                sql += "AND ob2.OBJECT_NAME = " + user_name.get(0) + " ";
            } else if (params.get(MeetingFilter.FOR_USER_WITH_ID) != null) { // если надо получить ID всех встреч пользователя по его ID,
                ArrayList<String> user_id = params.get(MeetingFilter.FOR_USER_WITH_ID);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID AND re.ATTR_ID = 307 ";
                sql += "JOIN OBJECTS ob2 ON ob2.OBJECT_ID = re.REFERENCE ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + MEETING + " ";
                sql += "AND ob2.OBJECT_ID = " + user_id.get(0) + " ";
            } else if (params.get(MeetingFilter.BETWEEN_USERS_WITH_NAMES) != null) { // если надо получить ID всех встреч между пользователями с конкретными именами (ПЕРЕСЕЧЕНИЕ),
                ArrayList<String> user_names = params.get(MeetingFilter.BETWEEN_USERS_WITH_NAMES);
                int i;
                sql = "SELECT * FROM (";
                for (i = 0; i < user_names.size(); i++) {
                    sql += "(SELECT ob.OBJECT_ID FROM OBJECTS ob ";
                    sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID AND re.ATTR_ID = 307 ";
                    sql += "JOIN OBJECTS ob2 ON ob2.OBJECT_ID = re.REFERENCE ";
                    sql += "WHERE ob.OBJECT_TYPE_ID = " + MEETING + " ";
                    sql += "AND ob2.OBJECT_NAME = " + user_names.get(i) + ") ";
                    if (i < user_names.size() - 1) {
                        sql += "INTERSECT ";
                    }
                }
                sql += sql + ") ";
            } else if (params.get(MeetingFilter.BETWEEN_USERS_WITH_IDS) != null) { // если надо получить ID всех встреч между пользователями с конкретными IDs (ПЕРЕСЕЧЕНИЕ),
                ArrayList<String> user_ids = params.get(MeetingFilter.BETWEEN_USERS_WITH_IDS);
                int i;
                sql = "SELECT * FROM (";
                for (i = 0; i < user_ids.size(); i++) {
                    sql += "(SELECT ob.OBJECT_ID FROM OBJECTS ob ";
                    sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID AND re.ATTR_ID = 307 ";
                    sql += "JOIN OBJECTS ob2 ON ob2.OBJECT_ID = re.REFERENCE ";
                    sql += "WHERE ob.OBJECT_TYPE_ID = " + MEETING + " ";
                    sql += "AND ob2.OBJECT_ID = " + user_ids.get(i) + ") ";
                    if (i < user_ids.size() - 1) {
                        sql += "INTERSECT ";
                    }
                }
                sql += sql + ") ";
            } else {
                return null; // Иначе не нашли основного фильтра, не сможем составить запрос
            }

            // Прикручиваем вспомогательные фильтры:
            if (params.get(MeetingFilter.BEFORE_DATE) != null) { // если надо получить ID всех встреч, ЗАВЕРШАЮЩИХСЯ ДО какой-то даты,
                ArrayList<String> before_date = params.get(MeetingFilter.BEFORE_DATE);
                sql += "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 303 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') < TO_DATE(" + before_date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss'))";
            } else if (params.get(MeetingFilter.AFTER_DATE) != null) { // если надо получить ID всех встреч, НАЧИНАЮЩИХСЯ ПОСЛЕ какой-то даты,
                ArrayList<String> after_date = params.get(MeetingFilter.AFTER_DATE);
                sql += "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 302 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') > TO_DATE(" + after_date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss'))";
            } else if (params.get(MeetingFilter.BETWEEN_TWO_DATES) != null) { // если надо получить ID всех встреч МЕЖДУ двумя датами,
                ArrayList<String> date = params.get(MeetingFilter.BETWEEN_TWO_DATES);
                sql += "SELECT ob.OBJECT_ID FROM (" + sql + ") ob " +
                        "JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 301 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') > TO_DATE(" + date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss'))" +
                        "JOIN PARAMS pa1 ON ob.OBJECT_ID = pa1.OBJECT_ID " +
                        "AND pa1.ATTR_ID = 302 AND (TO_DATE(pa1.VALUE, 'dd.mm.yyyy hh24:mi:ss') < TO_DATE(" + date.get(1) + ", 'dd.mm.yyyy hh24:mi:ss'))";
            }

            //sql += "ORDER BY ob.OBJECT_ID"; // И группируем. Возможно придется сабрать в каждую else, если не сработает с INTERSECT
        } else if (filter instanceof MessageFilter) {
            // Работаем с сообщениями
            if (params.get(MessageFilter.ALL) != null) { // если надо получить IDs всех сообщений в системе,
                sql += "WHERE ob.OBJECT_TYPE_ID = " + MESSAGE;
            } else if (params.get(MessageFilter.FOR_CURRENT_USER) != null) { // если надо получить ID всех отправленных сообщений текущего пользователей,
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE AND re.ATTR_ID = 30 ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + MESSAGE + " ";
                sql += "AND ob2.OBJECT_NAME = " + "'" + userService.getCurrentUsername() + "'" + " ";
            } else if (params.get(MessageFilter.TO_CURRENT_USER) != null) { // если надо получить ID всех полученных сообщений текущего пользователей,
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE AND re.ATTR_ID = 30 ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                sql += "JOIN PARAMS pa ON ob2.OBJECT_ID = pa.OBJECT_ID AND pa.ATTR_ID = 4 ";
                sql += "JOIN PARAMS pa2 ON ob.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 202 ";

                sql += "JOIN OBJECTS ob3 ON pa2.VALUE = ob3.OBJECT_ID ";
                sql += "JOIN PARAMS pa3 ON ob3.OBJECT_ID = pa3.OBJECT_ID AND pa3.ATTR_ID = 4 ";

                sql += "WHERE ob.OBJECT_TYPE_ID = " + MESSAGE + " ";
                sql += "AND pa3.VALUE = " + "'" + userService.getCurrentUsername() + "'" + " ";
            } else if (params.get(MessageFilter.FOR_USER_WITH_NAME) != null) { // если надо получить ID всех отправленных сообщений пользователя с конкретным именем,
                ArrayList<String> user_name = params.get(MessageFilter.FOR_USER_WITH_NAME);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE AND re.ATTR_ID = 30 ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + MESSAGE + " ";
                sql += "AND ob2.OBJECT_NAME = " + user_name.get(0) + " ";
            } else if (params.get(MessageFilter.FOR_USER_WITH_ID) != null) { // если надо получить ID всех событий пользователя с конкретным id,
                ArrayList<String> user_id = params.get(MessageFilter.FOR_USER_WITH_ID);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE AND re.ATTR_ID = 30 ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + MESSAGE + " ";
                sql += "AND ob2.OBJECT_ID = " + user_id.get(0) + " ";
            } else if (params.get(MessageFilter.BETWEEN_TWO_USERS_WITH_NAMES) != null) { // если надо получить ID всех сообщений между двумя пользователями с конкретными именами друг другу,
                ArrayList<String> user_names = params.get(MessageFilter.BETWEEN_TWO_USERS_WITH_NAMES);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID AND re.ATTR_ID = 30 ";
                sql += "JOIN PARAMS pa1 ON ob.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 201 ";
                sql += "JOIN OBJECTS user1 ON pa1.VALUE = user1.OBJECT_ID ";
                sql += "JOIN PARAMS pa2 ON ob.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 202 ";
                sql += "JOIN OBJECTS user2 ON pa2.VALUE = user2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + MESSAGE + " ";
                sql += "AND (user1.OBJECT_NAME = " + user_names.get(0) + " AND user2.OBJECT_NAME = " + user_names.get(1) + " ";
                sql += "OR user1.OBJECT_NAME = " + user_names.get(1) + " AND user2.OBJECT_NAME = " + user_names.get(0) + ") ";
            } else if (params.get(MessageFilter.BETWEEN_TWO_USERS_WITH_IDS) != null) { // если надо получить ID всех сообщений двух пользователей с конкретным id друг другу,
                ArrayList<String> user_ids = params.get(MessageFilter.BETWEEN_TWO_USERS_WITH_IDS);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE  AND re.ATTR_ID = 30 ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                sql += "JOIN PARAMS pa1 ON ob.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 201 ";
                sql += "JOIN PARAMS pa2 ON ob.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 202 ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + MESSAGE + " ";
                sql += "AND (ob2.OBJECT_ID = " + user_ids.get(0) + " AND pa2.VALUE = " + user_ids.get(1) + " ";
                sql += "OR ob2.OBJECT_ID = " + user_ids.get(1) + " AND pa2.VALUE = " + user_ids.get(0) + ") ";
                sql += "ORDER BY ob.OBJECT_ID"; // 2017-02-26, иначе сообщения выводились вразноброд
            } else if (params.get(MessageFilter.FROM_TO_USERS_WITH_NAMES) != null) { // если надо получить ID всех сообщений от первого пользователя второму по их именам пользователей,
                ArrayList<String> user_names = params.get(MessageFilter.FROM_TO_USERS_WITH_NAMES);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID AND re.ATTR_ID = 30 ";
                sql += "JOIN PARAMS pa1 ON ob.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 201 ";
                sql += "JOIN OBJECTS user1 ON pa1.VALUE = user1.OBJECT_ID ";
                sql += "JOIN PARAMS pa2 ON ob.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 202 ";
                sql += "JOIN OBJECTS user2 ON pa2.VALUE = user2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + MESSAGE + " ";
                sql += "AND user1.OBJECT_NAME = " + user_names.get(0) + " AND user2.OBJECT_NAME = " + user_names.get(1) + " ";
            } else if (params.get(MessageFilter.FROM_TO_USERS_WITH_IDS) != null) { // если надо получить ID всех сообщений от первого пользователя второму по их id пользователей,
                ArrayList<String> user_ids = params.get(MessageFilter.FROM_TO_USERS_WITH_IDS);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.REFERENCE  AND re.ATTR_ID = 30 ";
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                sql += "JOIN PARAMS pa1 ON ob.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 201 ";
                sql += "JOIN PARAMS pa2 ON ob.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 202 ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + MESSAGE + " ";
                sql += "AND ob2.OBJECT_ID = " + user_ids.get(0) + " AND pa2.VALUE = " + user_ids.get(1) + " ";
            } else {
                return null; // Иначе не нашли основного фильтра, не сможем составить запрос
            }

            // Прикручиваем вспомогательные фильтры:
            if (params.get(MessageFilter.BEFORE_DATE) != null) { // если надо получить ID всех сообщений, отправленных ДО какой-то даты,
                ArrayList<String> before_date = params.get(MessageFilter.BEFORE_DATE);
                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 203 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') < TO_DATE(" + before_date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss'))";
            } else if (params.get(MessageFilter.AFTER_DATE) != null) { // если надо получить ID всех сообщений, отправленных ПОСЛЕ какой-то даты,
                ArrayList<String> after_date = params.get(MessageFilter.AFTER_DATE);
                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 203 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') > TO_DATE(" + after_date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss'))";
            } else if (params.get(MessageFilter.BETWEEN_TWO_DATES) != null) { // если надо получить ID всех сообщений, отправленных МЕЖДУ двумя датами,
                ArrayList<String> date = params.get(MessageFilter.BETWEEN_TWO_DATES);
                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob " +
                        "JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 203 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') > TO_DATE(" + date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss')) " +
                        "AND (TO_DATE(pa1.VALUE, 'dd.mm.yyyy hh24:mi:ss') < TO_DATE(" + date.get(1) + ", 'dd.mm.yyyy hh24:mi:ss'))"; // Можно сделать и between'ом, в принципе
            } else if (params.get(MessageFilter.UNREAD) != null) { // если надо получить ID всех непрочитанных сообщений, отправленных текущему пользователю,
                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 204 AND pa.VALUE = 0";
            }

            // 2017-03-12 Для уведомлений

            //sql += "ORDER BY ob.OBJECT_ID"; // И группируем. Возможно придется сабрать в каждую else, если не сработает с INTERSECT
        }

        ///// 2017-03-24 Поправил // 2017-03-12 Для уведомлений (не все еще реализовал, сделаю)
        else if (filter instanceof NotificationFilter) {
            // Работаем с уведомлениями
            if (params.get(NotificationFilter.ALL) != null) { // если надо получить IDs всех уведомлений в системе,
                sql += "WHERE ob.OBJECT_TYPE_ID = " + NOTIFICATION;
            } else if (params.get(NotificationFilter.FOR_CURRENT_USER) != null) { // если надо получить ID всех уведомлений для текущего пользователя,
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID AND re.ATTR_ID = 503 "; // 503 - это получатель
                sql += "JOIN OBJECTS ob2 ON re.REFERENCE = ob2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + NOTIFICATION + " ";
                sql += "AND ob2.OBJECT_ID = " + userService.getCurrentUser().getId() + " ";
            } else if (params.get(NotificationFilter.FOR_USER_WITH_NAME) != null) { // если надо получить ID всех уведомлений для пользователя с конкретным именем,
                ArrayList<String> user_name = params.get(NotificationFilter.FOR_USER_WITH_NAME);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID AND re.ATTR_ID = 503 "; // 503 - это получатель
                sql += "JOIN OBJECTS ob2 ON re.REFERENCE = ob2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + NOTIFICATION + " ";
                sql += "AND ob2.OBJECT_NAME = " + user_name.get(0) + " ";
            } else if (params.get(NotificationFilter.FOR_USER_WITH_ID) != null) { // если надо получить ID всех уведомлений для пользователя с конкретным id,
                ArrayList<String> user_id = params.get(NotificationFilter.FOR_USER_WITH_ID);
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID AND re.ATTR_ID = 503 "; // 503 - это получатель
                sql += "JOIN OBJECTS ob2 ON re.REFERENCE = ob2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + NOTIFICATION + " ";
                sql += "AND ob2.OBJECT_ID = " + user_id.get(0) + " ";
            } else {
                return null; // Иначе не нашли основного фильтра, не сможем составить запрос
            }

            // Прикручиваем вспомогательные фильтры:
            if (params.get(NotificationFilter.BEFORE_DATE) != null) { // если надо получить ID всех уведомлений юзеру, отправленных ДО какой-то даты,
                ArrayList<String> before_date = params.get(NotificationFilter.BEFORE_DATE);
                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 506 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') < TO_DATE(" + before_date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss'))"; // 506 параметр
            } else if (params.get(NotificationFilter.AFTER_DATE) != null) { // если надо получить ID всех уведомлений юзеру, отправленных ПОСЛЕ какой-то даты,
                ArrayList<String> after_date = params.get(NotificationFilter.AFTER_DATE);
                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 506 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') > TO_DATE(" + after_date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss'))";
            } else if (params.get(NotificationFilter.BETWEEN_TWO_DATES) != null) { // если надо получить ID всех уведомлений юзеру, отправленных МЕЖДУ двумя датами,
                ArrayList<String> date = params.get(NotificationFilter.BETWEEN_TWO_DATES);
                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob " +
                        "JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 506 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') > TO_DATE(" + date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss')) " +
                        "AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') < TO_DATE(" + date.get(1) + ", 'dd.mm.yyyy hh24:mi:ss'))"; // Можно сделать и between'ом, в принципе
            } else if (params.get(NotificationFilter.UNSEEN) != null) { // если надо получить ID всех непросмотренных уведомлений, отправленных текущему пользователю,
                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 507 AND pa.VALUE = '0'"; // Параметр 507
            } else if (params.get(NotificationFilter.SEEN) != null) { // если надо получить ID всех просмотренных уведомлений, отправленных текущему пользователю,
                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 507 AND pa.VALUE = '1'"; // Параметр 507
            }

            if (params.get(NotificationFilter.WITH_TYPE) != null) { // если надо получить ID всех уведомлений конкретного типа,
                ArrayList<String> type = params.get(NotificationFilter.WITH_TYPE);
                sql = "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 505 AND pa.VALUE = " + type.get(0); // Параметр 505
            }
        }
        /////


        System.out.println("Итоговый запрос " + sql);
        return sql;
    }

    // 2017-02-16 Исполнитель строки SQL-запроса, полученного от Парсер-генератора по переданному фильтру:
    // вытаскивает список id подходящих под фильтры датаобджектов
    public ArrayList<Integer> getListId_SQL_Executor(String sql) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        System.out.println("Выполняю запрос " + sql);
        ArrayList<Integer> idList = new ArrayList<>();
        if (sql != null) {

            try (Connection Con = getConnection();
                 PreparedStatement PS = Con.prepareStatement(sql);
                 ResultSet RS = PS.executeQuery();) {

                // Обходим всю полученную таблицу и формируем лист id-шек
                while (RS.next()) {
                    idList.add(RS.getInt(1));
                }
                RS.close();
                PS.close();
                CloseConnection(Con);
            }
            System.out.println("Возвращаю список");
        }
        return idList;
    }

    // 2017-02-16 Все вместе: новый метод для полчения списка объектов, удовлетворяющих фильтрам:
    // Альтернативный вспомогательный метод2, вытаскивает список id подходящих под фильтры датаобджектов
    public ArrayList<Integer> getListObjectsByFilters(BaseFilter filter) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        System.out.println("Запускаю getListObjectsByFilters c полученным фильтром");
        String sql = parseGenerate(filter);
        return getListId_SQL_Executor(sql);
    }


    //
    /*...............................................................................................................*/
    // 2017-03-26 Новый метод выгрузки датаобджектов-ТЕГов в базу (создание DO): (Поскольку надо сначала создать кипой все датаобджекты, а потом уже выставить им ссылки, иначе некуда будет привязываться
    public void setDataObjectTag(ArrayList<DataObject> newTagList, ArrayList<DataObject> updTagList) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, ParseException {
        // 1) Cначала создадим все ноды без ссылок друг на друга:
        for (int i = 0; i < newTagList.size(); i++) {
            //
            DataObject dataObject = newTagList.get(i);
            if (dataObject.getObjectTypeId() != 1010) return; // Выходим, если случайно передали не тег

            System.out.println("\n\nДля загрузки в базу пришел новый датаобджект:" +
                    "\nid = " + dataObject.getId() +
                    "\ntype = " + dataObject.getObjectTypeId() +
                    "\nname = " + dataObject.getName() +
                    "\nparams = " + dataObject.getParams() +
                    "\nreferences = " + dataObject.getRefParams() +
                    "");


            int id = dataObject.getId();
            try (Connection Con = getConnection();
                 PreparedStatement PS = Con.prepareStatement("INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES (?, ?, ?)");
                 PreparedStatement PS1 = Con.prepareStatement("INSERT INTO PARAMS (OBJECT_ID, ATTR_ID, VALUE) VALUES (?, ?, ?)");) {

                // A-1 Подготавливаем и заполняем в базе строку таблицы OBJECTS

                System.out.println(">> Новый нод тега с id =" + id);
                PS.setInt(1, id);
                PS.setInt(2, dataObject.getObjectTypeId());
                PS.setString(3, dataObject.getName());
                PS.executeUpdate();

                // A-2 Подготавливаем и заполняем в базе новые строки таблицы PARAMS
                // Получаем список параметров:

                while (!dataObject.getParams().isEmpty()) {
                    Map.Entry<Integer, String> en = dataObject.getParams().pollFirstEntry();
                    PS1.setInt(1, id);
                    PS1.setInt(2, en.getKey());
                    PS1.setString(3, en.getValue());
                    PS1.addBatch();
                }
                PS1.executeBatch();
            }
        }


        // 2) Затем таким же образом обойдем их и создадим необходимые ссылки
        for (int i = 0; i < newTagList.size(); i++) {

            DataObject dataObject = newTagList.get(i);

            int id = dataObject.getId();
            try (Connection Con = getConnection();
                 PreparedStatement PS2 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?, ?, ?)");) {

                // A-3 Подготавливаем и заполняем в базе новые строки таблицы REFERENCES
                // Получаем список параметров:
                while (!dataObject.getRefParams().isEmpty()) {
                    Map.Entry<Integer, ArrayList<Integer>> En = dataObject.getRefParams().pollFirstEntry();
                    ArrayList<Integer> valueList = En.getValue(); // получаем значение
                    for (Integer value : valueList) {
                        if (value != null) {
                            PS2.setInt(1, id);
                            PS2.setInt(2, En.getKey());
                            System.out.println("value = " + value);
                            PS2.setInt(3, value);
                            PS2.addBatch();
                        }
                    }
                }
                PS2.executeBatch();

            }

        }


        // 3) И, наконец, обойдем все оставшиеся ноды, которые не надо добавлять, им нужно только обновить ссылки и параметры
        System.out.println("Количество нодов на обновление: " + updTagList.size());
        System.out.println("Ноды на обновление: " + updTagList);
        for (int i = 0; i < updTagList.size(); i++) {

            DataObject dataObject = updTagList.get(i);
            if (dataObject.getName().equals("ROOT_NODE")) dataObject = (new Converter()).toDO(dataObject);
            System.out.println("\n\nДля обновления в базу пришел новый датаобджект:" +
                    "\nid = " + dataObject.getId() +
                    "\ntype = " + dataObject.getObjectTypeId() +
                    "\nname = " + dataObject.getName() +
                    "\nparams = " + dataObject.getParams() +
                    "\nreferences = " + dataObject.getRefParams() +
                    "");

            int id = dataObject.getId();
            try (Connection Con = getConnection();
                 PreparedStatement PS3 = Con.prepareStatement("UPDATE PARAMS SET VALUE = ? WHERE OBJECT_ID = ? AND ATTR_ID = ?");
                 PreparedStatement PS4 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?, ?, ?)");) {


                // A-2 Подготавливаем и заполняем в базе новые строки таблицы PARAMS
                // Получаем список параметров:

                while (!dataObject.getParams().isEmpty()) {
                    Map.Entry<Integer, String> en = dataObject.getParams().pollFirstEntry();
                    PS3.setString(1, en.getValue());
                    PS3.setInt(2, id);
                    PS3.setInt(3, en.getKey());
                    PS3.addBatch();
                }
                PS3.executeBatch();

                // A-3 Подготавливаем и заполняем в базе новые строки таблицы REFERENCES
                // Получаем список параметров:
                while (!dataObject.getRefParams().isEmpty()) {
                    Map.Entry<Integer, ArrayList<Integer>> En = dataObject.getRefParams().pollFirstEntry();
                    ArrayList<Integer> valueList = En.getValue(); // получаем значение
                    for (Integer value : valueList) {
                        if (value != null) {
                            PS4.setInt(1, id);
                            PS4.setInt(2, En.getKey());
                            System.out.println("Обновление value = " + value);
                            PS4.setInt(3, value);
                            PS4.addBatch();
                        }
                    }
                }
                PS4.executeBatch();

            }

        }


    }


    /*...............................................................................................................*/
    // 2017-02-18 Новый метод выгрузки датаобджекта в базу (создание DO): (исправил 2017-03-02)
    public int setDataObjectToDB(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        int id = generationID(dataObject.getObjectTypeId());
        try (Connection Con = getConnection();
             PreparedStatement PS = Con
                     .prepareStatement("INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES (?, ?, ?)");
             PreparedStatement PS1 = Con.prepareStatement("INSERT INTO PARAMS (OBJECT_ID, ATTR_ID, VALUE) VALUES (?, ?, ?)");) {

            // 1. Подготавливаем и заполняем в базе строку таблицы OBJECTS


            System.out.println(">> Новый объект с id =" + id);
            PS.setInt(1, id);
            PS.setInt(2, dataObject.getObjectTypeId());
            PS.setString(3, dataObject.getName());
            PS.executeUpdate();

            // 2. Подготавливаем и заполняем в базе новые строки таблицы PARAMS
            // Получаем список параметров:

            while (!dataObject.getParams().isEmpty()) {
                Map.Entry<Integer, String> En = dataObject.getParams().pollFirstEntry();
                PS1.setInt(1, id);
                PS1.setInt(2, En.getKey());
                PS1.setString(3, En.getValue());
                PS1.addBatch();
                //System.out.println("param: id = " + id + ", attr_id = " + En.getKey() + ", ref = " + En.getValue());
            }
            PS1.executeBatch();

            // Добавление ссылки для настроек. Потребовался отдельный блок, так как здесь value и id стоят на другом месте
            if (dataObject.getObjectTypeId().equals(SETTINGS)) {
                try (PreparedStatement PS7 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");) {
                    while (!dataObject.getRefParams().isEmpty()) {
                        Map.Entry<Integer, ArrayList<Integer>> En = dataObject.getRefParams().pollFirstEntry();
                        ArrayList<Integer> valueList = En.getValue(); // получаем значение
                        for (Integer value : valueList) {
                            PS7.setInt(1, value);
                            PS7.setInt(2, En.getKey());
                            PS7.setInt(3, id);
                        }
                    }
                    PS7.executeQuery();
                }
            }

            // 3. Подготавливаем и заполняем в базе новые строки таблицы REFERENCES
            // Получаем список параметров:
            int idUser = userService.getObjID(userService.getCurrentUsername());
            Integer host_id = idUser;
            try (PreparedStatement PS2 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?, ?, ?)");) {

                while (!dataObject.getRefParams().isEmpty()) {
                    Map.Entry<Integer, ArrayList<Integer>> En = dataObject.getRefParams().pollFirstEntry();
                    ArrayList<Integer> valueList = En.getValue(); // получаем значение
                    for (Integer value : valueList) {
                        PS2.setInt(1, id);
                        PS2.setInt(2, En.getKey());
                        PS2.setInt(3, value);
                        PS2.addBatch();
                        // System.out.println("reference: id = " + id + ", attr_id = " + En.getKey() + ", par = " + value);
                        if (En.getKey().equals(141)) {
                            host_id = value;
                        }
                    }
                }
                PS2.executeBatch();
            }

            System.out.println("host_id = " + host_id);

            // Если добавляем событие, то надо еще вручную создать ссылки:
            if (dataObject.getObjectTypeId().equals(EVENT)) { // Если это событие, то
                int attrId;
                // Проверяем, привязано ли событие к встрече, и если привязано, то ссылка на юзера тоже будет, но только как на создателя,
                // а ссылка на встречу будет уазывать, что это событие относится ко встрече, а не к расписанию юзера
                if ((host_id > START_ID_USER) & (host_id < START_ID_EVENT)) { // Если родителем является юзер, то
                    attrId = 13;
                    // 4) Добавление ссылки Юзер - Событие (связывание): (или Встреча - Событие)
                    try (PreparedStatement PS3 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");) {

                        PS3.setInt(1, host_id); // PS3.setInt(1, idUser);
                        PS3.setInt(2, attrId);
                        PS3.setInt(3, id);
                        PS3.executeQuery();
                    }

                    // 5) Добавление 13-го параметра в PARAMS (task_id для текущего пользователя):
                    try (PreparedStatement PS4 = Con.prepareStatement("INSERT INTO PARAMS (OBJECT_ID, ATTR_ID, VALUE) VALUES (?,?,?)");) {
                        PS4.setInt(1, host_id);
                        PS4.setInt(2, attrId);
                        PS4.setObject(3, id);
                        PS4.executeQuery();
                    }

                    // 6) (НА ВСЯКИЙ СЛУЧАЙ) Удаление 13-го параметра с VALUE = NULL в PARAMS (task_id для текущего пользователя):
                    try (PreparedStatement PS5 = Con.prepareStatement("DELETE FROM PARAMS WHERE OBJECT_ID = ? AND ATTR_ID = ? AND VALUE IS NULL");) {
                        PS5.setInt(1, host_id); // = user_id
                        PS5.setInt(2, attrId); // = 13
                        PS5.executeUpdate();
                    }
                } else if ((host_id > START_ID_MEETING) & (host_id < START_ID_USER)) { // иначе если родителем является встреча
                    attrId = 308;
                    // 2017-03-02 7) Добавление прямой ссылки Встреча - Событие (связывание):
                    try (PreparedStatement PS6 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");) {
                        PS6.setInt(1, host_id);
                        PS6.setInt(2, attrId);
                        PS6.setInt(3, id);
                        PS6.executeQuery();
                    }
                }
            /* не нужно
            attrId = 141;
            // 2017-03-02 8) Добавление обратной ссылки Событие - Юзер (связывание): (или Событие - Встреча)
            PreparedStatement PS7 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");
            PS7.setInt(1, id);
            PS7.setInt(2, attrId);
            PS7.setInt(3, host_id);
            PS7.executeQuery();
            PS7.close();
            */


            } else if (dataObject.getObjectTypeId().equals(MESSAGE)) { // Если это сообщение, то
                // 1) Добавление ссылки Юзер - Сообщение (связывание): INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES ('10001', '30', '30001');
                int attrId = 30;
                try (PreparedStatement PS6 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");) {
                    PS6.setInt(1, idUser);
                    PS6.setInt(2, attrId);
                    PS6.setInt(3, id);
                    PS6.executeQuery();
                }
            } else if (dataObject.getObjectTypeId().equals(LOG)) { // Если это логи, то
                // 1) Добавление ссылки Юзер - Лог (связывание): INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES ('10001', '31', '30001');
                int attrId = 31;
                try (PreparedStatement PS7 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");) {
                    PS7.setInt(1, idUser);
                    PS7.setInt(2, attrId);
                    PS7.setInt(3, id);
                    PS7.executeQuery();
                }
            }
        }

        return id;
    }

    // 2017-03-17 Метод выгрузки датаобджекта (лога) в базу (создание DO): (тут еще и айдишник пользовтеля надо передать, к которому лог привесить)
    public void setDataObjectToDB(DataObject dataObject, Integer idUser) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        int id = generationID(dataObject.getObjectTypeId());
        try (Connection Con = getConnection();
             PreparedStatement PS = Con
                     .prepareStatement("INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES (?, ?, ?)");
             PreparedStatement PS1 = Con.prepareStatement("INSERT INTO PARAMS (OBJECT_ID, ATTR_ID, VALUE) VALUES (?, ?, ?)");) {

            // 1. Подготавливаем и заполняем в базе строку таблицы OBJECTS


            System.out.println(">> Новый объект-лог с id =" + id);
            PS.setInt(1, id);
            PS.setInt(2, dataObject.getObjectTypeId());
            PS.setString(3, dataObject.getName());
            PS.executeUpdate();

            // 2. Подготавливаем и заполняем в базе новые строки таблицы PARAMS
            // Получаем список параметров:

            while (!dataObject.getParams().isEmpty()) {
                Map.Entry<Integer, String> En = dataObject.getParams().pollFirstEntry();
                PS1.setInt(1, id);
                PS1.setInt(2, En.getKey());
                PS1.setString(3, En.getValue());
                PS1.addBatch();
                //System.out.println("param: id = " + id + ", attr_id = " + En.getKey() + ", ref = " + En.getValue());
            }
            PS1.executeBatch();

            // 3. Подготавливаем и заполняем в базе новые строки таблицы REFERENCES
            // Получаем список параметров:
            Integer host_id = idUser;
            try (PreparedStatement PS2 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?, ?, ?)");) {

                while (!dataObject.getRefParams().isEmpty()) {
                    Map.Entry<Integer, ArrayList<Integer>> En = dataObject.getRefParams().pollFirstEntry();
                    ArrayList<Integer> valueList = En.getValue(); // получаем значение
                    for (Integer value : valueList) {
                        PS2.setInt(1, id);
                        PS2.setInt(2, En.getKey());
                        PS2.setInt(3, value);
                        PS2.addBatch();
                        // System.out.println("reference: id = " + id + ", attr_id = " + En.getKey() + ", par = " + value);
                        if (En.getKey().equals(141)) {
                            host_id = value;
                        }
                    }
                }
                PS2.executeBatch();
            }

            System.out.println("host_id = " + host_id);


            // 1) Добавление ссылки Юзер - Лог (связывание): INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES ('10001', '31', '30001');
            int attrId = 31;
            try (PreparedStatement PS7 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");) {
                PS7.setInt(1, idUser);
                PS7.setInt(2, attrId);
                PS7.setInt(3, id);
                PS7.executeQuery();
            }

        }

    }

    /*...............................................................................................................*/
    // 2017-02-18 Новый метод обновления датаобджекта в базе:
    public int updateDataObject(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        int id = 0;
        try (Connection Con = getConnection();) {

            // 1. Подгружаем из базы текущее состояние DO:
            id = dataObject.getId();
            DataObject dataObjectOld = getObjectsByIdAlternative(id);

            // 2. Подготавливаем и заполняем (если соотвествующие поля в базе и в памяти отличаются) в базе строку таблицы OBJECTS
            if (!dataObjectOld.getName().equals(dataObject.getName())) { // Если имена различны, то обновляем имя
                try (PreparedStatement PS = Con.prepareStatement("UPDATE OBJECTS SET OBJECT_NAME = ? WHERE OBJECT_ID = ?");) {
                    PS.setString(1, dataObject.getName());
                    PS.setInt(2, id);
                    PS.executeUpdate();
                }
            }

            // 3. Подготавливаем и заполняем (если соотвествующие поля в базе и в памяти отличаются) в базе строки таблицы PARAMS
            // Получаем список параметров:
            TreeMap<Integer, String> paramsOld = dataObjectOld.getParams();
            TreeMap<Integer, String> params = dataObject.getParams();

            try (PreparedStatement PS_upd = Con.prepareStatement("UPDATE PARAMS SET VALUE = ? WHERE OBJECT_ID = ? AND ATTR_ID = ?");) {


                // Обходим все параметры в листе в новом датаобджекте
                for (Map.Entry<Integer, String> entry : params.entrySet()) {
                    Integer key = entry.getKey(); // получаем ключ
                    String value = entry.getValue(); // получаем значение
                    String valueOld = paramsOld.get(key);
                    System.out.println("Старое значение ключа " + key + " = " + valueOld + ", новое значение ключа = " + value);

                    PS_upd.setString(1, value);
                    PS_upd.setInt(2, id);
                    PS_upd.setInt(3, key);
                    PS_upd.addBatch();

                }
                PS_upd.executeBatch();
            }

            // 4. Подготавливаем и заполняем (если соотвествующие поля в базе и в памяти отличаются) в базе строки таблицы REFERENCES
            // Получаем список параметров:
            TreeMap<Integer, ArrayList<Integer>> referencesOld = dataObjectOld.getRefParams();
            TreeMap<Integer, ArrayList<Integer>> references = dataObject.getRefParams();
            try (PreparedStatement PS_ref_ins = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?, ?, ?)");
                 PreparedStatement PS_ref_del = Con.prepareStatement("DELETE FROM REFERENCES WHERE OBJECT_ID = ? AND ATTR_ID = ? AND REFERENCE = ?");) {

                //DELETE FROM REFERENCES WHERE OBJECT_ID = 10003 AND ATTR_ID = 12 AND REFERENCE = 10001
                //PreparedStatement PS_ref_del2 = Con.prepareStatement("DELETE FROM REFERENCES WHERE OBJECT_ID = ? AND ATTR_ID = ?");

                // Обходим все параметры в листе в новом датаобджекте
                for (Map.Entry<Integer, ArrayList<Integer>> entry : references.entrySet()) {
                    Integer key = entry.getKey(); // получаем ключ
                    ArrayList<Integer> valueList = entry.getValue(); // получаем значение
                    ArrayList<Integer> valueListOld = referencesOld.get(key);

                    for (Integer value : valueList) {
                        // Если в старом объекте нет такого ключа или если есть ключ, но нет такого значения в старом датаобджекте, то надо создать новую строку
                        if ((valueListOld == null) || (!valueListOld.contains(value))) {
                            PS_ref_ins.setInt(1, id);
                            PS_ref_ins.setInt(2, key);
                            PS_ref_ins.setInt(3, value);
                            PS_ref_ins.addBatch();
                        } // иначе не трогаем
                    }
                }
                PS_ref_ins.executeBatch();

                // А теперь смотрим, может, нужно какие-то ссылки удалить. Обходим все ссылки в листе в старом датаобджекте
                for (Map.Entry<Integer, ArrayList<Integer>> entry : referencesOld.entrySet()) {
                    Integer keyOld = entry.getKey(); // получаем ключ
                    ArrayList<Integer> valueListOld = entry.getValue(); // получаем значение
                    ArrayList<Integer> valueList = references.get(keyOld);

                    if (valueList == null) { // если вообще нет такого ключа
                        //PS_ref_del2.setInt(1, id);
                        //PS_ref_del2.setInt(2, keyOld);
                        //PS_ref_del2.addBatch();
                    } else { // Если ключ есть, сравниваем значения
                        for (Integer valueOld : valueListOld) {
                            // Если в новом объекте нет такого значения, то надо удалить строку из базы
                            if (!valueList.contains(valueOld)) {
                                PS_ref_del.setInt(1, id);
                                PS_ref_del.setInt(2, keyOld);
                                PS_ref_del.setObject(3, valueOld);
                                PS_ref_del.addBatch();
                                System.out.println("Удаляем ссылку " + id + " : " + keyOld + " : " + valueOld);
                            } // иначе не трогаем
                        }
                        PS_ref_del.executeBatch();

                    }
                }

            }
            //PS_ref_del2.executeBatch();
            //PS_ref_del2.close();

        }
        return id;
    }

    /*...............................................................................................................*/
    // 2017-02-18 Новый универсальный метод удаления датаобджекта из базы:
    public void deleteDataObject(Integer id) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {
        try (Connection Con = getConnection();) {

            PreparedStatement PS_del = Con.prepareStatement("DELETE FROM PARAMS WHERE OBJECT_ID = ?");
            PS_del.setInt(1, id);
            PS_del.executeUpdate();

            PS_del = Con.prepareStatement("DELETE FROM REFERENCES WHERE OBJECT_ID = ?");
            PS_del.setInt(1, id);
            PS_del.executeUpdate();

            PS_del = Con.prepareStatement("DELETE FROM REFERENCES WHERE REFERENCE = ?");
            PS_del.setInt(1, id);
            PS_del.executeUpdate();

            PS_del = Con.prepareStatement("DELETE FROM OBJECTS WHERE OBJECT_ID = ?");
            PS_del.setInt(1, id);
            PS_del.executeUpdate();

            PS_del.close();

        }
    }

    // На всякий случай удаление по объекту, а не по айди
    public void deleteDataObject(DataObject dataObject) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {
        Integer id = dataObject.getId();
        deleteDataObject(id);
    }

    /*...............................................................................................................*/
    // 2017-02-19 Новый универсальный метод частичной загрузки датаобджектов из базы с использованием Partitions-фильтров:
    public ArrayList<DataObject> getPartitionsDataObjectsList(ArrayList<Integer> objectIds, BasePartitionFilter filter) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ArrayList<DataObject> partitionDataObjectList = new ArrayList<>();
        if (objectIds.size() > 0) { // Если список айдишников не пустой

            TreeMap<String, ArrayList<String>> params = filter.getParams();

            // Формируем вставку в запрос вида IN (10001, 10002, 10003) из переданного списка айдишников
            String set = "IN (";
            for (int i = 0; i < objectIds.size() - 1; i++) { // кроме последнего элемента
                set += objectIds.get(i) + ", ";
            }
            set += objectIds.get(objectIds.size() - 1) + ")";
            // Подготавливаем начало запроса:
            String sql = "SELECT * FROM((SELECT -2 AS KEY, CAST(OBJECT_ID AS VARCHAR(70)) AS VALUE, 0 AS REF, OBJECT_ID FROM OBJECTS WHERE OBJECT_ID " + set + ") " +
                    "UNION (SELECT -1, OBJECT_NAME, 0, OBJECT_ID FROM OBJECTS WHERE OBJECT_ID " + set + ") " +
                    "UNION (SELECT 0, CAST(OBJECT_TYPE_ID AS VARCHAR(70)), 0, OBJECT_ID FROM OBJECTS WHERE OBJECT_ID " + set + ") ";

            // в зависимости от типа фильтра
            // 1. Для пользователей
            if (filter instanceof UserPartition) {
                //  начинаем вытаскивать параметры и формировать строку запроса:
                // Для основных фильтров:
                if (params.get(UserPartition.FULL) != null) { // если надо получить объекты целиком,
                    sql += "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) AS VALUE_LIST, 0, OBJECT_ID FROM PARAMS pa " +
                            "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13) " +
                            "UNION (SELECT ATTR_ID, CAST(REFERENCE AS VARCHAR(70)), 1, OBJECT_ID FROM REFERENCES WHERE OBJECT_ID " + set + ")) ORDER BY OBJECT_ID, KEY";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(UserPartition.LITE) != null) { // если надо получить только заголовки для объектов (айди, имя, тип),
                    sql += ") ";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(UserPartition.WITH_ALL_PARAMS) != null) { // если надо получить объекты только с заголовком и всеми параметрами,
                    sql += "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) AS VALUE_LIST, 0, OBJECT_ID FROM PARAMS pa " +
                            "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13) ";
                    sql += ") ";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(UserPartition.WITH_ALL_REFERENCES) != null) { // если надо получить объекты только с заголовком и всеми ссылками,
                    sql += "UNION (SELECT ATTR_ID, CAST(REFERENCE AS VARCHAR(70)), 1, OBJECT_ID FROM REFERENCES WHERE OBJECT_ID " + set + ")) ORDER BY OBJECT_ID, KEY";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(UserPartition.WITH_PARAMS_OR_REFERENCES_LIST) != null) { // если надо получить объекты с определенными параметрами или ссылками
                    // 1). Формируем вставку в запрос вида IN (AND ATTR_ID_1, AND ATTR_ID_2, AND ATTR_ID_3) в зависимости от набора аргументов
                    String setParams = "IN (";

                    // И проверяем аргумента параметра фильтра
                    ArrayList<String> name = params.get(UserPartition.NAME);
                    if (name != null) {
                        setParams += "1, ";
                    }
                    ArrayList<String> surname = params.get(UserPartition.SURNAME);
                    if (surname != null) {
                        setParams += "2, ";
                    }
                    ArrayList<String> middlename = params.get(UserPartition.MIDDLENAME);
                    if (middlename != null) {
                        setParams += "3, ";
                    }
                    ArrayList<String> agedata = params.get(UserPartition.AGEDATA);
                    if (agedata != null) {
                        setParams += "5, ";
                    }
                    ArrayList<String> email = params.get(UserPartition.EMAIL);
                    if (email != null) {
                        setParams += "6, ";
                    }
                    ArrayList<String> password = params.get(UserPartition.PASSWORD);
                    if (password != null) {
                        setParams += "7, ";
                    }
                    ArrayList<String> sex = params.get(UserPartition.SEX);
                    if (sex != null) {
                        setParams += "8, ";
                    }
                    ArrayList<String> city = params.get(UserPartition.CITY);
                    if (city != null) {
                        setParams += "9, ";
                    }
                    ArrayList<String> additional = params.get(UserPartition.ADDITIONAL);
                    if (additional != null) {
                        setParams += "10, ";
                    }
                    ArrayList<String> avatar = params.get(UserPartition.AVATAR);
                    if (avatar != null) {
                        setParams += "11, ";
                    }
                    setParams += "0)"; //  в конце дописываем форматирующий ноль, чтобы не получилось ", )", а получилось ", 0)"
                    // и дописываем запрос
                    if (setParams.length() > "IN (0)".length()) { // Если положили хоть один параметр, то ставим условие в запрос
                        sql += "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) AS VALUE_LIST, 0, OBJECT_ID FROM PARAMS pa " +
                                "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13 ";
                        sql += "AND ATTR_ID " + setParams + ")";
                    }

                    // 2). Формируем вставку в запрос вида IN (AND ATTR_ID_1, AND ATTR_ID_2, AND ATTR_ID_3) в зависимости от набора аргументов
                    String setReferences = "IN (";

                    // И проверяем аргумента параметра-ссылки фильтра
                    ArrayList<String> friends = params.get(UserPartition.FRIENDS);
                    if (friends != null) {
                        setReferences += "12, ";
                    }
                    ArrayList<String> messages = params.get(UserPartition.MESSAGES);
                    if (messages != null) {
                        setReferences += "13, ";
                    }
                    ArrayList<String> events = params.get(UserPartition.EVENTS);
                    if (events != null) {
                        setReferences += "30, ";
                    }
                    setReferences += "0)"; //  в конце дописываем форматирующий ноль, чтобы не получилось ", )", а получилось ", 0)"
                    // и дописываем запрос
                    if (setReferences.length() > "IN (0)".length()) { // Если положили хоть один параметр-ссылку, то ставим условие в запрос
                        sql += "UNION (SELECT ATTR_ID, CAST(REFERENCE AS VARCHAR(70)), 1, OBJECT_ID FROM REFERENCES WHERE OBJECT_ID " + set;
                        sql += " AND ATTR_ID " + setReferences + ")";
                        sql += ") ORDER BY OBJECT_ID, KEY";
                    } else {
                        sql += ") "; // Закрывающая скобка
                    }
                    System.out.println("Формирую запрос " + sql);
                } else {
                    System.out.println("Частичный фильтр задан неверно. Частичная загрузка отменена");
                    return null;
                }
            }
            // 2. Для событий
            if (filter instanceof EventPartition) {
                //  начинаем вытаскивать параметры и формировать строку запроса:
                // Для основных фильтров:
                if (params.get(EventPartition.FULL) != null) { // если надо получить объекты целиком,
                    sql += "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) AS VALUE_LIST, 0, OBJECT_ID FROM PARAMS pa " +
                            "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13) " +
                            "UNION (SELECT ATTR_ID, CAST(REFERENCE AS VARCHAR(70)), 1, OBJECT_ID FROM REFERENCES WHERE OBJECT_ID " + set + ")) ORDER BY OBJECT_ID, KEY";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(EventPartition.LITE) != null) { // если надо получить только заголовки для объектов (айди, имя, тип),
                    sql += ") ";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(EventPartition.WITH_ALL_PARAMS) != null) { // если надо получить объекты только с заголовком и всеми параметрами,
                    sql += "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) AS VALUE_LIST, 0, OBJECT_ID FROM PARAMS pa " +
                            "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13) ";
                    sql += ") ";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(EventPartition.WITH_PARAMS_LIST) != null) { // если надо получить объекты с определенными параметрами
                    // 1). Формируем вставку в запрос вида IN (AND ATTR_ID_1, AND ATTR_ID_2, AND ATTR_ID_3) в зависимости от набора аргументов
                    String setParams = "IN (";

                    // И проверяем аргумента параметра фильтра
                    ArrayList<String> date_begin = params.get(EventPartition.DATE_BEGIN);
                    if (date_begin != null) {
                        setParams += "101, ";
                    }
                    ArrayList<String> date_end = params.get(EventPartition.DATE_END);
                    if (date_end != null) {
                        setParams += "102, ";
                    }
                    ArrayList<String> duration = params.get(EventPartition.DURATION);
                    if (duration != null) {
                        setParams += "103, ";
                    }
                    ArrayList<String> info = params.get(EventPartition.INFO);
                    if (info != null) {
                        setParams += "104, ";
                    }
                    ArrayList<String> priority = params.get(EventPartition.PRIORITY);
                    if (priority != null) {
                        setParams += "105, ";
                    }

                    setParams += "0)"; //  в конце дописываем форматирующий ноль, чтобы не получилось ", )", а получилось ", 0)"
                    // и дописываем запрос
                    if (setParams.length() > "IN (0)".length()) { // Если положили хоть один параметр, то ставим условие в запрос
                        sql += "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) AS VALUE_LIST, 0, OBJECT_ID FROM PARAMS pa " +
                                "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13 ";
                        sql += "AND ATTR_ID " + setParams + ")";
                    }

                    // 2017-02-28 Ссылка на Юзера-Создателя:
                    String setReferences = "IN (";
                    ArrayList<String> host_id = params.get(EventPartition.HOST_ID);
                    if (host_id != null) {
                        setReferences += "141, ";
                    }

                    setReferences += "0)"; //  в конце дописываем форматирующий ноль, чтобы не получилось ", )", а получилось ", 0)"
                    // и дописываем запрос
                    if (setReferences.length() > "IN (0)".length()) { // Если положили хоть один параметр, то ставим условие в запрос
                        sql += "UNION (SELECT ATTR_ID, listagg(REFERENCE, '~') WITHIN GROUP(ORDER BY rf.ATTR_ID) over(PARTITION BY REFERENCE) AS REFERENCE_LIST, 0, OBJECT_ID FROM REFERENCES rf " +
                                "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13 ";
                        sql += "AND ATTR_ID " + setReferences + ")";
                    }
                    //

                    sql += ") "; // Закрывающая скобка

                    System.out.println("Формирую запрос " + sql);
                } else {
                    System.out.println("Частичный фильтр задан неверно. Частичная загрузка отменена");
                    return null;
                }
            }
            // 3. Для сообщений
            if (filter instanceof MessagePartition) {
                //  начинаем вытаскивать параметры и формировать строку запроса:
                // Для основных фильтров:
                if (params.get(MessagePartition.FULL) != null) { // если надо получить объекты целиком,
                    sql += "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) AS VALUE_LIST, 0, OBJECT_ID FROM PARAMS pa " +
                            "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13) " +
                            "UNION (SELECT ATTR_ID, CAST(REFERENCE AS VARCHAR(70)), 1, OBJECT_ID FROM REFERENCES WHERE OBJECT_ID " + set + ")) ORDER BY OBJECT_ID, KEY";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(MessagePartition.LITE) != null) { // если надо получить только заголовки для объектов (айди, имя, тип),
                    sql += ") ";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(MessagePartition.WITH_ALL_PARAMS) != null) { // если надо получить объекты только с заголовком и всеми параметрами,
                    sql += "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) AS VALUE_LIST, 0, OBJECT_ID FROM PARAMS pa " +
                            "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13) ";
                    sql += ") ";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(MessagePartition.WITH_PARAMS_LIST) != null) { // если надо получить объекты с определенными параметрами
                    // 1). Формируем вставку в запрос вида IN (AND ATTR_ID_1, AND ATTR_ID_2, AND ATTR_ID_3) в зависимости от набора аргументов
                    String setParams = "IN (";

                    // И проверяем аргумента параметра фильтра
                    ArrayList<String> from_id = params.get(MessagePartition.FROM_ID);
                    if (from_id != null) {
                        setParams += "201, ";
                    }
                    ArrayList<String> to_id = params.get(MessagePartition.TO_ID);
                    if (to_id != null) {
                        setParams += "202, ";
                    }
                    ArrayList<String> date_send = params.get(MessagePartition.DATE_SEND);
                    if (date_send != null) {
                        setParams += "203, ";
                    }
                    ArrayList<String> read_status = params.get(MessagePartition.READ_STATUS);
                    if (read_status != null) {
                        setParams += "204, ";
                    }
                    ArrayList<String> text = params.get(MessagePartition.TEXT);
                    if (text != null) {
                        setParams += "205, ";
                    }
                    ArrayList<String> from_name = params.get(MessagePartition.FROM_NAME);
                    if (from_name != null) {
                        setParams += "206, ";
                    }
                    ArrayList<String> to_name = params.get(MessagePartition.TO_NAME);
                    if (to_name != null) {
                        setParams += "207, ";
                    }

                    setParams += "0)"; //  в конце дописываем форматирующий ноль, чтобы не получилось ", )", а получилось ", 0)"
                    // и дописываем запрос
                    if (setParams.length() > "IN (0)".length()) { // Если положили хоть один параметр, то ставим условие в запрос
                        sql += "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) AS VALUE_LIST, 0, OBJECT_ID FROM PARAMS pa " +
                                "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13 ";
                        sql += "AND ATTR_ID " + setParams + ")";
                    }
                    sql += ") "; // Закрывающая скобка

                    System.out.println("Формирую запрос " + sql);
                } else {
                    System.out.println("Частичный фильтр задан неверно. Частичная загрузка отменена");
                    return null;
                }
            }
            // 4. Для встреч
            if (filter instanceof MeetingPartition) {
                //  начинаем вытаскивать параметры и формировать строку запроса:
                // Для основных фильтров:
                if (params.get(MeetingPartition.FULL) != null) { // если надо получить объекты целиком,
                    sql += "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) AS VALUE_LIST, 0, OBJECT_ID FROM PARAMS pa " +
                            "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13 AND ATTR_ID != 307) " +
                            "UNION (SELECT ATTR_ID, CAST(REFERENCE AS VARCHAR(70)), 1, OBJECT_ID FROM REFERENCES WHERE OBJECT_ID " + set + ")) ORDER BY OBJECT_ID, KEY";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(MeetingPartition.LITE) != null) { // если надо получить только заголовки для объектов (айди, имя, тип),
                    sql += ") ";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(MeetingPartition.WITH_ALL_PARAMS) != null) { // если надо получить объекты только с заголовком и всеми параметрами,
                    sql += "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) AS VALUE_LIST, 0, OBJECT_ID FROM PARAMS pa " +
                            "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13 AND ATTR_ID != 307) ";
                    sql += ") ";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(MeetingPartition.WITH_ALL_REFERENCES) != null) { // если надо получить объекты только с заголовком и всеми ссылками,
                    sql += "UNION (SELECT ATTR_ID, CAST(REFERENCE AS VARCHAR(70)), 1, OBJECT_ID FROM REFERENCES WHERE OBJECT_ID " + set + ")) ORDER BY OBJECT_ID, KEY";
                    System.out.println("Формирую запрос " + sql);
                } else if (params.get(MeetingPartition.WITH_PARAMS_OR_REFERENCES_LIST) != null) { // если надо получить объекты с определенными параметрами или ссылками
                    // 1). Формируем вставку в запрос вида IN (AND ATTR_ID_1, AND ATTR_ID_2, AND ATTR_ID_3) в зависимости от набора аргументов
                    String setParams = "IN (";

                    // И проверяем аргумента параметра фильтра
                    ArrayList<String> title = params.get(MeetingPartition.TITLE);
                    if (title != null) {
                        setParams += "301, ";
                    }
                    ArrayList<String> date_start = params.get(MeetingPartition.DATE_START);
                    if (date_start != null) {
                        setParams += "302, ";
                    }
                    ArrayList<String> date_end = params.get(MeetingPartition.DATE_END);
                    if (date_end != null) {
                        setParams += "303, ";
                    }
                    ArrayList<String> info = params.get(MeetingPartition.INFO);
                    if (info != null) {
                        setParams += "304, ";
                    }
                    ArrayList<String> organizer = params.get(MeetingPartition.ORGANIZER);
                    if (organizer != null) {
                        setParams += "305, ";
                    }
                    ArrayList<String> tag = params.get(MeetingPartition.TAG);
                    if (tag != null) {
                        setParams += "306, ";
                    }

                    setParams += "0)"; //  в конце дописываем форматирующий ноль, чтобы не получилось ", )", а получилось ", 0)"
                    // и дописываем запрос
                    if (setParams.length() > "IN (0)".length()) { // Если положили хоть один параметр, то ставим условие в запрос
                        sql += "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) AS VALUE_LIST, 0, OBJECT_ID FROM PARAMS pa " +
                                "WHERE OBJECT_ID " + set + " AND ATTR_ID != 12 AND ATTR_ID != 13 AND ATTR_ID != 307 ";
                        sql += "AND ATTR_ID " + setParams + ")";
                    }

                    // 2). Формируем вставку в запрос вида IN (AND ATTR_ID_1, AND ATTR_ID_2, AND ATTR_ID_3) в зависимости от набора аргументов
                    String setReferences = "IN (";

                    // И проверяем аргумента параметра-ссылки фильтра
                    ArrayList<String> members = params.get(MeetingPartition.MEMBERS);
                    if (members != null) {
                        setReferences += "307, ";
                    }
                    setReferences += "0)"; //  в конце дописываем форматирующий ноль, чтобы не получилось ", )", а получилось ", 0)"
                    // и дописываем запрос
                    if (setReferences.length() > "IN (0)".length()) { // Если положили хоть один параметр-ссылку, то ставим условие в запрос
                        sql += "UNION (SELECT ATTR_ID, CAST(REFERENCE AS VARCHAR(70)), 1, OBJECT_ID FROM REFERENCES WHERE OBJECT_ID " + set;
                        sql += " AND ATTR_ID " + setReferences + ")";
                        sql += ") ORDER BY OBJECT_ID, KEY ";
                    } else {
                        sql += ") "; // Закрывающая скобка
                    }
                    System.out.println("Формирую запрос " + sql);
                } else {
                    System.out.println("Частичный фильтр задан неверно. Частичная загрузка отменена");
                    return null;
                }
            }

            try (Connection Con = getConnection();
                 PreparedStatement PS = Con.prepareStatement(sql);
                 ResultSet RS = PS.executeQuery();) {

                // Обходим всю полученную таблицу и формируем поля датаобджектов
                DataObject partitionDataObject = null;
                while (RS.next()) {
                    Integer key = RS.getInt(1); // key
                    String value = RS.getString(2); // value
                    // Удаление дублирования строк (Вася Вася Вася):
                    value = (((value != null) && (value.indexOf('~') > 0)) ? value.substring(0, value.indexOf('~')) : value);
                    Integer ref = RS.getInt(3); // ref (reference flag, 0 - not ref, 1 - ref)
                    // Integer id = RS.getInt(4); // object id
                    if (key == -2) { // Это пришел к нам айдишник
                        if (partitionDataObject != null) {
                            partitionDataObjectList.add(partitionDataObject); // кладем предыдущий объект в лист
                        }
                        partitionDataObject = new DataObject(); // создаем новый, и будем теперь в него писать
                        partitionDataObject.setId(Integer.parseInt(value));
                    } else if (key == -1) { // Это пришло к нам имя
                        partitionDataObject.setName(value);
                    } else if (key == 0) { // Это пришел к нам тип
                        partitionDataObject.setObjectTypeId(Integer.parseInt(value));
                    } else { // Иначе пришли параматры или ссылки
                        if (ref == 0) { // Значит, это пришли параметры
                            partitionDataObject.setParams(key, value);
                        } else { // Иначе пришли ссылки
                            partitionDataObject.setRefParams(key, Integer.parseInt(value));
                        }
                    }
                }
                // и в конце надо дописать последний элемент, который из-за while не занесся в лист:
                if (partitionDataObject != null) { // если успели прочитать поля в объект, то есть он не просто пустая заготовка
                    partitionDataObjectList.add(partitionDataObject); // кладем объект в лист
                }
            }
        }

        return partitionDataObjectList;
    }

    // 2017-02-26 Новый метод выставвления флагов о прочтении для сообщений (по списку их айди) в базе:
    public void updateMessageReadStatus(ArrayList<Integer> ids) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // Подготавливаем запрос на выставление флагов о прочтении

        String sql = "UPDATE PARAMS SET VALUE = '1' WHERE ATTR_ID = 204 AND OBJECT_ID IN (";
        for (Integer id : ids) {
            sql += id + ", ";
        }
        sql += "0)";

        try (Connection Con = getConnection();
             PreparedStatement PS = Con.prepareStatement(sql);
             ResultSet RS = PS.executeQuery();) {
        }

    }
    //endregion


}
