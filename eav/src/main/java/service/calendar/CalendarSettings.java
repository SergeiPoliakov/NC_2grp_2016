package service.calendar;

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.store.FileDataStoreFactory;
import com.google.api.services.calendar.CalendarScopes;
import dbHelp.DBHelp;
import javafx.scene.control.Separator;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

/**
 * Created by Hroniko on 07.03.2017.
 * Вспомогательный класс для хранения настроек календаря текущего юзера
 */
public class CalendarSettings {


    // Название сервиса, нужно для сервера гугл
    private String APPLICATION_NAME;
    // Почтовый адрес юзера в качестве идентификатора календаря
    private String SERVICE_GOOGLE_MEIL;
    // Путь для временного хранения пользовательских учетных данных (идентификационного файла календаря)
    private java.io.File DATA_STORE_DIR;
    private FileDataStoreFactory DATA_STORE_FACTORY;
    private JsonFactory JSON_FACTORY;
    private HttpTransport HTTP_TRANSPORT;
    private List<String> SCOPES;

    public CalendarSettings(int user_id) throws SQLException, GeneralSecurityException, IOException {
        this.APPLICATION_NAME = "NC";
        this.SERVICE_GOOGLE_MEIL = new DBHelp().getCurrentUser().getEmail();
        DATA_STORE_DIR = new java.io.File(System.getProperty("user.home"), ".credentials/2/calendar/" + user_id);
        this.HTTP_TRANSPORT = GoogleNetHttpTransport.newTrustedTransport();
        this.DATA_STORE_FACTORY = new FileDataStoreFactory(DATA_STORE_DIR);
        this.JSON_FACTORY = JacksonFactory.getDefaultInstance();
        this.SCOPES = Arrays.asList(CalendarScopes.CALENDAR);
    }

    public String getAPPLICATION_NAME() {
        return APPLICATION_NAME;
    }

    public void setAPPLICATION_NAME(String APPLICATION_NAME) {
        this.APPLICATION_NAME = APPLICATION_NAME;
    }

    public String getSERVICE_GOOGLE_MEIL() {
        return SERVICE_GOOGLE_MEIL;
    }

    public void setSERVICE_GOOGLE_MEIL(String SERVICE_GOOGLE_MEIL) {
        this.SERVICE_GOOGLE_MEIL = SERVICE_GOOGLE_MEIL;
    }

    public File getDATA_STORE_DIR() {
        return DATA_STORE_DIR;
    }

    public void setDATA_STORE_DIR(File DATA_STORE_DIR) {
        this.DATA_STORE_DIR = DATA_STORE_DIR;
    }

    public FileDataStoreFactory getDATA_STORE_FACTORY() {
        return DATA_STORE_FACTORY;
    }

    public void setDATA_STORE_FACTORY(FileDataStoreFactory DATA_STORE_FACTORY) {
        this.DATA_STORE_FACTORY = DATA_STORE_FACTORY;
    }

    public JsonFactory getJSON_FACTORY() {
        return JSON_FACTORY;
    }

    public void setJSON_FACTORY(JsonFactory JSON_FACTORY) {
        this.JSON_FACTORY = JSON_FACTORY;
    }

    public HttpTransport getHTTP_TRANSPORT() {
        return HTTP_TRANSPORT;
    }

    public void setHTTP_TRANSPORT(HttpTransport HTTP_TRANSPORT) {
        this.HTTP_TRANSPORT = HTTP_TRANSPORT;
    }

    public List<String> getSCOPES() {
        return SCOPES;
    }

    public void setSCOPES(List<String> SCOPES) {
        this.SCOPES = SCOPES;
    }
}
