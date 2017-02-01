package dbHelp;

import entities.Event;
import entities.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.persistence.TypedQuery;
import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.*;

/**
 * Created by Lawrence on 14.01.2017.
 */

public class DBHelp {

    private static final Logger logger = LoggerFactory.getLogger(DBHelp.class);

    private static Connection getConnection() throws SQLException {
        Locale.setDefault(Locale.ENGLISH);
        try {
            InitialContext initContext = new InitialContext();
            Context envContext = (Context)initContext.lookup("java:comp/env");
            DataSource ds = (DataSource)envContext.lookup("jdbc/myoracle");
            return ds.getConnection();
        } catch (NamingException e) {
            e.printStackTrace();
        }
        return null;
    }


    private static void CloseConnection(Connection Con) throws SQLException {

        Con.close();
    }


    public int getObjID(String username) throws SQLException
    {
        Connection Con = getConnection();
        PreparedStatement PS = Con
                .prepareStatement("SELECT OBJECT_ID FROM PARAMS WHERE VALUE = ?");
        PS.setString(1, username);
        ResultSet RS = PS.executeQuery();
        int objID = 0;
        while(RS.next())
        {
            objID =  RS.getInt(1);
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return objID;
    }


    public ArrayList<String> getObjParamsByObjID(int objID) throws SQLException
    {
        ArrayList<String> objParams = new ArrayList<>();
        Connection Con = getConnection();
        PreparedStatement PS = Con
                .prepareStatement("SELECT NVL(VALUE, ' ') FROM PARAMS WHERE OBJECT_ID = ? and ATTR_ID BETWEEN 1 and 10");
        PS.setInt(1, objID);
        ResultSet RS = PS.executeQuery();
        while (RS.next()) {
            objParams.add(RS.getString(1));
        }
        logger.info("size = " + objParams.size());
        RS.close();
        PS.close();
        CloseConnection(Con);
        return objParams;
    }

    public ArrayList<Object> getEventParamsByObjID(int eventID) throws SQLException
    {
        ArrayList<Object> eventParams = new ArrayList<>();
        Connection Con = getConnection();
        PreparedStatement PS = Con.prepareStatement("SELECT NVL(VALUE, ' ') FROM PARAMS WHERE OBJECT_ID = ? and ATTR_ID BETWEEN 101 and 105");
        PS.setInt(1, eventID);
        ResultSet RS = PS.executeQuery();
        while (RS.next()) {
            eventParams.add(RS.getString(1));
        }
        logger.info("size = " + eventParams.size());
        RS.close();
        PS.close();
        CloseConnection(Con);
        return eventParams;
    }



    // Получение всех пользователей
    public ArrayList<Object> getObjectsIDbyObjectTypeID(int ObjectTypeID)
            throws SQLException {
        ArrayList<Object> Res = new ArrayList<>();
        Connection Con = getConnection();
        PreparedStatement PS = Con
                .prepareStatement("SELECT OBJECT_NAME FROM OBJECTS WHERE OBJECT_TYPE_ID = ?");
        PS.setInt(1, ObjectTypeID);
        ResultSet RS = PS.executeQuery();
        while (RS.next()) {
            Res.add(RS.getObject(1));
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return Res;
    }

    // Получение всех пользователей
    public ArrayList<User> getUserList() throws SQLException {
        ArrayList<User> Res = new ArrayList<>();
        Connection Con = getConnection();
        Integer userTypeID = 1001; // ID типа Пользователь
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
                        "WHERE ob.OBJECT_TYPE_ID = ? ORDER BY ob.OBJECT_ID");
        PS.setInt(1, userTypeID); // В качестве параметра id типа Пользователь
        ResultSet RS = PS.executeQuery(); // System.out.println(RS);
        while (RS.next()) {
            User user = new User();
            user.setId(RS.getInt(1));
            user.setName(RS.getString(2));
            user.setSurname(RS.getString(3));
            user.setMiddleName(RS.getString(4));
            user.setLogin(RS.getString(5));
            user.setAgeDate(RS.getString(6));
            user.setEmail(RS.getString(7));
            user.setPassword(RS.getString(8));
            user.setSex(RS.getString(9));
            user.setCountry(RS.getString(10));
            user.setAdditional_field(RS.getString(11));
            Res.add(user);
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return Res;
    }

    // Получение объекта текущего авторизованного пользователя
    public User getCurrentUser() throws SQLException {
        Integer userID = new DBHelp().getObjID(new UserService().getCurrentUsername());
        User user = getUserByUserID(userID);
        return user;
    }

    // Получение объекта одного конкретного пользователя по id этого пользователя
    public User getUserByUserID(int userID) throws SQLException {
        Connection Con = getConnection();
        Integer userTypeID = 1001; // ID типа Пользователь
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
            user.setCountry(RS.getString(10));
            user.setAdditional_field(RS.getString(11));
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return user;
    }

    // Получение ВСЕХ событий данного пользователя
    public ArrayList<Event> getEventsIDbyObjectID(int ObjectID) throws SQLException {
        ArrayList<Event> Res = new ArrayList<>();
        Connection Con = getConnection();
        // PreparedStatement PS = Con.prepareStatement("SELECT OBJECT_NAME FROM OBJECTS WHERE OBJECT_TYPE_ID = ?");

        PreparedStatement PS = Con.prepareStatement("SELECT ev.OBJECT_ID, ob.OBJECT_ID, ev.OBJECT_NAME," +
                "pa2.VALUE, pa3.VALUE, pa4.VALUE, pa5.VALUE FROM OBJECTS ob LEFT JOIN REFERENCES re " +
                "ON ob.OBJECT_ID = re.OBJECT_ID LEFT JOIN OBJECTS ev  ON re.REFERENCE = ev.OBJECT_ID " +
                "LEFT JOIN PARAMS pa1 ON ev.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 103 LEFT JOIN PARAMS pa2 " +
                "ON ev.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 101   LEFT JOIN PARAMS pa3 " +
                "ON ev.OBJECT_ID = pa3.OBJECT_ID AND  pa3.ATTR_ID = 102 LEFT JOIN PARAMS pa4 " +
                "ON ev.OBJECT_ID = pa4.OBJECT_ID AND pa4.ATTR_ID = 105 LEFT JOIN PARAMS pa5 " +
                "ON ev.OBJECT_ID = pa5.OBJECT_ID AND pa5.ATTR_ID = 104 WHERE ob.OBJECT_ID = ? ORDER BY ev.OBJECT_ID");
        PS.setInt(1, ObjectID); // В качестве параметра id пользователя
        ResultSet RS = PS.executeQuery(); // System.out.println(RS);
        while (RS.next()) {
            Event event = new Event();
            event.setId( RS.getInt(1));
            event.setHost_id( RS.getInt(2));
            event.setName( RS.getString(3));
            event.setDate_begin( RS.getString(4));
            event.setDate_end( RS.getString(5));
            event.setPriority( RS.getInt(6));
            event.setInfo( RS.getString(7));

            Res.add(event); // Res.add(RS.getObject(1));
            //Res.add(RS.getObject(2));
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return Res;
    }

    // Получение одного конкретного события данного пользователя по id этого события
    public Event getEventByEventID(int EventID) throws SQLException {
        Connection Con = getConnection();

        Integer idUser = new DBHelp().getObjID(new UserService().getCurrentUsername());

        PreparedStatement PS = Con.prepareStatement("SELECT ev.OBJECT_ID, ob.OBJECT_ID, ev.OBJECT_NAME," +
                "pa2.VALUE, pa3.VALUE, pa4.VALUE, pa5.VALUE FROM OBJECTS ob LEFT JOIN REFERENCES re " +
                "ON ob.OBJECT_ID = re.OBJECT_ID LEFT JOIN OBJECTS ev  ON re.REFERENCE = ev.OBJECT_ID " +
                "LEFT JOIN PARAMS pa1 ON ev.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 103 LEFT JOIN PARAMS pa2 " +
                "ON ev.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 101   LEFT JOIN PARAMS pa3 " +
                "ON ev.OBJECT_ID = pa3.OBJECT_ID AND  pa3.ATTR_ID = 102 LEFT JOIN PARAMS pa4 " +
                "ON ev.OBJECT_ID = pa4.OBJECT_ID AND pa4.ATTR_ID = 105 LEFT JOIN PARAMS pa5 " +
                "ON ev.OBJECT_ID = pa5.OBJECT_ID AND pa5.ATTR_ID = 104 WHERE ob.OBJECT_ID = ? AND ev.OBJECT_ID = ? ORDER BY ev.OBJECT_ID");
        PS.setInt(1, idUser); // В качестве параметра id пользователя
        PS.setInt(2, EventID); // В качестве параметра id пользователя
        ResultSet RS = PS.executeQuery(); // System.out.println(RS);
        Event event = null;
        while (RS.next()) {
            event = new Event();
            event.setId(RS.getInt(1));
            event.setHost_id(RS.getInt(2));
            event.setName(RS.getString(3));
            event.setDate_begin(RS.getString(4));
            event.setDate_end(RS.getString(5));
            event.setPriority(RS.getInt(6));
            event.setInfo(RS.getString(7));
        }


        RS.close();
        PS.close();
        CloseConnection(Con);
        return event;
    }

    /*
        public ArrayList<Object> getEventsIDbyObjectID(int ObjectID) throws SQLException {
        ArrayList<Object> Res = new ArrayList<>();
        Connection Con = getConnection();
        // PreparedStatement PS = Con.prepareStatement("SELECT OBJECT_NAME FROM OBJECTS WHERE OBJECT_TYPE_ID = ?");

        PreparedStatement PS = Con.prepareStatement("SELECT ev.OBJECT_ID, NVL(ev.OBJECT_NAME, 'Нет событий') " +
                "AS EVENT FROM OBJECTS ob LEFT JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID " +
                "LEFT JOIN OBJECTS ev ON re.REFERENCE = ev.OBJECT_ID WHERE ob.OBJECT_ID =  ?");
        PS.setInt(1, ObjectID); // В качестве параметра id пользователя
        ResultSet RS = PS.executeQuery(); System.out.println(RS);
        while (RS.next()) {
            ArrayList<Object> arrayRS = new ArrayList<>();
            Event event = new Event();
            event.
            arrayRS.add(RS.getObject(1));
            arrayRS.add(RS.getObject(2));
            Res.add(arrayRS); // Res.add(RS.getObject(1));
            //Res.add(RS.getObject(2));
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return Res;
    }

     */

    public int getAttrID(int ObjID, int ObjRefID) throws SQLException
    {
        Connection Con = getConnection();
        PreparedStatement PS = Con
                .prepareStatement("SELECT ATTR_ID FROM REFERENCES WHERE OBJECT_ID = ? and REFERENCE = ?");
        PS.setInt(1, ObjID);
        PS.setInt(2, ObjRefID);
        ResultSet RS = PS.executeQuery();
        int attrid=0;
        while(RS.next())
        {
            attrid =  RS.getInt(1);
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return attrid;
    }


    public String getValue(int ObjID, int AttrId) throws SQLException
    {
        Connection Con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "nc","nc");
        PreparedStatement PS = Con
                .prepareStatement("SELECT VALUE FROM PARAMS WHERE OBJECT_ID = ? and ATTR_ID = ?");
        PS.setInt(1, ObjID);
        PS.setInt(2, AttrId);
        ResultSet RS = PS.executeQuery();
        String value = "";
        while(RS.next())
        {
            value =  RS.getString(1);
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return value;
    }


    // Удаление пользователя
    public void deleteObject(Integer ID) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException
    {
        Connection Con = getConnection();
        PreparedStatement PS = Con
                .prepareStatement("DELETE FROM PARAMS WHERE OBJECT_ID = ?");
        PS.setInt(1, ID);
        PS.executeUpdate();
        PS = Con
                .prepareStatement("DELETE FROM REFERENCES WHERE OBJECT_ID = ?");
        PS.setInt(1, ID);
        PS.executeUpdate();
        PS = Con.prepareStatement("DELETE FROM REFERENCES WHERE REFERENCE = ?");
        PS.setInt(1, ID);
        PS.executeUpdate();
        PS = Con.prepareStatement("DELETE FROM OBJECTS WHERE OBJECT_ID = ?");
        PS.setInt(1, ID);
        PS.executeUpdate();
        PS.close();
        CloseConnection(Con);
    }

    // Удаление события:
    public void deleteEvent(Integer eventId) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException
    {
        Connection Con = getConnection();
        PreparedStatement PS = Con.prepareStatement("DELETE FROM PARAMS WHERE OBJECT_ID = ?");
        PS.setInt(1, eventId);
        PS.executeUpdate();
        PS = Con.prepareStatement("DELETE FROM REFERENCES WHERE OBJECT_ID = ?");
        PS.setInt(1, eventId);
        PS.executeUpdate();
        PS = Con.prepareStatement("DELETE FROM REFERENCES WHERE REFERENCE = ?");
        PS.setInt(1, eventId);
        PS.executeUpdate();
        PS = Con.prepareStatement("DELETE FROM OBJECTS WHERE OBJECT_ID = ?");
        PS.setInt(1, eventId);
        PS.executeUpdate();
        // И удаляем 13-ый параметр в PARAMS (task_id для текущего пользователя):
        PS = Con.prepareStatement("DELETE FROM PARAMS WHERE VALUE = ?");
        PS.setString(1, String.valueOf(eventId));
        PS.executeUpdate();

        PS.close();
        CloseConnection(Con);
    }

    public void addNewUser(int ObjTypeID, String name, TreeMap<Integer, String> massAttr) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();
        PreparedStatement PS = Con
                .prepareStatement("INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES (?,?,?)");
        Statement st = Con.createStatement();
        ResultSet RS = st.executeQuery("Select max(OBJECT_ID) from OBJECTS WHERE OBJECT_TYPE_ID = 1001");
        int newID = 0;
        while (RS.next()) {
            newID = RS.getInt(1) + 1;
        }
        PS.setInt(1, newID);
        PS.setInt(2, ObjTypeID);
        PS.setObject(3, name);
        PS.executeUpdate();
        PS.close();

        PreparedStatement PS1 = Con
                .prepareStatement("INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES (?,?,?)");
        while (!massAttr.isEmpty()) {
            java.util.Map.Entry<Integer, String> En = massAttr.pollFirstEntry();
            PS1.setObject(1, En.getValue());
            PS1.setInt(2, newID);
            PS1.setInt(3, En.getKey());
            PS1.addBatch();
        }
        PS1.executeBatch();
        PS1.close();


        CloseConnection(Con);
    }

    // Метод добавления события со всеми его атрибутами (2017-01-31)
    public void addNewEvent(int ObjTypeID, String name, TreeMap<Integer, Object> massAttr) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Connection Con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "nc","nc");

        // 1) Добавление события:
        PreparedStatement PS = Con.prepareStatement("INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES (?,?,?)");
        Statement st = Con.createStatement();
        ResultSet RS = st.executeQuery("Select max(OBJECT_ID) from OBJECTS WHERE OBJECT_TYPE_ID = " + ObjTypeID);
        int newID = 0;
        while (RS.next()) {
            newID = RS.getInt(1) + 1;
        }
        PS.setInt(1, newID);
        PS.setInt(2, ObjTypeID);
        PS.setObject(3, name);
        PS.executeUpdate();
        PS.close();

        // 2) Добавление атрибутов события (параметры со страницы создания события):
        PreparedStatement PS1 = Con.prepareStatement("INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES (?,?,?)");
        while (!massAttr.isEmpty()) {
            java.util.Map.Entry<Integer, Object> En = massAttr.pollFirstEntry();
            PS1.setObject(1, En.getValue());
            PS1.setInt(2, newID);
            PS1.setInt(3, En.getKey());
            PS1.addBatch();
        }
        PS1.executeBatch();
        PS1.close();

        // 3) Добавление ссылки Юзер - Событие (связывание):
        UserService userService = new UserService();
        int idUser = new DBHelp().getObjID(userService.getCurrentUsername());
        int attrId = 13;
        PreparedStatement PS2 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");
        PS2.setInt(1, idUser); // System.out.println(idUser);
        PS2.setInt(2, attrId); // System.out.println(attrId);
        PS2.setInt(3, newID); // System.out.println(newID);
        PS2.executeQuery(); // PS2.executeBatch();
        PS2.close();

        // 4) Добавление 13-го параметра в PARAMS (task_id для текущего пользователя):
        PreparedStatement PS3 = Con.prepareStatement("INSERT INTO PARAMS (OBJECT_ID, ATTR_ID, VALUE) VALUES (?,?,?)");
        PS3.setInt(1, idUser); // System.out.println(idUser); // = user_id
        PS3.setInt(2, attrId); // System.out.println(attrId); // = 13
        PS3.setObject(3, newID); // System.out.println(newID); // = task_id
        PS3.executeQuery();
        PS3.close();

        // 5) (НА ВСЯКИЙ СЛУЧАЙ) Удаление 13-го параметра с VALUE = NULL в PARAMS (task_id для текущего пользователя):
        PreparedStatement PS4 = Con.prepareStatement("DELETE FROM PARAMS WHERE OBJECT_ID = ? AND ATTR_ID = ? AND VALUE IS NULL");
        PS4.setInt(1, idUser); // = user_id
        PS4.setInt(2, attrId); // = 13
        PS4.executeUpdate();
        PS4.close();


        /*
        // Обновление существующего
        PreparedStatement PS3 = Con.prepareStatement("UPDATE PARAMS SET VALUE = ? WHERE OBJECT_ID = ? and ATTR_ID = ?");
        PS3.setObject(1, newID); System.out.println(newID); // = task_id
        PS3.setInt(2, idUser); System.out.println(idUser); // = user_id
        PS3.setInt(3, attrId); System.out.println(attrId); // = 13
        PS3.executeUpdate();
        PS3.close();
        */

        CloseConnection(Con);
    }

    // Обновление события
    public void updateEvent(int ObjID, String name, TreeMap<Integer, Object> massAttr) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();
        PreparedStatement PS = Con.prepareStatement("UPDATE PARAMS SET VALUE = ? WHERE OBJECT_ID = ? and ATTR_ID = ?");
        while (!massAttr.isEmpty()) {
            java.util.Map.Entry<Integer, Object> En = massAttr.pollFirstEntry();
            PS.setObject(1, En.getValue());
            PS.setInt(2, ObjID);
            PS.setInt(3, En.getKey());
            PS.addBatch();
        }
        PS.executeBatch();
        PS.close();

        PreparedStatement PS1 = Con.prepareStatement("UPDATE OBJECTS SET OBJECT_NAME = ? WHERE OBJECT_ID = ?");
        PS1.setString(1, name);
        PS1.setInt(2, ObjID); //
        PS1.executeUpdate();
        PS1.close();

        CloseConnection(Con);
    }


    public void updateUser(int ObjTypeID, String name, TreeMap<Integer, String> massAttr) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();
        PreparedStatement PS = Con
                .prepareStatement("UPDATE PARAMS SET VALUE = ? WHERE OBJECT_ID = ? and ATTR_ID = ?");
        while (!massAttr.isEmpty()) {
            java.util.Map.Entry<Integer, String> En = massAttr.pollFirstEntry();
            PS.setObject(1, En.getValue());
            PS.setInt(2, ObjTypeID);
            PS.setInt(3, En.getKey());
            PS.addBatch();
        }
        PS.executeBatch();
        PS.close();

        PreparedStatement PS1 = Con
                .prepareStatement("UPDATE OBJECTS SET OBJECT_NAME = ? WHERE OBJECT_ID = ?");
        PS1.setString(1, name);
        PS1.setInt(2, ObjTypeID);
        PS1.executeUpdate();
        PS1.close();
        CloseConnection(Con);
    }



    public static void main(String[] args) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "nc","nc");
        //int attr = getAttrID(10001, 20004);
        //System.out.println(attr);
       // deleteObject(10001);
       // addTask();

      //  System.out.println(getValue(10001, 6));

    }



}
