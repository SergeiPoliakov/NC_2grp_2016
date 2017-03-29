package service.calendar;

/**
 * Created by Hroniko on 05.03.2017.
 * Класс для работы с календарем гугл (загрузка и выгрузка событий)
 */

import com.google.api.client.auth.oauth2.Credential;
import com.google.api.client.extensions.java6.auth.oauth2.AuthorizationCodeInstalledApp;
import com.google.api.client.extensions.jetty.auth.oauth2.LocalServerReceiver;
import com.google.api.client.googleapis.auth.oauth2.GoogleAuthorizationCodeFlow;
import com.google.api.client.googleapis.auth.oauth2.GoogleClientSecrets;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.Calendar;
import com.google.api.services.calendar.model.*;
import com.google.api.services.calendar.model.Event;
import dbHelp.DBHelp;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class CalendarService {

    // 2017-03-05 Форматы даты, нужны для переконвертирования
    private static final SimpleDateFormat DATA_FORMAT_GOOGLE = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX"); // Rfc3339 гугловский формат Date and Time on the Internet: Timestamps
    private static final SimpleDateFormat DATA_FORMAT_LOCAL = new SimpleDateFormat("dd.MM.yyyy HH:mm"); // наш привычный локальный формат

    // 2017-03-05 Конвертер даты из гугловского формата в локальный (как в бд у нас)
    public static String dateConvert(DateTime googleTime) throws ParseException {
        return DATA_FORMAT_LOCAL.format(DATA_FORMAT_GOOGLE.parse(googleTime.toString()));
    }
    // 2017-03-05 Конвертер даты из локального формата в гугловский (Rfc3339)
    public static String dateConvert(Date localTime) throws ParseException {
        return DATA_FORMAT_GOOGLE.format(DATA_FORMAT_LOCAL.parse(localTime.toString()));
    }
    // 2017-03-05 Конвертер даты из локального формата в EventDateTime
    public static EventDateTime dateConvert(String localTime) throws ParseException {
        // System.out.println(localTime);
        localTime = DATA_FORMAT_GOOGLE.format(DATA_FORMAT_LOCAL.parse(localTime));
        // System.out.println(localTime);
        DateTime dateTime = new DateTime(localTime); // new DateTime("2017-03-05T09:00:00-07:00");
        EventDateTime eventDateTime = new EventDateTime()
                .setDateTime(dateTime)
                .setTimeZone("Europe/Moscow");
        return eventDateTime;
    }

    // 2017-03-07 Получение айдишника текущего пользователя, чтобы не писать везде new DBHelp().getCurrentUser().getId();
    private static int current_user_id() throws SQLException {
        return new DBHelp().getCurrentUser().getId();
    }

    // 2017-03-07 Создаем авторизованный объект прав доступа Credential
    public static Credential authorize() throws IOException, SQLException, GeneralSecurityException {
        // Формируем настройки календаря текущего юзера:
        CalendarSettings calendarSettings = new CalendarSettings(current_user_id());

        // Заходим в базу и подгружаем оттуда файл авторизации в директорию пользователя (совпадает с айди, например, 10001)
        // !!! Пока сделал так, что-то сериализация не через файл с гугловским объектом не пошла. А вообще надо сделать минуя ФС, загружать объект в BLOB сразу и читать оттуда в объект сразу
        String ok = new DBHelp().getCalendarFile(current_user_id());
        // Замечание! Если в базе нет файла авторизации, отправляем пользователя на страницу авторизации гугла и автоматически подгружаем файл оттуда

        // Загружаем файл с ключем с гугловского сервера в соотвествии с параметрами в client_secret.json в RESOURCES директории проекта
        InputStream in = Calendar.class.getResourceAsStream("/client_secret.json"); // собственно ссылка на настроечный JSON файл для авторизации к календарю
        GoogleClientSecrets clientSecrets = GoogleClientSecrets.load(calendarSettings.getJSON_FACTORY(), new InputStreamReader(in));

        // Создаем поток и вызываем запрос на авторизацию юзера
        GoogleAuthorizationCodeFlow flow =
                    new GoogleAuthorizationCodeFlow.Builder(calendarSettings.getHTTP_TRANSPORT(), calendarSettings.getJSON_FACTORY(),
                             clientSecrets, calendarSettings.getSCOPES())
                            .setDataStoreFactory(calendarSettings.getDATA_STORE_FACTORY())
                            .setAccessType("offline") // "оффлайн" позволяет загружать долгоживущие токены и обновлять их по мере необходимости без учстия юзера
                            .build();

        // Вытаскиваем из потока авторизационные данные
        Credential credential = new AuthorizationCodeInstalledApp(
                    flow, new LocalServerReceiver()).authorize("user");
        System.out.println("Получен новый токен для календаря: " + credential.getAccessToken());
        // Змечание! Так как используем DATA_STORE_FACTORY и сериализацию в файл через FileDataStoreFactory,
        // то файл автоматически создается в текущей папке юзера. Надо это потом обойти
        System.out.println("Права доступа сохранены в файл: " + calendarSettings.getDATA_STORE_DIR().getAbsolutePath() + "\\StoredCredential");

        // и сразу же заливаем их в репозиторий в базе к текущему пользователю, передавая имя файла (НЕ МЕНЯТЬ ИМЯ! у Гугла оно стандартное):
        if (ok == null){ // но только если в базе не было файла авторизации!
            new DBHelp().setCalendarFile("StoredCredential");
        }
        return credential;
    }



    // 2017-03-05 Создание и возврат авторизованного клиентского сервиса для Гугл-Календаря:
    public static com.google.api.services.calendar.Calendar getCalendarService()
            throws IOException, SQLException, GeneralSecurityException {
        // Настройки текущего юзера:
        CalendarSettings calendarSettings = new CalendarSettings(current_user_id());
        // Авторизуемся и получаем объект авторизации:
        Credential credential = authorize();
        // Подготавливаем календарь к работе, авторизуем календарь
        Calendar current_calendar =  new com.google.api.services.calendar.Calendar.Builder(
                calendarSettings.getHTTP_TRANSPORT(), calendarSettings.getJSON_FACTORY(), credential)
                .setApplicationName(calendarSettings.getAPPLICATION_NAME())
                .build();
        return current_calendar;
    }




    // 2017-03-05 Метод получения списка событий из гугл-календаря (входные параметры - имя календаря и ограничение на размер получаемого списка)
    // Например, from_calendar_name = "primary", size_list = 10
    public static ArrayList<entities.Event> getEventListCalendar(String from_calendar_name, Integer size_list)
            throws IOException, ParseException, SQLException, GeneralSecurityException {
        ArrayList<entities.Event> eventListCalendar = new ArrayList<>();

        // Создаем новый клиентский сервис CalendarService с авторизацией
        // Замечание! Не путать с com.google.api.services.calendar.model.Calendar class.
        com.google.api.services.calendar.Calendar service = getCalendarService();

        // Ограничение на начало событий, то есть будем получать только те события, которые еще не произошли
        DateTime now = new DateTime(System.currentTimeMillis());

        // Получаем списком следующих [size_list]-штук событий из календаря с именем [from_calendar_name] юзера:
        Events events = service.events().list(from_calendar_name)
                .setMaxResults(size_list)
                .setTimeMin(now)
                .setOrderBy("startTime")
                .setSingleEvents(true)
                .execute();

        // И переносим их в массив НАШИХ НОРМАЛЬНЫХ ЛОКАЛЬНЫХ СОБЫТИЙ, попутно выводя их в консоль:
        List<Event> items = events.getItems();
        if (items.size() == 0) {
            System.out.println("Нет ожидающих событий");
        } else {
            System.out.println("Предстоящие события: ");
            for (Event event : items) {


                // Начало события:
                DateTime start = event.getStart().getDateTime();
                if (start == null) {
                    start = event.getStart().getDate(); // Формат даты 2017-03-05T19:00:00.000+03:00 // надо победить этот формат // ок, победил
                }
                String date_start = dateConvert(start); // переконвертируем


                // Окончание события:
                DateTime end = event.getEnd().getDateTime();
                if (end == null) {
                    end = event.getEnd().getDate(); // Формат даты 2017-03-05T19:00:00.000+03:00
                }
                String date_end = dateConvert(end); // переконвертируем


                // Слоздаем новое локальное событие, переносим в него соотвествующие поля
                entities.Event local_event = new entities.Event();
                // переносим в него соотвествующие поля:
                // ---- local_event.setId(Integer.parseInt(event.getId())); // int id; // 1 // Гугл использует айдишники вида "s91gkctdiqco2ljl8prtipc6nk_20170305T160000Z", поэтому надо как-то иначе получать айди. Может быть, сравнивать с бд
                // ---- local_event.setHost_id(Integer.parseInt(event.getCreator().getId())); // int host_id; // 141 // Аналогично предыдущему
                local_event.setName(event.getSummary()); // String name; // 3
                local_event.setDate_begin(date_start); // String date_begin; // 101
                local_event.setDate_end(date_end); // String date_end; // 102

                String priority;
                try{
                    priority = event.getColorId(); // String priority; // 105 // !!! Использую ColorId вместо приоритета!!!
                    if (priority.equals("11")){ // #DC2127 // Красный, т.е. высокий
                        local_event.setPriority("Style1");
                    }
                    else if (priority.equals("6")){ // #FFB878 // Оранжевый, т.е. средний
                        local_event.setPriority("Style2");
                    }
                    else if (priority.equals("9")){ // #5484ED // Синий, т.е. низкий
                        local_event.setPriority("Style3");
                    }
                    else {
                        local_event.setPriority(""); // ничего
                    }

                }catch (Exception e){
                    System.out.print(e.getMessage());
                    local_event.setPriority(""); // ничего
                }

                local_event.setInfo(event.getDescription()); // String info; // 104

                // И отправляем в список:
                eventListCalendar.add(local_event);
                System.out.println("Событие: " + event.getSummary() + ", начало " + date_start + ", окончание " + date_end); //System.out.printf("%s (%s)\n", event.getSummary(), start);
            }
        }
        return null; //eventListCalendar;
    }




    // 2017-03-05 Метод отправки списка событий из бд в гугл-календарь (входные параметры - имя календаря и список локальных событий)
    public static void setEventListCalendar(String to_calendar_name, ArrayList<entities.Event> eventListCalendar) throws IOException, ParseException, SQLException, GeneralSecurityException {
        // Выставляем настройки текущего юзера для календаря:
        CalendarSettings calendarSettings = new CalendarSettings(current_user_id());

        // Создаем новый клиентский сервис CalendarService с авторизацией
        com.google.api.services.calendar.Calendar service = getCalendarService();

        if (eventListCalendar.size() == 0) {
            System.out.println("Получен пустой список для синхронизации с календарем!");
        } else {
            System.out.println("Предстоящие события: ");
            for (entities.Event local_event : eventListCalendar) {

                // 1) Создаем новое календарное событие, переносим в него соотвествующие поля из локального
                Event event = new Event();

                // 2) Переносим в него соотвествующие поля:
                // event.setId(String.valueOf(local_event.getId())); // int id; // 1
                // event.setCreator(new Event.Creator().setId(String.valueOf(local_event.getHost_id())));  // int host_id; // 141
                event.setSummary(local_event.getName()); // String name; // 3
                event.setStart(dateConvert(local_event.getDate_begin()));   // String date_begin; // 101
                event.setEnd(dateConvert(local_event.getDate_end())); // String date_end; // 102
                // event.setStatus(local_event.getPriority()); // String priority; // 105 // !!! Использую статус вместо приоритета!!!
                // Приоритет:
                String priority = local_event.getPriority();
                if (priority.equals("Style1")){
                    event.setColorId("11"); // #DC2127 // Красный
                }
                else if (priority.equals("Style2")){
                    event.setColorId("6"); // #FFB878 // Оранжевый
                }
                else if (priority.equals("Style3")){
                    event.setColorId("9"); // #5484ED // Синий
                }
                else {
                    event.setColorId("10"); // #51B749 // Зеленый
                }
                // Описание:
                event.setDescription(local_event.getInfo()); // String info; // 104

                /* Пока отключил
                // Выставление повторения: ежедневно в количестве двух раз
                String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
                event.setRecurrence(Arrays.asList(recurrence));
                */

                // Выставление участников - пригодится, когда будем встречи размещать
                EventAttendee[] attendees = new EventAttendee[]{
                        new EventAttendee().setEmail(calendarSettings.getSERVICE_GOOGLE_MEIL()),  //.setEmail("hroniko@bk.ru"),
                        // new EventAttendee().setEmail(SERVICE_GOOGLE_MEIL),
                };
                event.setAttendees(Arrays.asList(attendees));

                // Выставление способов напоминания - пригодится, когда будем работать с напоминаниями
                EventReminder[] reminderOverrides = new EventReminder[]{
                        new EventReminder().setMethod("email").setMinutes(24 * 60),
                        new EventReminder().setMethod("popup").setMinutes(10),
                        new EventReminder().setMethod("sms").setMinutes(10),
                };
                Event.Reminders reminders = new Event.Reminders()
                        .setUseDefault(false)
                        .setOverrides(Arrays.asList(reminderOverrides));
                event.setReminders(reminders);


                // 3) И переносим подготовленное событие в удаленный календарь (EXECUTE его)
                event = service.events().insert(to_calendar_name, event).execute(); // где [to_calendar_name] - Идентификатор календаря, в котороый записываем событие, по умолчанию "primary"

                System.out.printf("Событие скопировано из БД в удаленный календарь: %s\n", event.getHtmlLink());
            }
        }
    }





    /*
    // 2017-03-05 Тестовый метод для синхронизации тестового события, просто добавляет событие
    public static void newTestEventCalendar() throws IOException, SQLException, GeneralSecurityException {
        int user_id = new DBHelp().getCurrentUser().getId();
        // Настройки текущего юзера:
        CalendarSettings calendarHelp = new CalendarSettings(user_id);

        com.google.api.services.calendar.Calendar service =
                getCalendarService();

        Event event = new Event()
                .setSummary("Новое событие 2017")
                .setLocation("20 лет октября, Воронеж, 394006")
                .setDescription("Проверочное событие для синхронизации с гугл-календарем");

        DateTime startDateTime = new DateTime("2017-03-07T10:00:00-07:00");
        EventDateTime start = new EventDateTime()
                .setDateTime(startDateTime)
                .setTimeZone("Europe/Moscow");
        event.setStart(start);

        DateTime endDateTime = new DateTime("2017-03-07T17:00:00-07:00");
        EventDateTime end = new EventDateTime()
                .setDateTime(endDateTime)
                .setTimeZone("Europe/Moscow");
        event.setEnd(end);

        String[] recurrence = new String[]{"RRULE:FREQ=DAILY;COUNT=2"};
        event.setRecurrence(Arrays.asList(recurrence));

        EventAttendee[] attendees = new EventAttendee[]{
                new EventAttendee().setEmail(calendarHelp.getSERVICE_GOOGLE_MEIL()),
                new EventAttendee().setEmail(calendarHelp.getSERVICE_GOOGLE_MEIL()),
        };
        event.setAttendees(Arrays.asList(attendees));

        EventReminder[] reminderOverrides = new EventReminder[]{
                new EventReminder().setMethod("email").setMinutes(24 * 60),
                new EventReminder().setMethod("popup").setMinutes(10),
        };
        Event.Reminders reminders = new Event.Reminders()
                .setUseDefault(false)
                .setOverrides(Arrays.asList(reminderOverrides));
        event.setReminders(reminders);

        String calendarId = "primary";
        event = service.events().insertForUser(calendarId, event).execute();
        System.out.printf("Событие синхронизировано с календарем: %s\n", event.getHtmlLink());
    }
    */

}