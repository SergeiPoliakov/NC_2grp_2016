package dbHelp;

import entities.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.Filter;
import service.UserServiceImp;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;

import service.filters.*;

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

    public int getObjID(String username) throws SQLException {
        Connection Con = getConnection();
        PreparedStatement PS = Con
                .prepareStatement("SELECT OBJECT_ID FROM PARAMS WHERE VALUE = ?");
        PS.setString(1, username);
        ResultSet RS = PS.executeQuery();
        int objID = 0;
        while (RS.next()) {
            objID = RS.getInt(1);
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return objID;
    }


    public ArrayList<String> getObjParamsByObjID(int objID) throws SQLException {
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

    public ArrayList<Object> getEventParamsByObjID(int eventID) throws SQLException {
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


    public ArrayList<DataObject> searchUser(String name) throws SQLException {
        ArrayList<DataObject> Res = new ArrayList<>();
        TreeMap<Integer, Object> mapAttr = new TreeMap<>();
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
            mapAttr.put(1, RS.getString(2));
            mapAttr.put(2, RS.getString(3));
            mapAttr.put(3, RS.getString(4));
            mapAttr.put(4, RS.getString(5));

            String nameUser = RS.getString(2) + " " + RS.getString(3) + " " + RS.getString(4);

            DataObject dataObject = new DataObject(RS.getInt(1), nameUser, 1001, mapAttr);
            Res.add(dataObject);

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
                        "WHERE p.ATTR_ID = 6 and p.VALUE = ?");
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
            user.setCity(RS.getString(10));
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
            user.setCity(RS.getString(10));
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
            user.setCity(RS.getString(10));
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
            event.setId(RS1.getInt(1));
            event.setHost_id(RS1.getInt(2));
            event.setName(RS1.getString(3));
            event.setDate_begin(RS1.getString(4));
            event.setDate_end(RS1.getString(5));
            event.setPriority(RS1.getString(6));
            event.setInfo(RS1.getString(7));

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

    public ArrayList<DataObject> getEventList(int ObjectID) throws SQLException {
        ArrayList<DataObject> Res = new ArrayList<>();
        TreeMap<Integer, Object> mapAttr = new TreeMap<>();
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
            mapAttr.put(1, RS.getString(4));
            mapAttr.put(2, RS.getString(5));
            mapAttr.put(3, RS.getString(6));
            mapAttr.put(4, RS.getString(7));

            DataObject dataObject = new DataObject(RS.getInt(1), RS.getString(3), 1002, mapAttr);

            Res.add(dataObject); // Res.add(RS.getObject(1));
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

    public int getAttrID(int ObjID, int ObjRefID) throws SQLException {
        Connection Con = getConnection();
        PreparedStatement PS = Con
                .prepareStatement("SELECT ATTR_ID FROM REFERENCES WHERE OBJECT_ID = ? and REFERENCE = ?");
        PS.setInt(1, ObjID);
        PS.setInt(2, ObjRefID);
        ResultSet RS = PS.executeQuery();
        int attrid = 0;
        while (RS.next()) {
            attrid = RS.getInt(1);
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return attrid;
    }


    public String getValue(int ObjID, int AttrId) throws SQLException {
        Connection Con = getConnection();
        PreparedStatement PS = Con
                .prepareStatement("SELECT VALUE FROM PARAMS WHERE OBJECT_ID = ? and ATTR_ID = ?");
        PS.setInt(1, ObjID);
        PS.setInt(2, AttrId);
        ResultSet RS = PS.executeQuery();
        String value = "";
        while (RS.next()) {
            value = RS.getString(1);
        }
        RS.close();
        PS.close();
        CloseConnection(Con);
        return value;
    }


    public void deleteObject(Integer ID) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {
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


    public void deleteEvent(Integer eventId) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {
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
        setDataObjectToDB(dataObject);
        /*
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
        */
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


    public void setNewEvent(DataObject dataObject) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();
        // 1) Добавление события:
        PreparedStatement PS = Con.prepareStatement("INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES (?,?,?)");
        Statement st = Con.createStatement();
        ResultSet RS = st.executeQuery("Select max(OBJECT_ID) from OBJECTS WHERE OBJECT_TYPE_ID = " + dataObject.getObjectTypeId());
        int newID = 0;
        while (RS.next()) {
            newID = RS.getInt(1) + 1;
        }
        PS.setInt(1, newID);
        PS.setInt(2, dataObject.getObjectTypeId());
        PS.setObject(3, dataObject.getName());
        PS.executeUpdate();
        PS.close();

        // 2) Добавление атрибутов события (параметры со страницы создания события):
        PreparedStatement PS1 = Con.prepareStatement("INSERT INTO PARAMS (VALUE,OBJECT_ID,ATTR_ID) VALUES (?,?,?)");
        while (!dataObject.getParams().isEmpty()) {
            java.util.Map.Entry<Integer, String> En = dataObject.getParams().pollFirstEntry();
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


    public void updateUser(DataObject dataObject) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        updateDataObject(dataObject);
        /*
        Connection Con = getConnection();
        PreparedStatement PS = Con
                .prepareStatement("UPDATE PARAMS SET VALUE = ? WHERE OBJECT_ID = ? and ATTR_ID = ?");
        while (!dataObject.getParams().isEmpty()) {
            java.util.Map.Entry<Integer, String> En = dataObject.getParams().pollFirstEntry();
            PS.setObject(1, En.getValue());
            PS.setInt(2, dataObject.getId());
            PS.setInt(3, En.getKey());
            PS.addBatch();
        }
        PS.executeBatch();
        PS.close();

        PreparedStatement PS1 = Con
                .prepareStatement("UPDATE OBJECTS SET OBJECT_NAME = ? WHERE OBJECT_ID = ?");
        PS1.setString(1, dataObject.getName());
        PS1.setInt(2, dataObject.getId());
        PS1.executeUpdate();
        PS1.close();
        CloseConnection(Con);
        */
    }


    public void setFriend(int idFriend) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();


        int idUser = getObjID(userService.getCurrentUsername()); // Получаем id текущего авторизованного пользователя
        int attrId = 12; // ID атрибута в базе, соответствующий друзьям пользователя

        User user = getCurrentUser(); // Получаем объект текущего пользователя
        String fullNameUser = user.getMiddleName() + " " + user.getName() + " " + user.getSurname(); // Формируем полное имя друга

        User friend = getUserByUserID(idFriend); // Получаем объект друга
        String fullNameFriend = friend.getSurname() + " " + friend.getName() + " " + friend.getMiddleName(); // Формируем полное имя друга

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
        while (RS.next()) {
            count = RS.getInt(1);
        }
        RS.close();
        PS.close();


        if ((count == 0) & (idUser != idFriend)) { // Если нет в друзьях и не добавляем пользователя к самому себе в друзья, то добавляем

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
            friend.setCity(RS.getString(10));
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


    public void deleteMessage(Integer messageId) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException {
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
            message.setId(RS.getInt(1));
            message.setFrom_id(RS.getInt(2));
            message.setTo_id(RS.getInt(3));
            message.setDate_send(RS.getString(4));
            message.setRead_status(RS.getInt(5));
            message.setText(RS.getString(6));
            message.setFrom_name(RS.getString(7));
            message.setTo_name(RS.getString(8));

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
    public void setMeeting(Meeting meeting) throws SQLException {

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
        Integer idUser = meeting.getOrganizer().getId();
        int referenceAttrId = 307; // Параметр-ссылка, в данном случае - список участников встречи
        PreparedStatement PS2 = connection.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?,?,?)");
        PS2.setInt(1, meetingID); // ID встречи
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


    public void setUsersToMeeting(int meetingID, String... userIDs) throws SQLException {
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


    public void removeUsersFromMeeting(String meetingID, String... userIDs) throws SQLException {
        Connection connection = getConnection();
        PreparedStatement PS2 = connection.prepareStatement("DELETE FROM REFERENCES WHERE REFERENCE = ? AND OBJECT_ID = ?");

        for (int i = 0; i < userIDs.length; i++) {
            PS2.setString(1, userIDs[i]); // ID пользователя
            PS2.setString(2, meetingID); // ID встречи
            PS2.executeUpdate();
        }
        PS2.close();
        CloseConnection(connection);
    }


    public ArrayList<User> getUsersAtMeeting(int meetingID) throws IllegalAccessException, IllegalArgumentException,
            InvocationTargetException, NoSuchMethodException, SecurityException, SQLException, NullPointerException {
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
        return Res;
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

/*
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

    //  }


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

    // 2017-02-14 Альтернативный вспомогательный метод, вытаскивает все поля ДатаОбджекта, используя универсальный запрос в базу
    public DataObject getObjectsByIdAlternative(int objectId) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();
        PreparedStatement PS = Con.
                prepareStatement("(SELECT -2 AS KEY, CAST(OBJECT_ID AS VARCHAR(70)) AS VALUE, 0 AS REF FROM OBJECTS WHERE OBJECT_ID = ?) " +
                        "UNION (SELECT -1, OBJECT_NAME, 0 FROM OBJECTS WHERE OBJECT_ID = ?) " +
                        "UNION (SELECT 0, CAST(OBJECT_TYPE_ID AS VARCHAR(70)), 0 FROM OBJECTS WHERE OBJECT_ID = ?) " +
                        "UNION (SELECT ATTR_ID, listagg(VALUE, '~') WITHIN GROUP(ORDER BY pa.ATTR_ID) over(PARTITION BY VALUE) " +
                        "AS VALUE_LIST, 0 FROM PARAMS pa WHERE OBJECT_ID = ? AND ATTR_ID != 12 AND ATTR_ID != 13) " +
                        "UNION (SELECT ATTR_ID, CAST(REFERENCE AS VARCHAR(70)), 1 FROM REFERENCES WHERE OBJECT_ID = ?)");
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
            Integer ref = RS.getInt(3); // ref (reference flag, 0 - not ref, 1 - ref)
            // System.out.println(key + " : " + value); // для отладки

            if (key == -2) { // Это пришел к нам айдишник
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

        RS.close();
        PS.close();
        CloseConnection(Con);
        return dataObject;
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
            Connection Con = getConnection();
            PreparedStatement PS = Con.prepareStatement(sql);
            ResultSet RS = PS.executeQuery();

            // Обходим всю полученную таблицу и формируем поля датаобджектов
            DataObject dataObject = null;
            while (RS.next()) {
                Integer key = RS.getInt(1); // key
                String value = RS.getString(2); // value
                // Удаление дублирования строк (Вася Вася Вася):
                value = (((value != null) && (value.indexOf('~') > 0)) ? value.substring(0, value.indexOf('~')) : value);
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
            RS.close();
            PS.close();
            CloseConnection(Con);
        }
        return dataObjectList;
    }

    // 2017-02-14 Альтернативный вспомогательный метод, вытаскивает список id подходящих под фильтры датаобджектов
    public ArrayList<Integer> getListObjectsByListIdAlternative(String... strings) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        ArrayList<Integer> idList = new ArrayList<>();
        if (strings.length > 0) {
            boolean fromFlag = false;
            boolean tableFlag = false;
            String sql = "SELECT OBJECT_ID FROM ";
            for (int i = 0; i < strings.length; i++) {
                switch (strings[i]) {
                    case Filter.OBJECT_TYPE: // Если выбран тип
                        if (!tableFlag) {
                            sql += "OBJECTS ";
                            tableFlag = !tableFlag;
                        }
                        if (!fromFlag) {
                            sql += "WHERE ";
                            fromFlag = !fromFlag;
                        } else {
                            sql += "AND ";
                        }
                        i++;
                        sql += Filter.OBJECT_TYPE + " = " + strings[i] + " ";
                        break;

                    case Filter.OBJECT_NAME:// Если выбрано имя
                        if (!tableFlag) {
                            sql += "OBJECTS ";
                            tableFlag = !tableFlag;
                        }
                        if (!fromFlag) {
                            sql += "WHERE ";
                            fromFlag = !fromFlag;
                        } else {
                            sql += "AND ";
                        }
                        i++;
                        sql += Filter.OBJECT_NAME + " = " + strings[i] + " ";
                        break;
                }
            }
            if (!tableFlag) {
                sql += "OBJECTS ";
                tableFlag = !tableFlag;
            }
            sql += "ORDER BY OBJECT_ID";

            Connection Con = getConnection();
            PreparedStatement PS = Con.prepareStatement(sql);
            ResultSet RS = PS.executeQuery();
            // Обходим всю полученную таблицу и формируем лист id-шек
            while (RS.next()) {
                idList.add(RS.getInt(1));
            }
            RS.close();
            PS.close();
            CloseConnection(Con);
        }
        return idList;
    }

    /* ................................................................................................................... */
    // 2017-02-16 Парсер-генератор строки SQL-запроса по переданному фильтру:
    public String parseGenerate(BaseFilter filter) {
        System.out.println("Запускаю parseGenerate");
        Integer USER = 1001;
        Integer EVENT = 1002;
        Integer MESSAGE = 1003;
        Integer MEETING = 1004;

        String sql = "SELECT ob.OBJECT_ID FROM OBJECTS ob ";
        TreeMap<String, ArrayList<String>> params = filter.getParams();

        // в зависимости от типа фильтра
        if (filter instanceof UserFilter) {
            //  начинаем вытаскивать параметры и формировать строку запроса:
            if (params.get(UserFilter.ALL) != null) { // если надо получить IDs всех пользователей,
                sql += "WHERE ob.OBJECT_TYPE_ID = " + USER;
                System.out.println("Формирую запрос " + sql);
            } else if (params.get(UserFilter.CURRENT) != null) { // если надо получить ID текущего пользователей,
                sql += "WHERE ob.OBJECT_TYPE_ID = " + USER + " ";
                sql += "AND ob.OBJECT_NAME = " + userService.getCurrentUsername();
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
                sql += "AND (lower(ob.OBJECT_NAME) LIKE lower(" + search.get(0) + ")) " + userService.getCurrentUsername();
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
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID AND ATTR_ID = 12 ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + USER + " AND ob2.OBJECT_ID = " + user_id.get(0) + " ";
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
                sql += "JOIN OBJECTS ob2 ON re.OBJECT_ID = ob2.OBJECT_ID ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + EVENT + " ";
                sql += "AND ob2.OBJECT_NAME = " + userService.getCurrentUsername() + " ";
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
                ArrayList<String> date = params.get(EventFilter.AFTER_DATE);
                sql += "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 101 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') " +
                        "BETWEEN TO_DATE(" + date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss') AND TO_DATE(" + date.get(1) + ", 'dd.mm.yyyy hh24:mi:ss'))";
            }

            //sql += "ORDER BY ob.OBJECT_ID"; // И группируем. Возможно придется сабрать в каждую else, если не сработает с INTERSECT

        } else if (filter instanceof MeetingFilter) {
            // Работаем со встречами
            if (params.get(MeetingFilter.ALL) != null) { // если надо получить IDs всех встреч в системе,
                sql += "WHERE ob.OBJECT_TYPE_ID = " + MEETING;
            } else if (params.get(MeetingFilter.FOR_CURRENT_USER) != null) { // если надо получить ID всех встреч текущего пользователей,
                sql += "JOIN REFERENCES re ON ob.OBJECT_ID = re.OBJECT_ID AND re.ATTR_ID = 307 ";
                sql += "JOIN OBJECTS ob2 ON ob2.OBJECT_ID = re.REFERENCE ";
                sql += "WHERE ob.OBJECT_TYPE_ID = " + MEETING + " ";
                sql += "AND ob2.OBJECT_NAME = " + userService.getCurrentUsername() + " ";
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
                sql += "AND ob2.OBJECT_NAME = " + userService.getCurrentUsername() + " ";
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
                sql += "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 203 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') < TO_DATE(" + before_date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss'))";
            } else if (params.get(MessageFilter.AFTER_DATE) != null) { // если надо получить ID всех сообщений, отправленных ПОСЛЕ какой-то даты,
                ArrayList<String> after_date = params.get(MessageFilter.AFTER_DATE);
                sql += "SELECT ob.OBJECT_ID FROM (" + sql + ") ob JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 203 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') > TO_DATE(" + after_date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss'))";
            } else if (params.get(MessageFilter.BETWEEN_TWO_DATES) != null) { // если надо получить ID всех сообщений, отправленных МЕЖДУ двумя датами,
                ArrayList<String> date = params.get(MessageFilter.BETWEEN_TWO_DATES);
                sql += "SELECT ob.OBJECT_ID FROM (" + sql + ") ob " +
                        "JOIN PARAMS pa ON ob.OBJECT_ID = pa.OBJECT_ID " +
                        "AND pa.ATTR_ID = 203 AND (TO_DATE(pa.VALUE, 'dd.mm.yyyy hh24:mi:ss') > TO_DATE(" + date.get(0) + ", 'dd.mm.yyyy hh24:mi:ss')) " +
                        "AND (TO_DATE(pa1.VALUE, 'dd.mm.yyyy hh24:mi:ss') < TO_DATE(" + date.get(1) + ", 'dd.mm.yyyy hh24:mi:ss'))"; // Можно сделать и between'ом, в принципе
            }

            //sql += "ORDER BY ob.OBJECT_ID"; // И группируем. Возможно придется сабрать в каждую else, если не сработает с INTERSECT
        }


        System.out.println("Итоговый запрос " + sql);
        return sql;
    }

    // 2017-02-16 Исполнитель строки SQL-запроса, полученного от Парсер-генератора по переданному фильтру:
    // вытаскивает список id подходящих под фильтры датаобджектов
    public ArrayList<Integer> getListId_SQL_Executor(String sql) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        System.out.println("Выполняю запрос " + sql);
        ArrayList<Integer> idList = new ArrayList<>();
        if (sql != null) {

            Connection Con = getConnection();
            PreparedStatement PS = Con.prepareStatement(sql);
            ResultSet RS = PS.executeQuery();
            // Обходим всю полученную таблицу и формируем лист id-шек
            while (RS.next()) {
                idList.add(RS.getInt(1));
            }
            RS.close();
            PS.close();
            CloseConnection(Con);
        }
        System.out.println("Возвращаю список");
        return idList;
    }

    // 2017-02-16 Все вместе: новый метод для полчения списка объектов, удовлетворяющих фильтрам:
    // Альтернативный вспомогательный метод2, вытаскивает список id подходящих под фильтры датаобджектов
    public ArrayList<Integer> getListObjectsByFilters(BaseFilter filter) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        System.out.println("Запускаю getListObjectsByFilters c полученным фильтром");
        String sql = parseGenerate(filter);
        ArrayList<Integer> idList = getListId_SQL_Executor(sql);
        return idList;
    }

    /*...............................................................................................................*/

    // 2017-02-18 Новый метод выгрузки датаобджекта в базу (создание DO):
    public void setDataObjectToDB(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();
        // 1. Подготавливаем и заполняем в базе строку таблицы OBJECTS
        PreparedStatement PS = Con
                .prepareStatement("INSERT INTO OBJECTS (OBJECT_ID, OBJECT_TYPE_ID, OBJECT_NAME) VALUES (?, ?, ?)");
        int id = generationID(dataObject.getObjectTypeId());
        PS.setInt(1, id);
        PS.setInt(2, dataObject.getObjectTypeId());
        PS.setObject(3, dataObject.getName());
        PS.executeUpdate();
        PS.close();

        // 2. Подготавливаем и заполняем в базе новые строки таблицы PARAMS
        // Получаем список параметров:
        TreeMap<Integer, String> params = dataObject.getParams();
        PS = Con
                .prepareStatement("INSERT INTO PARAMS (OBJECT_ID, ATTR_ID, VALUE) VALUES (?, ?, ?)");
        // Обходим все параметры в листе в датаобджекте и каждый
        for (Map.Entry<Integer, String> entry : params.entrySet()) {
            Integer key = entry.getKey(); // получаем ключ
            String value = entry.getValue(); // получаем значение
            PS.setInt(1, id);
            PS.setInt(2, key);
            PS.setObject(3, value);
            PS.addBatch();
        }
        PS.executeBatch();
        PS.close();

        // 3. Подготавливаем и заполняем в базе новые строки таблицы REFERENCES
        // Получаем список параметров:
        TreeMap<Integer, ArrayList<Integer>> references = dataObject.getRefParams();
        PS = Con
                .prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?, ?, ?)");
        // Обходим все параметры в листе в датаобджекте и каждый
        for (Map.Entry<Integer, ArrayList<Integer>> entry : references.entrySet()) {
            Integer key = entry.getKey(); // получаем ключ
            ArrayList<Integer> valueList = entry.getValue(); // получаем значение
            for (Integer value : valueList) {
                PS.setInt(1, id);
                PS.setInt(2, key);
                PS.setObject(3, value);
                PS.addBatch();
            }
        }
        PS.executeBatch();
        PS.close();

        CloseConnection(Con);
    }

    /*...............................................................................................................*/
    // 2017-02-18 Новый метод обновления датаобджекта в базе:
    public void updateDataObject(DataObject dataObject) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Connection Con = getConnection();
        // 1. Подгружаем из базы текущее состояние DO:
        int id = dataObject.getId();
        DataObject dataObjectOld = getObjectsByIdAlternative(id);

        // 2. Подготавливаем и заполняем (если соотвествующие поля в базе и в памяти отличаются) в базе строку таблицы OBJECTS
        if (dataObjectOld.getName().equals(dataObject.getName()) == false) { // Если имена различны, то обновляем имя
            PreparedStatement PS = Con.prepareStatement("UPDATE OBJECTS SET OBJECT_NAME = ? WHERE OBJECT_ID = ?");
            PS.setString(1, dataObject.getName());
            PS.setInt(2, id);
            PS.executeUpdate();
            PS.close();
        }

        // 3. Подготавливаем и заполняем (если соотвествующие поля в базе и в памяти отличаются) в базе строки таблицы PARAMS
        // Получаем список параметров:
        TreeMap<Integer, String> paramsOld = dataObjectOld.getParams();
        TreeMap<Integer, String> params = dataObject.getParams();

        PreparedStatement PS_upd = Con.prepareStatement("UPDATE PARAMS SET VALUE = ? WHERE OBJECT_ID = ? AND ATTR_ID = ?");

        // Обходим все параметры в листе в новом датаобджекте
        for (Map.Entry<Integer, String> entry : params.entrySet()) {
            Integer key = entry.getKey(); // получаем ключ
            String value = entry.getValue(); // получаем значение
            String valueOld = paramsOld.get(key);
            System.out.println("Старое значение ключа "+key + " = " + valueOld + ", новое значение ключа = " + value);

                PS_upd.setString(1, value);
                PS_upd.setInt(2, id);
                PS_upd.setInt(3, key);
                PS_upd.addBatch();

        }
        PS_upd.executeBatch();
        PS_upd.close();

        // 4. Подготавливаем и заполняем (если соотвествующие поля в базе и в памяти отличаются) в базе строки таблицы REFERENCES
        // Получаем список параметров:
        TreeMap<Integer, ArrayList<Integer>> referencesOld = dataObjectOld.getRefParams();
        TreeMap<Integer, ArrayList<Integer>> references = dataObject.getRefParams();

        PreparedStatement PS_ref_ins = Con.prepareStatement("INSERT INTO REFERENCES (OBJECT_ID, ATTR_ID, REFERENCE) VALUES (?, ?, ?)");
        PreparedStatement PS_ref_del = Con.prepareStatement("DELETE FROM REFERENCES WHERE OBJECT_ID = ? AND ATTR_ID = ?");

        // Обходим все параметры в листе в новом датаобджекте
        for (Map.Entry<Integer, ArrayList<Integer>> entry : references.entrySet()) {
            Integer key = entry.getKey(); // получаем ключ
            ArrayList<Integer> valueList = entry.getValue(); // получаем значение
            ArrayList<Integer> valueListOld = referencesOld.get(key);

            for (Integer value : valueList) {
                // Если в старом объекте нет такого ключа или если есть ключ, но нет такого значения в старом датаобджекте, то надо создать новую строку
                if ((valueListOld == null) || (valueListOld.contains(value) == false)) {
                    PS_ref_ins.setInt(1, id);
                    PS_ref_ins.setInt(2, key);
                    PS_ref_ins.setInt(3, value);
                    PS_ref_ins.addBatch();
                } // иначе не трогаем
            }
        }
        PS_ref_ins.executeBatch();
        PS_ref_ins.close();

        // А теперь смотрим, может, нужно какие-то ссылки удалить. Обходим все ссылки в листе в старом датаобджекте
        for (Map.Entry<Integer, ArrayList<Integer>> entry : referencesOld.entrySet()) {
            Integer keyOld = entry.getKey(); // получаем ключ
            ArrayList<Integer> valueListOld = entry.getValue(); // получаем значение
            ArrayList<Integer> valueList = references.get(keyOld);

            for (Integer valueOld : valueListOld) {
                // Если в новом объекте нет такого ключа или если есть ключ, но нет такого значения в новом датаобджекте, то надо удалить строку из базы
                if ((valueList == null) || (valueList.contains(valueOld) == false)) {
                    PS_ref_del.setInt(1, id);
                    PS_ref_del.setInt(2, keyOld);
                    PS_ref_del.addBatch();
                } // иначе не трогаем
            }
        }
        PS_ref_del.executeBatch();
        PS_ref_del.close();

        CloseConnection(Con);
    }


}
