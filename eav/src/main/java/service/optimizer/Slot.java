package service.optimizer;

import entities.Event;
import service.converter.DateConverter;

import java.text.ParseException;
import java.time.LocalDateTime;

/**
 * Created by Hroniko on 08.04.2017.
 */
// Класс для хранения свободного слота
public class Slot implements Comparable<Slot> {

    private LocalDateTime start;

    private LocalDateTime end;

    private String string_start;

    private String string_end;

    private DateConverter dateConverter = new DateConverter();

    public Slot() {
    }

    public Slot(LocalDateTime start, LocalDateTime end) throws ParseException {
        this.start = start;
        this.end = end;
        this.string_start = dateConverter.dateToString(start);
        this.string_end = dateConverter.dateToString(end);
    }

    public Slot(String string_start, String string_end) throws ParseException {
        this.string_start = string_start;
        this.string_end = string_end;
        this.start = dateConverter.stringToDate(string_start);
        this.end = dateConverter.stringToDate(string_end);
    }

    public Slot(Event event) throws ParseException {
        this.string_start = event.getDate_begin();
        this.string_end = event.getDate_end();
        this.start = dateConverter.stringToDate(this.string_start);
        this.end = dateConverter.stringToDate(this.string_end);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public void setStart(LocalDateTime start) throws ParseException {
        this.start = start;
        this.string_start = DateConverter.dateToString(start);
    }

    public LocalDateTime getEnd() {
        return end;
    }

    public void setEnd(LocalDateTime end) throws ParseException {
        this.end = end;
        this.string_end = DateConverter.dateToString(end);
    }

    public String getString_start() {
        return string_start;
    }

    public void setString_start(String string_start) throws ParseException {
        this.string_start = string_start;
        this.start = DateConverter.stringToDate(string_start);
    }

    public String getString_end() {
        return string_end;
    }

    public void setString_end(String string_end) throws ParseException {
        this.string_end = string_end;
        this.end = DateConverter.stringToDate(string_end);
    }

    @Override // Метод сравнения
    public int compareTo(Slot otherSlot) {

        if (otherSlot.getStart().isAfter(this.getEnd())){
            return - 1;
        }
        else if (otherSlot.getStart().isBefore(this.getEnd())){
            return 1;
        }
        else {
            return 0;
        }
    }
}
