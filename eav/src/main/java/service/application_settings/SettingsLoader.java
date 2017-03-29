package service.application_settings;

import java.io.*;
import java.util.Properties;

/**
 * Created by Hroniko on 17.03.2017.
 * Класс для загрузки настроек приложения из property-файла eav\src\main\resources\app_settings.properties
 */
public class SettingsLoader {

    //private Properties property;
    public static final Properties property = new Properties();
    private static boolean load_status = false; // Статус загрузки настроек, тобы повторно не загружать

    public SettingsLoader() {

    }

    private static void refreshProps() throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("app_settings.properties");
        property.load(inputStream);
        load_status = true;
    }

    // Метод получения значения настройки по ее имени
    public static String getSetting(String setttingName) throws IOException {
        // Сначала проверяем, загружали ли мы настройки уже ранее, и если нет, то загружаем их:
        if (! load_status) refreshProps();
        // Заходим в настройки и проверяем, есть ли настройка с таким именем в конфигурационном файле:
        // и если есть, то возвращаем ее значение:
        return property.getProperty(setttingName);
    }
}
