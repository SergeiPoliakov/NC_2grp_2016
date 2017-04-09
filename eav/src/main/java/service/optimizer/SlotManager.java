package service.optimizer;

import com.google.common.cache.LoadingCache;
import entities.*;
import service.*;
import service.cache.DataObjectCache;
import service.converter.*;
import service.id_filters.EventFilter;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 08.04.2017.
 */
// Класс для работы со свободными слотами
public class SlotManager {

    private SlotSaver slotSaver = new SlotSaver();

    private LoadingServiceImp loadingService = new LoadingServiceImp();
    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();


    private UserServiceImp userService = new UserServiceImp();

    // 1) Метод для получения списка свободных слотов для юзера с айди user_id за период времени с date_start до date_end
    public ArrayList<Slot> getFreeSlots(SlotRequest slotRequest) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException {
        return getFreeSlots(new Integer(slotRequest.getUser().trim()), new Integer(slotRequest.getMeeting().trim()), slotRequest.getStart().trim(), slotRequest.getEnd().trim());
    }

    // 2) Метод для получения списка свободных слотов для юзера с айди user_id за период времени с date_start до date_end
    public ArrayList<Slot> getFreeSlots(Integer user_id, Integer meeting_id, String date_start, String date_end) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException {
        ArrayList<Slot> freeSlots = new ArrayList<>();
        ArrayList<Slot> freeSlotsForMeeting = new ArrayList<>();  // свободные слоты для встечи
        // Пробуем преобразовать даты из строки к обычным датам
        LocalDateTime start = DateConverter.stringToDate(date_start);
        LocalDateTime end = DateConverter.stringToDate(date_end);

        if (start == null | end == null) return freeSlots; // Выходим из метода, если не удалось сконвертировать
        // Выбираем все события из расписаеия юзера за заданный период:
        ArrayList<Integer> ids = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_CURRENT_USER, EventFilter.BETWEEN_TWO_DATES, date_start, date_end));
        ArrayList<DataObject> aldo = loadingService.getListDataObjectByListIdAlternative(ids); // allSlots
        ArrayList<Event> events = new Converter().ToEvent(aldo);
        ArrayList<Slot> usageSlots = new ArrayList<>();
        Meeting meeting = new Meeting(doCache.get(meeting_id));
        // Обходим занятые слоты и переносим в список занятых слотов:
        for (Event event : events) {
            usageSlots.add(new Slot(event));
        }
        // и надо еще как-то отсортировать в порядке увеличения даты // ок, сделал
        Collections.sort(usageSlots);

        // А затем обойти отсортированные занятые слоты и вытащить свободные:
        for (Slot usageSlot : usageSlots) {
            LocalDateTime newend = usageSlot.getStart();
            // если есть свободное место
            if (start.isBefore(newend)) {
                freeSlots.add(new Slot(start, newend));
            }
            start = usageSlot.getEnd();
        }
        // И в конце может остаться кусочек в самом конце отрезка времени (или же весь отрезок, если занятых слотов в нем не было), добавляем его:
        if (start.isBefore(end)) {
            freeSlots.add(new Slot(start, end));
        }

        for (Slot freeSlot : freeSlots
             ) {
            System.out.println(DateConverter.duration(freeSlot.getString_start(), freeSlot.getString_end()));
        }

        int count = 0;
        for (Slot freeSlot : freeSlots) {
            String startTime = freeSlot.getString_start();
            String endTime = freeSlot.getString_end();
            long duration = DateConverter.duration(startTime, endTime);

            if (duration >= Long.parseLong(meeting.getDuration())) {
                System.out.println("ПРОДОЛЖИТЕЛЬНОСТЬ СВОБОДНОГО СЛОТА" + duration);
                System.out.println("ПРОДОЛЖИТЕЛЬНОСТЬ ВСТРЕЧИ" + Long.parseLong(meeting.getDuration()));
                freeSlotsForMeeting.add(freeSlot);
                count++;
            }
        }
        System.out.println("КОЛИЧЕСТВО СВОБОДНЫХ СЛОТОВ :" + count);

        // и оставим точку сохранения в слот-сейвере:
        slotSaver.add(user_id,  events, usageSlots, freeSlotsForMeeting, date_start, date_end);

        return freeSlotsForMeeting;
    }


}
