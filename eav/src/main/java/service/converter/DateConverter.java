package service.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

/**
 * Created by Hroniko on 08.04.2017.
 */
public class DateConverter {
    // 2017-04-08 Конвертер даты из Java-8 строку
    public static String dateToString(LocalDateTime ldt) throws ParseException {
        String res = null;
        try{
            res = ldt.format(DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm"));
        }
        catch (Exception e){
            System.out.println("Ошибка конвертации даты: " + e.getMessage());
        }
        return res;
    }

    // 2017-04-08 Конвертер строки в дату из Java-8
    public static LocalDateTime stringToDate(String str) throws ParseException {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime res = null;
        try{
            res = LocalDateTime.parse(str, formatter);
        }
        catch (Exception e){
            System.out.println("Ошибка конвертации даты: " + e.getMessage());
        }
        return res;
    }

    public static long duration(String start, String end) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        long timeStart = format.parse(start).getTime();
        long timeEnd = format.parse(end).getTime();
        long diff = timeEnd - timeStart;

        return TimeUnit.MILLISECONDS.toMinutes(diff);
    }

    public static Duration dlitelnost(String start, String end) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy HH:mm");

        LocalDateTime date_start = stringToDate(start);
        LocalDateTime date_end = stringToDate(end);

        Duration duration = Duration.between(date_start, date_end);

        return duration;
    }

    // Для работы с таймером обратного отсчета

    // 2017-05-08 Конвертер даты из Java-8 в строку типа countdown для таймера обратного отсчета на страницах встречи и событий
    public static String dateToCountdown(LocalDateTime ldt) throws ParseException {
        String res = null;
        try{
            res = ldt.format(DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm"));
        }
        catch (Exception e){
            System.out.println("Ошибка конвертации даты: " + e.getMessage());
        }
        return res;
    }

    // 2017-05-08 Конвертер даты из строки "dd.MM.yyyy HH:mm" в строку типа countdown "MM/dd/yyyy HH:mm" для таймера обратного отсчета на страницах встречи и событий
    public static String stringToCountdown(String str) throws ParseException {
        LocalDateTime ldt = stringToDate(str);
        String res = dateToCountdown(ldt);
        return res;
    }

}
