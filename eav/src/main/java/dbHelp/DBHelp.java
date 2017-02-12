package dbHelp;

import entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserServiceImp;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

/**
 * Created by Lawrence on 14.01.2017.
 */

public class DBHelp {

    private static final Logger logger = LoggerFactory.getLogger(DBHelp.class);

    private UserServiceImp userService = UserServiceImp.getInstance();

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

    public int generationID(int objTypeID) throws SQLException {
        Connection Con = getConnection();
        Statement st = Con.createStatement();
        ResultSet RS = st.executeQuery("Select max(OBJECT_ID) from OBJECTS WHERE OBJECT_TYPE_ID = " + objTypeID);
        int newID = 0;
        while (RS.next()) {
            newID = RS.getInt(1) + 1;
        }
        return newID;
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


    public ArrayList<User> searchUser(String name) throws SQLException {
        ArrayList<User> Res = new ArrayList<>();
        Connection Con = getConnection();
        Integer userTypeID = 1001; // ID типа Пользователь
        String sqlName = "%" + name + "%";
        PreparedStatement PS = Con.
                prepareStatement("SELECT ob.OBJECT_ID, pa1.VALUE, pa2.VALUE, pa3.VALUE, pa4.VALUE " +
                        " FROM OBJECTS ob " +
                        "LEFT JOIN PARAMS pa1 ON ob.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 1 " +
                        "LEFT JOIN PARAMS pa2 ON ob.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 2 " +
                        "LEFT JOIN PARAMS pa3 ON ob.OBJECT_ID = pa3.OBJECT_ID AND pa3.ATTR_ID = 3 " +
                        "LEFT JOIN PARAMS pa4 ON ob.OBJECT_ID = pa4.OBJECT_ID AND pa4.ATTR_ID = 4 " +
                        " WHERE ob.OBJECT_TYPE_ID = ? AND (lower(ob.OBJECT_NAME) LIKE lower(?)) ORDER BY ob.OBJECT_NAME");
        PS.setInt(1, userTypeID); // В качестве параметра id типа Пользователь
        PS.setString(2, sqlName);
        ResultSet RS = PS.executeQuery();

        while (RS.next()) {
            User user = new User();
            user.setId(RS.getInt(1));
            user.setName(RS.getString(2));
            user.setSurname(RS.getString(3));
            user.setMiddleName(RS.getString(4));
            user.setLogin(RS.getString(5));
            Res.add(user);

            // System.out.println(user.getId() + " , " + user.getName());
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return Res;
    }



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


    public ArrayList<Object> getEmail(String email)
            throws SQLException {
        ArrayList<Object> Res = new ArrayList<>();
        Connection Con = getConnection();
        PreparedStatement PS = Con
                .prepareStatement("SELECT p.VALUE " +
                        "FROM PARAMS p " +
                        "WHERE p.ATTR_ID = 6 and p.VALUE = ?" );
        PS.setString(1, email);
        ResultSet RS = PS.executeQuery();
        while (RS.next()) {
            Res.add(RS.getObject(1));
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return Res;
    }


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


    public User getCurrentUser() throws SQLException {
        Integer userID = new DBHelp().getObjID(userService.getCurrentUsername());
        User user = getUserByUserID(userID);
        return user;
    }

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

    public User getUserAndEventByUserID(int userID) throws SQLException {
        Connection Con = getConnection();
        ArrayList<Event> events = new ArrayList<>();
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

        PreparedStatement PS1 = Con.prepareStatement("SELECT ev.OBJECT_ID, ob.OBJECT_ID, ev.OBJECT_NAME," +
                "pa2.VALUE, pa3.VALUE, pa4.VALUE, pa5.VALUE FROM OBJECTS ob LEFT JOIN REFERENCES re " +
                "ON ob.OBJECT_ID = re.OBJECT_ID LEFT JOIN OBJECTS ev  ON re.REFERENCE = ev.OBJECT_ID " +
                "LEFT JOIN PARAMS pa1 ON ev.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 103 LEFT JOIN PARAMS pa2 " +
                "ON ev.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 101   LEFT JOIN PARAMS pa3 " +
                "ON ev.OBJECT_ID = pa3.OBJECT_ID AND  pa3.ATTR_ID = 102 LEFT JOIN PARAMS pa4 " +
                "ON ev.OBJECT_ID = pa4.OBJECT_ID AND pa4.ATTR_ID = 105 LEFT JOIN PARAMS pa5 " +
                "ON ev.OBJECT_ID = pa5.OBJECT_ID AND pa5.ATTR_ID = 104 WHERE ob.OBJECT_ID = ? AND re.ATTR_ID = 13 ORDER BY ev.OBJECT_ID");
        PS1.setInt(1, userID);
        ResultSet RS1 = PS1.executeQuery();
        while (RS1.next()) {
            Event event = new Event();
            event.setId( RS1.getInt(1));
            event.setHost_id( RS1.getInt(2));
            event.setName( RS1.getString(3));
            event.setDate_begin( RS1.getString(4));
            event.setDate_end( RS1.getString(5));
            event.setPriority( RS1.getString(6));
            event.setInfo( RS1.getString(7));

            events.add(event);
        }
        try {
            assert user != null;
            user.setEventsUser(events);
        } catch (NullPointerException e) {
            System.out.println("У данного пользователя нет событий или такой пользователь не найден");
        }

        CloseConnection(Con);
        return user;
    }

    public ArrayList<Event> getEventList(int ObjectID) throws SQLException {
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
                "ON ev.OBJECT_ID = pa5.OBJECT_ID AND pa5.ATTR_ID = 104 WHERE ob.OBJECT_ID = ? AND re.ATTR_ID = 13 ORDER BY ev.OBJECT_ID");
        PS.setInt(1, ObjectID); // В качестве параметра id пользователя
        ResultSet RS = PS.executeQuery(); // System.out.println(RS);
        while (RS.next()) {
            Event event = new Event();
            event.setId( RS.getInt(1));
            event.setHost_id( RS.getInt(2));
            event.setName( RS.getString(3));
            event.setDate_begin( RS.getString(4));
            event.setDate_end( RS.getString(5));
            event.setPriority( RS.getString(6));
            event.setInfo( RS.getString(7));

            Res.add(event); // Res.add(RS.getObject(1));
            //Res.add(RS.getObject(2));
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return Res;
    }


    public Event getEventByEventID(int EventID) throws SQLException {
        Connection Con = getConnection();

        Integer idUser = new DBHelp().getObjID(userService.getCurrentUsername());

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
            event.setPriority(RS.getString(6));
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
        Connection Con = getConnection();
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

    public void setNewUser(DataObject dataObject) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();
        PreparedStatement PS = Con
                .prepareStatement("INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES (?,?,?)");
        int newID = generationID(dataObject.getObjectTypeId());
        PS.setInt(1, newID);
        PS.setInt(2, dataObject.getObjectTypeId());
        PS.setObject(3, dataObject.getName());
        PS.executeUpdate();
        PS.close();

        PreparedStatement PS1 = Con
                .prepareStatement("INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES (?,?,?)");
        while (!dataObject.getParams().isEmpty()) {
            java.util.Map.Entry<Integer, String> En = dataObject.getParams().pollFirstEntry();
            PS1.setObject(1, En.getValue());
            PS1.setInt(2, newID);
            PS1.setInt(3, En.getKey());
            PS1.addBatch();
        }
        PS1.executeBatch();
        PS1.close();


        CloseConnection(Con);
    }

    ///////// Новое 2017-02-12 (для DataObject)
    public TreeMap<Integer, Object> getUserById(int userID) throws SQLException {
        Connection Con = getConnection();
        Integer userTypeID = 1001; // ID типа Пользователь
        PreparedStatement PS = Con.
                prepareStatement("SELECT ob.OBJECT_ID, pa1.VALUE, pa2.VALUE, pa3.VALUE, pa4.VALUE, pa5.VALUE, " +
                        "pa6.VALUE, pa7.VALUE, pa8.VALUE, pa9.VALUE, pa10.VALUE, pa11.VALUE FROM OBJECTS ob " +
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
                        "LEFT JOIN PARAMS pa11 ON ob.OBJECT_ID = pa11.OBJECT_ID AND pa11.ATTR_ID = 11 " +
                        "WHERE ob.OBJECT_TYPE_ID = ? AND ob.OBJECT_ID = ? ORDER BY ob.OBJECT_ID");
        PS.setInt(1, userTypeID); // В качестве параметра id типа Пользователь
        PS.setInt(2, userID); // В качестве параметра id пользователя
        ResultSet RS = PS.executeQuery(); // System.out.println(RS);
        TreeMap<Integer, Object> treeMap = null;
        while (RS.next()) {
            treeMap = new TreeMap<>();
            // treeMap.put(1, RS.getInt(1)); // id уже не нужно
            treeMap.put(1, RS.getString(2)); // name
            treeMap.put(2, RS.getString(3)); // surname
            treeMap.put(3, RS.getString(4)); // MiddleName
            treeMap.put(4, RS.getString(5)); // Login
            treeMap.put(5, RS.getString(6)); // AgeDate
            treeMap.put(6, RS.getString(7)); // Email
            treeMap.put(7, RS.getString(8)); // Password
            treeMap.put(8, RS.getString(9)); // Sex
            treeMap.put(9, RS.getString(10)); // Country
            treeMap.put(10, RS.getString(11)); // Additional_field
            treeMap.put(11, RS.getString(12)); // Avatar
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return treeMap;
    }


    public void setNewEvent(Event event) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();
        TreeMap<Integer, Object> attributeArray = event.getArrayWithAttributes();
        // 1) Добавление события:
        PreparedStatement PS = Con.prepareStatement("INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES (?,?,?)");
        Statement st = Con.createStatement();
        ResultSet RS = st.executeQuery("Select max(OBJECT_ID) from OBJECTS WHERE OBJECT_TYPE_ID = " + event.objTypeID);
        int newID = 0;
        while (RS.next()) {
            newID = RS.getInt(1) + 1;
        }
        PS.setInt(1, newID);
        PS.setInt(2, event.objTypeID);
        PS.setObject(3, event.getName());
        PS.executeUpdate();
        PS.close();

        // 2) Добавление атрибутов события (параметры со страницы создания события):
        PreparedStatement PS1 = Con.prepareStatement("INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES (?,?,?)");
        while (!attributeArray.isEmpty()) {
            java.util.Map.Entry<Integer, Object> En = attributeArray.pollFirstEntry();
            PS1.setObject(1, En.getValue());
            PS1.setInt(2, newID);
            PS1.setInt(3, En.getKey());
            PS1.addBatch();
        }
        PS1.executeBatch();
        PS1.close();

        // 3) Добавление ссылки Юзер - Событие (связывание):
        int idUser = userService.getObjID(userService.getCurrentUsername());
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


    public void updateEvent(int ObjID, Event event) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();
        TreeMap<Integer, Object> attributeArray = event.getArrayWithAttributes();
        PreparedStatement PS = Con.prepareStatement("UPDATE PARAMS SET VALUE = ? WHERE OBJECT_ID = ? and ATTR_ID = ?");
        while (!attributeArray.isEmpty()) {
            java.util.Map.Entry<Integer, Object> En = attributeArray.pollFirstEntry();
            PS.setObject(1, En.getValue());
            PS.setInt(2, ObjID);
            PS.setInt(3, En.getKey());
            PS.addBatch();
        }
        PS.executeBatch();
        PS.close();

        PreparedStatement PS1 = Con.prepareStatement("UPDATE OBJECTS SET OBJECT_NAME = ? WHERE OBJECT_ID = ?");
        PS1.setString(1, event.getName());
        PS1.setInt(2, ObjID);
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


    public void setFriend(int idFriend) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();


        int idUser = getObjID(userService.getCurrentUsername()); // Получаем id текущего авторизованного пользователя
        int attrId = 12; // ID атрибута в базе, соответствующий друзьям пользователя

        User user = getCurrentUser(); // Получаем объект текущего пользователя
        String fullNameUser = user.getMiddleName() + " " +  user.getName() + " " + user.getSurname(); // Формируем полное имя друга

        User friend = getUserByUserID(idFriend); // Получаем объект друга
        String fullNameFriend = friend.getSurname() + " " +  friend.getName() + " " + friend.getMiddleName(); // Формируем полное имя друга

        // 1) Проверяем, находится ли данный пользователь у нас в друзьях:
        // SELECT COUNT(*) FROM REFERENCES WHERE (OBJECT_ID = 10003 AND REFERENCE = 10002 OR OBJECT_ID = 10002 AND REFERENCE = 10003) AND ATTR_ID = 12;
        PreparedStatement PS = Con.prepareStatement("SELECT COUNT(*) FROM REFERENCES WHERE (OBJECT_ID = ? AND REFERENCE = ? OR OBJECT_ID = ? AND REFERENCE = ?) AND ATTR_ID = ?");
        PS.setInt(1, idUser);
        PS.setInt(2, idFriend);
        PS.setInt(3, idFriend);
        PS.setInt(4, idUser);
        PS.setInt(5, attrId);
        ResultSet RS = PS.executeQuery();
        int count = 0;
        while(RS.next())
        {
            count =  RS.getInt(1);
        }
        RS.close();
        PS.close();


        if ( (count == 0) & (idUser != idFriend) ){ // Если нет в друзьях и не добавляем пользователя к самому себе в друзья, то добавляем

            // 1) Добавление 12-го параметра в PARAMS (user_id для текущего пользователя):

            PreparedStatement PS1 = Con.prepareStatement("INSERT INTO PARAMS (OBJECT_ID, ATTR_ID, VALUE) VALUES (?,?,?)");
            PS1.setInt(1, idUser); //  = current user_id
            PS1.setInt(2, attrId); //  = 12
            PS1.setObject(3, fullNameFriend); //  = new friend user_id
            PS1.executeQuery();
            PS1.close();

            // 2) Добавление 12-го параметра в PARAMS (наоборот для друга - что вы у него в друзьях):
            PreparedStatement PS2 = Con.prepareStatement("INSERT INTO PARAMS (OBJECT_ID, ATTR_ID, VALUE) VALUES (?,?,?)");
            PS2.setInt(1, idFriend);
            PS2.setInt(2, attrId);
            PS2.setObject(3, fullNameUser);
            PS2.executeQuery();
            PS2.close();

            // 3) Добавление ссылки Юзер - Друг (связывание):
            PreparedStatement PS3 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");
            PS3.setInt(1, idUser);
            PS3.setInt(2, attrId);
            PS3.setInt(3, idFriend);
            PS3.executeQuery();
            PS3.close();

            // 4) Добавление ссылки Друг - Юзер (обратное связывание):
            PreparedStatement PS4 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");
            PS4.setInt(1, idFriend);
            PS4.setInt(2, attrId);
            PS4.setInt(3, idUser);
            PS4.executeQuery();
            PS4.close();

            // 5) (НА ВСЯКИЙ СЛУЧАЙ) Удаление 12-го параметра с VALUE = NULL в PARAMS (для текущего пользователя)
            PreparedStatement PS5 = Con.prepareStatement("DELETE FROM PARAMS WHERE OBJECT_ID = ? AND ATTR_ID = ? AND VALUE IS NULL");
            PS5.setInt(1, idUser);
            PS5.setInt(2, attrId);
            PS5.executeUpdate();
            PS5.close();

            // 6) (НА ВСЯКИЙ СЛУЧАЙ) Удаление 12-го параметра с VALUE = NULL в PARAMS (для друга)
            PreparedStatement PS6 = Con.prepareStatement("DELETE FROM PARAMS WHERE OBJECT_ID = ? AND ATTR_ID = ? AND VALUE IS NULL");
            PS6.setInt(1, idFriend);
            PS6.setInt(2, attrId);
            PS6.executeUpdate();
            PS6.close();

        }

        CloseConnection(Con);
    }



    public void deleteFriend(int idFriend) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();

        int idUser = getObjID(userService.getCurrentUsername()); // Получаем id текущего авторизованного пользователя
        int attrId = 12; // ID атрибута в базе, соответствующий друзьям пользователя

        PreparedStatement PS = Con.prepareStatement("DELETE FROM PARAMS WHERE (OBJECT_ID = ? OR OBJECT_ID = ?) AND ATTR_ID = ?");
        PS.setInt(1, idUser);  //  = current user_id
        PS.setInt(2, idFriend); //  = new friend user_id
        PS.setInt(3, attrId); //  = 12
        PS.executeUpdate();

        PS = Con.prepareStatement("DELETE FROM REFERENCES WHERE (OBJECT_ID = ? AND REFERENCE = ? OR OBJECT_ID = ? AND REFERENCE = ?) AND ATTR_ID = ?");
        PS.setInt(1, idUser);  //  = current user_id
        PS.setInt(2, idFriend); //  = new friend user_id
        PS.setInt(3, idFriend); //  = new friend user_id
        PS.setInt(4, idUser);  //  = current user_id
        PS.setInt(5, attrId); //  = 12
        PS.executeUpdate();
        PS.close();
        CloseConnection(Con);
    }


    public ArrayList<User> getFriendListCurrentUser() throws SQLException {
        Integer userID = new DBHelp().getObjID(userService.getCurrentUsername());
        ArrayList<User> friendList = getFriendListByUserId(userID);
        return friendList;
    }


    public ArrayList<User> getFriendListByUserId(int userID) throws SQLException {
        ArrayList<User> Res = new ArrayList<>();
        Connection Con = getConnection();
        Integer userTypeID = 1001; // ID типа Пользователь
        int attrId = 12; // ID атрибута в базе, соответствующий друзьям пользователя

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
                        "AND ob.OBJECT_ID = ? ORDER BY ob.OBJECT_ID"); // id юзера, кому ищем друзей

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
            friend.setCountry(RS.getString(10));
            friend.setAdditional_field(RS.getString(11));
            Res.add(friend);
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return Res;
    }



    public void setNewMessage(int ObjTypeID, TreeMap<Integer, Object> massAttr) throws SQLException,
           NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();

        // 1) Добавление сообщения:
        PreparedStatement PS = Con.prepareStatement("INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES (?,?,?)");
        Statement st = Con.createStatement();
        ResultSet RS = st.executeQuery("Select max(OBJECT_ID) from OBJECTS WHERE OBJECT_TYPE_ID = " + ObjTypeID);
        int newID = 30001; // 30к - отсюда отсчет айди для сообщений
        while (RS.next()) {
            newID = RS.getInt(1) + 1;
        }

        String name = "Message_" + newID;
        PS.setInt(1, newID);
        PS.setInt(2, ObjTypeID);
        PS.setObject(3, name);
        PS.executeUpdate();
        PS.close();

        // 2) Добавление атрибутов со страницы создания сообщения:
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

        // 3) Добавление ссылки Юзер - Сообщение (связывание): INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES ('10001', '30', '30001');
        int idUser = new DBHelp().getObjID(userService.getCurrentUsername());
        int attrId = 30;
        PreparedStatement PS2 = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");
        PS2.setInt(1, idUser); // System.out.println(idUser);
        PS2.setInt(2, attrId); // System.out.println(attrId);
        PS2.setInt(3, newID); // System.out.println(newID);
        PS2.executeQuery(); // PS2.executeBatch();
        PS2.close();

        CloseConnection(Con);
    }


