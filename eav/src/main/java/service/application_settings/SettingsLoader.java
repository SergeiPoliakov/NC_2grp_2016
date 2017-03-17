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
    public static boolean load_status = false; // Статус загрузки настроек, тобы повторно не загружать

    public SettingsLoader() throws IOException {
        // Сначала подгрузим наш файлик с параметрами (если еще не подгрузили)
        if (! this.load_status)
            refreshProps();
    }

    private void refreshProps() throws IOException {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("app_settings.properties");
        this.property.load(inputStream);
        this.load_status = true;
    }

    // Метод получения значения настройки по ее имени
    public String getSetting(String setttingName){
        // Заходим в настройки и проверяем, есть ли настройка с таким именем в конфигурационном файле:
        // и если есть, то возвращаем ее значение:
        return this.property.getProperty(setttingName);
    }
}
