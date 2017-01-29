package dbHelp;

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


    private static int getObjID(String username) throws SQLException
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


    private static int getObjParamsByobjID(int objID) throws SQLException
    {
        Connection Con = getConnection();

        CloseConnection(Con);
        return objID;
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

    private static int getAttrID(int ObjID, int ObjRefID) throws SQLException
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


    private static String getValue(int ObjID, int AttrId) throws SQLException
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

    public void addNewUser(int ObjTypeID, String name, TreeMap<Integer, String> massAttr) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException {
        Connection Con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "nc","nc");
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



    public static void main(String[] args) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "nc","nc");
        //int attr = getAttrID(10001, 20004);
        //System.out.println(attr);
       // deleteObject(10001);
       // addTask();
        System.out.println(getValue(10001, 6));
    }



}
