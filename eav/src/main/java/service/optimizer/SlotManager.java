package service.optimizer;

import entities.*;
import service.*;
import service.converter.*;
import service.id_filters.EventFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Hroniko on 08.04.2017.
 */
// Класс для работы со свободными слотами
public class SlotManager {

    private SlotSaver slotSaver = new SlotSaver();

    private LoadingServiceImp loadingService = new LoadingServiceImp();

    private UserServiceImp userService = new UserServiceImp();

    // 1) Метод для получения списка свободных слотов для юзера с айди user_id за период времени с date_start до date_end
    public ArrayList<Slot> getFreeSlots(SlotRequest slotRequest) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        return getFreeSlots(new Integer(slotRequest.getUser().trim()), slotRequest.getStart().trim(), slotRequest.getEnd().trim());
    }

    // 2) Метод для получения списка свободных слотов для юзера с айди user_id за период времени с date_start до date_end
    public ArrayList<Slot> getFreeSlots(Integer user_id, String date_start, String date_end) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        ArrayList<Slot> freeSlots = new ArrayList<>();
        // Пробуем преобразовать даты из строки к обычным датам
        LocalDateTime start = DateConverter.stringToDate(date_start);
        LocalDateTime end = DateConverter.stringToDate(date_end);

        if (start == null | end == null) return freeSlots; // Выходим из метода, если не удалось сконвертировать
        // Выбираем все события из расписаеия юзера за заданный период:
        ArrayList<Integer> ids = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_CURRENT_USER, EventFilter.BETWEEN_TWO_DATES, date_start, date_end));
        ArrayList<DataObject> aldo = loadingService.getListDataObjectByListIdAlternative(ids); // allSlots
        ArrayList<Event> events = new Converter().ToEvent(aldo);
        ArrayList<Slot> usageSlots = new ArrayList<>();
        // Обходим занятые слоты и переносим в список занятых слотов:
        for (int i = 0; i < events.size(); i++) {
            usageSlots.add(new Slot(events.get(i)));
        }
        // и надо еще как-то отсортировать а порядке увеличения даты // ок, сделал
        Collections.sort(usageSlots);

        // А затем обойти отсортированные занятые слоты и вытащить свободные:
        for (int i = 0; i < usageSlots.size(); i++) {
            LocalDateTime newend = usageSlots.get(i).getStart();
            // если есть свободное место
            if (start.isBefore(newend)) {
                freeSlots.add(new Slot(start, newend));
            }
            start = usageSlots.get(i).getEnd();
        }
        // И в конце может остаться кусочек в самом конце отрезка времени (или же весь отрезок, если занятых слотов в нем не было), добавляем его:
        if (start.isBefore(end)) {
            freeSlots.add(new Slot(start, end));
        }

        // и оставим точку сохранения в слот-сейвере:
        slotSaver.add(user_id,  events, usageSlots, freeSlots, date_start, date_end);

        return freeSlots;
    }


}
