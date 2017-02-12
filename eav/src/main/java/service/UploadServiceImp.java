package service;

import dbHelp.DBHelp;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;

/**
 * Created by Hroniko on 12.02.2017.
 * Работа с загрузкой-выгрузкой файлов (картинок и пр.)
 */
public class UploadServiceImp implements UploadService {
    private static volatile UploadServiceImp instance;

    public static UploadServiceImp getInstance() {
        if (instance == null)
            synchronized (DBHelp.class) {
                if (instance == null)
                    instance = new UploadServiceImp();
            }
        return instance;
    }

    public void updateAvatar(int userId, String patch) throws SQLException, NoSuchMethodException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        new DBHelp().updateAvatar(userId, patch);
    }
}
