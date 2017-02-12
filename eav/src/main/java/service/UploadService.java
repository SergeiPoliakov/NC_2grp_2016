package service;

import javax.jws.WebMethod;
import javax.jws.WebService;
import javax.jws.soap.SOAPBinding;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.TreeMap;

/**
 * Created by Hroniko on 12.02.2017.
 */
@WebService(name = "UploadService")
@SOAPBinding(style = SOAPBinding.Style.RPC)
public interface UploadService {

    @WebMethod
    void updateAvatar(int userId, String patch) throws SQLException,
            NoSuchMethodException, IllegalAccessException,
            IllegalArgumentException, InvocationTargetException;
}
