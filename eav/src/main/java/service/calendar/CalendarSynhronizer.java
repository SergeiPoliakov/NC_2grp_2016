package service.calendar;

import entities.DataObject;
import entities.Event;
import service.id_filters.EventFilter;

import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import com.google.common.cache.LoadingCache;
import service.LoadingServiceImp;
import service.cache.DataObjectCache;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created by Hroniko on 07.03.2017.
 * Класс для синхронизации оракловской базы с гугл-календарем
 * Думаю, лучше иметь настройки для юзера, где будет прописана периодичность синхронизации,
 * а не все время по каждому действию с базой дублировать сразу и в календаре
 * Но это все потом тогда, когда будут в базе отдельные настройки
 */
public class CalendarSynhronizer {

    private LoadingCache<Integer, DataObject> doCache = DataObjectCache.getLoadingCache();
    private LoadingServiceImp loadingService = new LoadingServiceImp();


    private ArrayList<DataObject> getListDataObject(Map<Integer, DataObject> map) {
        ArrayList<DataObject> list = new ArrayList<>();

        for(Map.Entry<Integer, DataObject> e : map.entrySet()) {
            list.add(e.getValue());
        }

        return list;
    }


    // Синхронизация календаря текущего юзера: (тестовое)
    public void synhronizedCurrentUser(String calendar_name) throws GeneralSecurityException, SQLException, IOException, ParseException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        // 1) Подгружаем события из удаленного календаря:
        ArrayList<Event> eventList = CalendarService.getEventListCalendar(calendar_name, 50);

        /*ArrayList<DataObject> doList = new Converter().toDO(eventList); // закомментировал пока, птому что с календаря приходят с другим айли, надо это обыграть
        for (DataObject doo : doList) {
            loadingService.updateDataObject(doo);
        }*/

        //Calendar.newTestEventCalendar(); // была моя тестовая штука

        // 2) Подгружаем события В удаленный календарь из оракла
        ArrayList<Integer> ilo = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_CURRENT_USER));
        ArrayList<DataObject> aldo = new ArrayList<>();
        // Работа с кэшем
        System.out.println("Размер кэша до обновления страницы " + doCache.size());
        try {
            System.out.println("Ищем в кэше список событий");
            Map<Integer, DataObject> map = null;
            map = doCache.getAll(ilo);
            aldo = getListDataObject(map);
            System.out.println("Размер кэша после добавления " + doCache.size());
            ArrayList<Event> events = new ArrayList<>(aldo.size());
            for (DataObject dataObject: aldo
                    ) {
                Event event = new Event(dataObject);
                events.add(event);
            }
            CalendarService.setEventListCalendar("primary", events);
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
