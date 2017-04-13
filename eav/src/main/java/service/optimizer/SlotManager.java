package service.optimizer;

import com.google.common.cache.LoadingCache;
import entities.*;
import service.*;
import service.cache.DataObjectCache;
import service.converter.*;
import service.id_filters.EventFilter;
import service.search.SearchParser;

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
        ArrayList<Integer> listIds = new ArrayList<>();
        ArrayList<String> listSting = SearchParser.parse(slotRequest.getUser());
        assert listSting != null;
        for (String str: listSting
                ) {
            listIds.add(Integer.parseInt(str));
        }
        return getFreeSlots(listIds, new Integer(slotRequest.getMeeting().trim()), slotRequest.getStart().trim(), slotRequest.getEnd().trim());
    }

    // 2) Метод для получения списка свободных слотов для всех юзеров встречи за период времени с date_start до date_end
    public ArrayList<Slot> getFreeSlots(ArrayList<Integer> users, Integer meeting_id, String date_start, String date_end) throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException, ExecutionException {
        ArrayList<Slot> freeSlots = new ArrayList<>();
        ArrayList<Slot> freeSlotsForMeeting = new ArrayList<>();  // свободные слоты для встечи
        // Пробуем преобразовать даты из строки к обычным датам
        LocalDateTime start = DateConverter.stringToDate(date_start);
        LocalDateTime end = DateConverter.stringToDate(date_end);

        if (start == null | end == null) return freeSlots; // Выходим из метода, если не удалось сконвертировать
        // Выбираем все события из расписаеия юзера за заданный период:
        ArrayList<Integer> ids = new ArrayList<>();
        for (Integer user: users
                ) {
            ArrayList<Integer> list = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_USER_WITH_ID, String.valueOf(user), EventFilter.BETWEEN_TWO_DATES, date_start, date_end));
            ids.addAll(list);
        }

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

        int user_id = userService.getObjID(userService.getCurrentUsername());

        // и оставим точку сохранения в слот-сейвере:
        slotSaver.add(user_id, meeting_id, events, usageSlots, freeSlotsForMeeting, date_start, date_end);

        return freeSlotsForMeeting;
    }

    // 3) Метод проверки перекрытия расписания (наложения задач расписания) на данную встречу
    public ArrayList<Event> getOverlapEvents(Meeting meeting) throws SQLException, ParseException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ArrayList<Event> result = null;
        // Вытаскиваем все копии встречи (чтобы в них найти подходящую данному юзеру):
        ArrayList<Event> duplicates = meeting.getDuplicates();
        Integer user_id = userService.getCurrentUser().getId();
        // Обходим список дубликатов в поисках события с подвешенным юзером:
        Boolean success_flag = false;
        Event event = null;
        for(int i = 0; i < duplicates.size(); i++){ // Event event : duplicates
            event = duplicates.get(i);
            if (user_id.equals(event.getHost_id())){
                // Нашли! И выходим, меняя флаг
                success_flag = true;
                break;
            }
        }
        if (success_flag){
            // Вытаскиваем из базы все, что встреча перекрыла
            String date_start = event.getDate_begin();
            String date_end = event.getDate_end();
            ArrayList<Integer> idsOverlapEvents = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_CURRENT_USER, EventFilter.BETWEEN_TWO_DATES, date_start, date_end ));
            ArrayList<DataObject> aldoOverlapEvents = loadingService.getListDataObjectByListIdAlternative(idsOverlapEvents);
            result = new Converter().ToEvent(aldoOverlapEvents);
        }
        return  result;
    }

    // 4) Метод для получения списка занятых слотов на основе данных из переданного списка событий за период времени с date_start до date_end
    public ArrayList<Slot> getUsageSlots(ArrayList<Event> events, String date_start, String date_end) throws ParseException {
        ArrayList<Slot> usageSlots = null;
        // Конвертируем даты:
        LocalDateTime start = DateConverter.stringToDate(date_start);
        LocalDateTime end = DateConverter.stringToDate(date_end);

        if (start == null | end == null) return usageSlots; // Выходим из метода, если не удалось сконвертировать

        usageSlots = new ArrayList<>();

        // Обходим занятые слоты и переносим в список занятых слотов:
        for (Event event : events) {
            usageSlots.add(new Slot(event));
        }
        // Сортируем в порядке увеличения даты
        Collections.sort(usageSlots);

        return usageSlots;
    }

    // 5) Метод для получения списка свободных слотов, в которые может поместиться наша встреча, на основе данных из переданного списка событий за период времени с date_start до date_end
    public ArrayList<Slot> getFreeSlots(Integer meeting_id, ArrayList<Event> events, String date_start, String date_end) throws ParseException, ExecutionException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {
        ArrayList<Slot> freeSlots = null;
        ArrayList<Slot> freeSlotsForMeeting = new ArrayList<>();  // свободные слоты для встечи
        // Пробуем преобразовать даты из строки к обычным датам
        LocalDateTime start = DateConverter.stringToDate(date_start);
        LocalDateTime end = DateConverter.stringToDate(date_end);

        if (start == null | end == null) return freeSlots; // Выходим из метода, если не удалось сконвертировать
        // Иначе продолжаем. Получаем список занятых слотов за данный период:
        ArrayList<Slot> usageSlots = getUsageSlots(events, date_start, date_end);
        if (usageSlots == null) return freeSlots; // Выходим из метода, если не удалось получить занятые слоты
        // Иначе продолжаем. Формируем список свободных слотов за данный период:
        freeSlots = new ArrayList<>();

        // Для этого обходим отсортированные занятые слоты и вытаскиваем свободные:
        for (int i = 0; i < usageSlots.size(); i++){
            Slot slot = usageSlots.get(i);
            LocalDateTime new_end = slot.getStart();
            // если есть свободное место
            if (start.isBefore(new_end)) {
                freeSlots.add(new Slot(start, new_end));
            }
            start = slot.getEnd();
        }

        // И в конце может остаться кусочек в самом конце отрезка времени (или же весь отрезок, если занятых слотов в нем не было), добавляем его:
        if (start.isBefore(end)) {
            freeSlots.add(new Slot(start, end));
        }

        // А теперь выбираем только те слоты, куда сможет поместиться наша встреча
        Meeting meeting = new Meeting(doCache.get(meeting_id));
        for (Slot freeSlot : freeSlots) {
            long duration = DateConverter.duration(freeSlot.getString_start(), freeSlot.getString_end());
            if (duration >= Long.parseLong(meeting.getDuration())) {
                freeSlotsForMeeting.add(freeSlot);
            }
        }
        return freeSlotsForMeeting;
    }


}
