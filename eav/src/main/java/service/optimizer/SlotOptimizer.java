package service.optimizer;

import entities.Event;
import entities.Meeting;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

/**
 * Created by Hroniko on 09.04.2017.
 */
// Класс для оптимизации встреч
public class SlotOptimizer {

    // 1) Пользовательский оптимизатор расписания
    public void optimizeEvents(Meeting meeting) throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        ArrayList<Event> overlapEvents = new SlotManager().getOverlapEvents(meeting);

        if (overlapEvents == null) return;


    }


    // Админмкиф оптимизатор встречи

}
