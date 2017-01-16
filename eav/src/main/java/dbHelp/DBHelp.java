package dbHelp;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

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

    private static int getAttrID( int ObjID, int ObjRefID) throws SQLException
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

    public static void deleteObject(int ID) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException, SQLException
    {
        Connection Con = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "nc","nc");
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
        PS = Con.prepareStatement("DELETE FROM OBJECTS WHERE OBJECT_ID =?");
        PS.setInt(1, ID);
        PS.executeUpdate();
        PS.close();
        CloseConnection(Con);
    }

    public static void main(String[] args) throws SQLException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        //DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "nc","nc");
        //int attr = getAttrID(10001, 20004);
        //System.out.println(attr);
        deleteObject(10001);
    }



}