    public void deleteMessage(Integer messageId) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException
    {
        Connection Con = getConnection();
        PreparedStatement PS = Con.prepareStatement("DELETE FROM PARAMS WHERE OBJECT_ID = ?");
        PS.setInt(1, messageId);
        PS.executeUpdate();
        PS = Con.prepareStatement("DELETE FROM REFERENCES WHERE OBJECT_ID = ?");
        PS.setInt(1, messageId);
        PS.executeUpdate();
        PS = Con.prepareStatement("DELETE FROM REFERENCES WHERE REFERENCE = ?");
        PS.setInt(1, messageId);
        PS.executeUpdate();
        PS = Con.prepareStatement("DELETE FROM OBJECTS WHERE OBJECT_ID = ?");
        PS.setInt(1, messageId);
        PS.executeUpdate();
        // И удаляем 30-ый параметр в PARAMS (task_id для текущего пользователя):
        PS = Con.prepareStatement("DELETE FROM PARAMS WHERE VALUE = ?");
        PS.setString(1, String.valueOf(messageId));
        PS.executeUpdate();

        PS.close();
        CloseConnection(Con);
    }



    public ArrayList<Message> getMessageList(int from_id, int to_id) throws SQLException {
        // from_id = 10001; to_id = 10002; // ТОЛЬКО ДЛЯ ОТЛАДКИ!!!
        ArrayList<Message> Res = new ArrayList<>();
        Connection Con = getConnection();
        PreparedStatement PS = Con.prepareStatement("SELECT ms.OBJECT_ID, ob.OBJECT_ID, pa2.VALUE, " +
                "pa3.VALUE, pa4.VALUE, pa5.VALUE, pa6.VALUE, pa7.VALUE FROM OBJECTS ob " +
                "LEFT JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID " +
                "LEFT JOIN OBJECTS ms ON re.REFERENCE = ms.OBJECT_ID " +
                "LEFT JOIN PARAMS pa1 ON ms.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 201 " +
                "LEFT JOIN PARAMS pa2 ON ms.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 202 " +
                "LEFT JOIN PARAMS pa3 ON ms.OBJECT_ID = pa3.OBJECT_ID AND pa3.ATTR_ID = 203 " +
                "LEFT JOIN PARAMS pa4 ON ms.OBJECT_ID = pa4.OBJECT_ID AND pa4.ATTR_ID = 204 " +
                "LEFT JOIN PARAMS pa5 ON ms.OBJECT_ID = pa5.OBJECT_ID AND pa5.ATTR_ID = 205 " +
                "LEFT JOIN PARAMS pa6 ON ms.OBJECT_ID = pa6.OBJECT_ID AND pa6.ATTR_ID = 206 " +
                "LEFT JOIN PARAMS pa7 ON ms.OBJECT_ID = pa7.OBJECT_ID AND pa7.ATTR_ID = 207 " +
                "WHERE (ob.OBJECT_ID = ? AND pa2.VALUE = ? OR ob.OBJECT_ID = ? " +
                "AND pa2.VALUE = ?) AND re.ATTR_ID = 30 ORDER BY ms.OBJECT_ID");
        PS.setInt(1, from_id); // В качестве параметра id пользователя отправителя
        PS.setInt(2, to_id); // В качестве параметра id пользователя получателя
        // и наоборот:
        PS.setInt(3, to_id); // В качестве параметра id пользователям
        PS.setInt(4, from_id); // В качестве параметра id пользователя отправителя
        ResultSet RS = PS.executeQuery(); // System.out.println(RS);
        while (RS.next()) {
            Message message = new Message();
            message.setId( RS.getInt(1) );
            message.setFrom_id( RS.getInt(2) );
            message.setTo_id( RS.getInt(3) );
            message.setDate_send( RS.getString(4));
            message.setRead_status( RS.getInt(5) );
            message.setText( RS.getString(6));
            message.setFrom_name( RS.getString(7));
            message.setTo_name( RS.getString(8));

            Res.add(message); // Res.add(RS.getObject(1));
            //Res.add(RS.getObject(2));
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return Res;
    }



    //region Meeting

    public ArrayList<Meeting> getAllMeetingsList() throws SQLException {
        ArrayList<Meeting> Res = new ArrayList<>();
        Connection Con = getConnection();
        Integer objTypeID = new Meeting().objTypeID; // ID типа Встреча
        PreparedStatement PS = Con.
                prepareStatement("SELECT ob.OBJECT_ID, pa1.VALUE, pa2.VALUE, pa3.VALUE, pa4.VALUE, pa5.VALUE, " +
                        "pa6.VALUE, pa7.VALUE FROM OBJECTS ob " +
                        "LEFT JOIN PARAMS pa1 ON ob.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 301 " +
                        "LEFT JOIN PARAMS pa2 ON ob.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 302 " +
                        "LEFT JOIN PARAMS pa3 ON ob.OBJECT_ID = pa3.OBJECT_ID AND pa3.ATTR_ID = 303 " +
                        "LEFT JOIN PARAMS pa4 ON ob.OBJECT_ID = pa4.OBJECT_ID AND pa4.ATTR_ID = 304 " +
                        "LEFT JOIN PARAMS pa5 ON ob.OBJECT_ID = pa5.OBJECT_ID AND pa5.ATTR_ID = 305 " +
                        "LEFT JOIN PARAMS pa6 ON ob.OBJECT_ID = pa6.OBJECT_ID AND pa6.ATTR_ID = 306 " +
                        "LEFT JOIN PARAMS pa7 ON ob.OBJECT_ID = pa7.OBJECT_ID AND pa7.ATTR_ID = 307 " +
                        "WHERE ob.OBJECT_TYPE_ID = ? ORDER BY ob.OBJECT_ID");
        PS.setInt(1, objTypeID); // В качестве параметра id типа Пользователь
        ResultSet RS = PS.executeQuery(); // System.out.println(RS);
        while (RS.next()) {
            Meeting meeting = new Meeting();
            meeting.setId(RS.getString(1));
            meeting.setTitle(RS.getString(2));
            meeting.setDate_start(RS.getString(3));
            meeting.setDate_end(RS.getString(4));
            meeting.setInfo(RS.getString(5));
            meeting.setOrganizer(this.getUserByUserID(RS.getInt(6)));
            meeting.setTag(RS.getString(7));
            meeting.setMembers(RS.getString(8));
            Res.add(meeting);
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return Res;
    }


    public ArrayList<Meeting> getUserMeetingsList(int userID) throws SQLException {
        ArrayList<Meeting> Res = new ArrayList<>();
        Connection Con = getConnection();
        // PreparedStatement PS = Con.prepareStatement("SELECT OBJECT_NAME FROM OBJECTS WHERE OBJECT_TYPE_ID = ?");

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
                "WHERE ob.OBJECT_ID = ? AND re.ATTR_ID = 307 ORDER BY ev.OBJECT_ID");
        PS.setInt(1, userID); // В качестве параметра id пользователя
        ResultSet RS = PS.executeQuery(); // System.out.println(RS);
        while (RS.next()) {
            Meeting meeting = new Meeting();
            meeting.setId(RS.getString(1));
            meeting.setTitle(RS.getString(2));
            meeting.setDate_start(RS.getString(3));
            meeting.setDate_end(RS.getString(4));
            meeting.setInfo(RS.getString(5));
            meeting.setOrganizer(this.getUserByUserID(RS.getInt(6)));
            meeting.setTag(RS.getString(7));
            Res.add(meeting);
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return Res;
    }


    // Добавить встречу (id у обьекта Meeting указывать не нужно)
    public void setMeeting(Meeting meeting) throws SQLException{

        Connection connection = getConnection();
        int meetingID = 40000;
        TreeMap<Integer, Object> attributeArray = meeting.getArrayWithAttributes();

        Statement st = connection.createStatement();
        ResultSet RS = st.executeQuery("Select max(OBJECT_ID) from OBJECTS WHERE OBJECT_TYPE_ID = " + meeting.objTypeID);
        while (RS.next()) {
            meetingID = RS.getInt(1) + 1;
        }

        PreparedStatement PS = connection.prepareStatement("INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES (?,?,?)");
        PS.setInt(1, meetingID);
        PS.setInt(2, meeting.objTypeID);
        PS.setObject(3, "Met" + meetingID);
        PS.executeUpdate();
        PS.close();

        // 2) Добавление атрибутов события (параметры со страницы создания события):
        PreparedStatement PS1 = connection.prepareStatement("INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES (?,?,?)");
        while (!attributeArray.isEmpty()) {
            java.util.Map.Entry<Integer, Object> en = attributeArray.pollFirstEntry();
            PS1.setObject(1, en.getValue());
            PS1.setInt(2, meetingID);
            PS1.setInt(3, en.getKey());
            PS1.addBatch();
        }
        PS1.executeBatch();
        PS1.close();

        // 3) Добавление ссылки Встреча - Участники (админ в анном случае):
        Integer  idUser = meeting.getOrganizer().getId();
        int referenceAttrId = 307; // Параметр-ссылка, в данном случае - список участников встречи
        PreparedStatement PS2 = connection.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");
        PS2.setInt(1, meetingID ); // ID встречи
        PS2.setInt(2, referenceAttrId); // ID параметра(307)
        PS2.setInt(3, idUser); // ID организатора
        PS2.executeQuery(); //PS2.executeBatch();
        PS2.close();

        CloseConnection(connection);
    }


    public void updateMeeting(String meetingID, Meeting newmeeting) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {

        Connection connection = getConnection();
        PreparedStatement PS = connection.prepareStatement("UPDATE PARAMS SET VALUE = ? WHERE OBJECT_ID = ? and ATTR_ID = ?");
        TreeMap<Integer, Object> arrayAttrib = newmeeting.getArrayWithAttributes();

        while (!arrayAttrib.isEmpty()) {
            java.util.Map.Entry<Integer, Object> En = arrayAttrib.pollFirstEntry();
            PS.setObject(1, En.getValue());
            PS.setString(2, meetingID);
            PS.setInt(3, En.getKey());
            PS.addBatch();
        }
        PS.executeBatch();
        PS.close();
        CloseConnection(connection);
    }


    public void setUsersToMeeting(int meetingID, String... userIDs) throws SQLException{
        Connection connection = getConnection();
        int referenceAttrId = 307; // Параметр-ссылка, в данном случае - список участников встречи
        PreparedStatement PS2 = connection.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");

        for (int i = 0; i < userIDs.length; i++) {
            PS2.setInt(1, meetingID); // ID встречи
            PS2.setInt(2, referenceAttrId); // ID параметра(307)
            PS2.setString(3, userIDs[i]); // ID пользователя
            PS2.addBatch();
        }
        PS2.executeBatch();
        PS2.close();
        CloseConnection(connection);
    }


    public void removeUsersFromMeeting(String meetingID, String... userIDs) throws SQLException{
        Connection connection = getConnection();
        PreparedStatement PS2 = connection.prepareStatement("DELETE FROM REFERENCES WHERE REFERENCE = ? AND OBJECT_ID = ?");

        for (int i = 0; i < userIDs.length; i++) {
            PS2.setString(1, userIDs[i]); // ID пользователя
            PS2.setString(2, meetingID ); // ID встречи
            PS2.executeUpdate();
        }
        PS2.close();
        CloseConnection(connection);
    }


    public ArrayList<User> getUsersAtMeeting(int meetingID) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException, SQLException, NullPointerException{
        ArrayList<User> Res = new ArrayList<>();
        Connection Con = getConnection();
        // PreparedStatement PS = Con.prepareStatement("SELECT OBJECT_NAME FROM OBJECTS WHERE OBJECT_TYPE_ID = ?");

        PreparedStatement PS = Con.prepareStatement("SELECT " +
                "        re.REFERENCE " +
                " FROM  OBJECTS ob " +
                "      LEFT JOIN REFERENCES re " +
                "        ON ob.OBJECT_ID = re.OBJECT_ID " +
                "      LEFT JOIN PARAMS pa1 " +
                "        ON re.REFERENCE = pa1.OBJECT_ID AND pa1.ATTR_ID = 1 " +
                "WHERE ob.OBJECT_ID = ? AND re.ATTR_ID = 307 ORDER BY re.OBJECT_ID");
        PS.setInt(1, meetingID); // В качестве параметра id встречи
        ResultSet RS = PS.executeQuery(); // System.out.println(RS);
        while (RS.next()) {
            User user = this.getUserAndEventByUserID(RS.getInt(1));
            Res.add(user);
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return  Res;
    }


    public Meeting getMeeting(int meetingID) throws SQLException {

        Connection Con = getConnection();
        PreparedStatement PS = Con.prepareStatement("SELECT  ob.OBJECT_ID, " +
                "        pa1.VALUE as PA1, " +
                "        pa2.VALUE as PA2, " +
                "        pa3.VALUE as PA3, " +
                "        pa4.VALUE as PA4, " +
                "        pa5.VALUE as PA5, " +
                "        pa6.VALUE as PA6 " +
                " FROM  OBJECTS ob " +
                "      LEFT JOIN PARAMS pa1 " +
                "        ON ob.OBJECT_ID = pa1.OBJECT_ID AND pa1.ATTR_ID = 301 " +
                "      LEFT JOIN PARAMS pa2 " +
                "        ON ob.OBJECT_ID = pa2.OBJECT_ID AND pa2.ATTR_ID = 302 " +
                "      LEFT JOIN PARAMS pa3 " +
                "        ON ob.OBJECT_ID = pa3.OBJECT_ID AND pa3.ATTR_ID = 303 " +
                "      LEFT JOIN PARAMS pa4 " +
                "        ON ob.OBJECT_ID = pa4.OBJECT_ID AND pa4.ATTR_ID = 304 " +
                "      LEFT JOIN PARAMS pa5 " +
                "        ON ob.OBJECT_ID = pa5.OBJECT_ID AND pa5.ATTR_ID = 305 " +
                "      LEFT JOIN PARAMS pa6 " +
                "        ON ob.OBJECT_ID = pa6.OBJECT_ID AND pa6.ATTR_ID = 306 " +
                " WHERE ob.OBJECT_ID = ? " +
                " ORDER BY ob.OBJECT_ID");
        PS.setInt(1, meetingID); // В качестве параметра id пользователя
        ResultSet RS = PS.executeQuery(); // System.out.println(RS);
        RS.next();
        Meeting meeting = new Meeting();
        meeting.setId(RS.getString(1));
        meeting.setTitle(RS.getString(2));
        meeting.setDate_start(RS.getString(3));
        meeting.setDate_end(RS.getString(4));
        meeting.setInfo(RS.getString(5));
        meeting.setOrganizer(this.getUserByUserID(RS.getInt(6)));
        meeting.setTag(RS.getString(7));

        RS.close();
        PS.close();
        CloseConnection(Con);
        return meeting;
    }

    //endregion


    public static void main(String[] args) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {

        User user = new User();
        user.setId(10003);
        Meeting meeting = new Meeting("Название", "02.02.2017 16:45", "02.02.2017 22:45", "Информация я написал", user, "Привяу", "");
        System.out.println();
        new DBHelp().setMeeting(meeting);
        //ArrayList<User> oldusers = new DBHelp().getUsersAtMeeting("28"); // ID

        //System.out.print("dsd");

        //Meeting ms = new DBHelp().getMeeting(28);
        //System.out.println(ms.getTitle());
        /*ArrayList<User> ms = new DBHelp().getUsersAtMeeting("28");
        for (int i=0; i < ms.size(); i++){
            System.out.println(ms.get(i).getId());
        }*/
        //new DBHelp().addUsersToMeeting("28", "10002", "10001");
        /*ArrayList<Meeting> ms = new DBHelp().getUserMeetingsList(10003);
        for (int i=0; i < ms.size(); i++){
            System.out.println(ms.get(i).getId());
            System.out.println(ms.get(i).getTitle());
            System.out.println(ms.get(i).getDate_start());
            System.out.println(ms.get(i).getDate_end());
            System.out.println(ms.get(i).getInfo());
            System.out.println(ms.get(i).getOrganizer());
            System.out.println(ms.get(i).getTag());
        }*/
       /*System.out.println("START");
        addMeeting(new Meeting("Выпиваем2", "07.02.2017 12:00", "07.02.2017 19:30", "Всем ку, тут хранится информация",  "10003", "#ky", ""));
        updateEvent("28", new Meeting("НЕ Выпиваем ters", "07.02.2017 14:40", "07.02.2017 14:32", "ЗДАРОВ",  "10003", "#ky", ""));
        removeUsersFromMeeting("27",  "10003");

        ArrayList<Meeting> mm = getAllMeetingsList();
        ArrayList<Meeting> ms = getUserMeetingsList(10002);
        for (int i=0; i < ms.size(); i++){
            System.out.println(ms.get(i).getId());
            System.out.println(ms.get(i).getTitle());
            System.out.println(ms.get(i).getDate_start());
            System.out.println(ms.get(i).getDate_end());
            System.out.println(ms.get(i).getInfo());
            System.out.println(ms.get(i).getOrganizer());
            System.out.println(ms.get(i).getTag());
        }
        System.out.println("END");*/
        //DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "nc","nc");
        //int attr = getAttrID(10001, 20004);
        //System.out.println(attr);
        // deleteObject(10001);
        // addTask();

        //  System.out.println(getValue(10001, 6));

    }


////// 2017-02-12 12-56 // Обновление ссылки на загруженный аватар:
public void updateAvatar(int userId, String patch) throws SQLException,
        NoSuchMethodException, IllegalAccessException,
        IllegalArgumentException, InvocationTargetException {

    //System.out.println(userId + " " + patch);
    Connection Con = getConnection();
    // Удаляем ту ссылку, которая уже имеется:
    PreparedStatement PS = Con.prepareStatement("DELETE FROM PARAMS WHERE OBJECT_ID = ? AND ATTR_ID = ?"); // DELETE FROM PARAMS WHERE OBJECT_ID = '10005' AND ATTR_ID = '11';
    PS.setInt(1, userId);
    PS.setInt(2, 11);
    PS.executeUpdate();
	// И создаем новую:
    PS = Con.prepareStatement("INSERT INTO PARAMS (OBJECT_ID, ATTR_ID, VALUE) VALUES (?, ?, ?)"); // INSERT INTO PARAMS (OBJECT_ID, ATTR_ID, VALUE) VALUES ('10005','11','\\upload\\10005\\avatar\\avatar_10005.png');
    PS.setInt(1, userId);
    PS.setInt(2, 11);
    PS.setString(3, patch);
    PS.executeUpdate();

    PS.close();
    CloseConnection(Con);
}


}
