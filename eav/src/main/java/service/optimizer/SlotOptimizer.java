package service.optimizer;

import dbHelp.DBHelp;
import entities.DataObject;
import entities.Event;
import entities.Meeting;
import entities.Notification;
import service.LoadingServiceImp;
import service.converter.Converter;
import service.converter.DateConverter;
import service.id_filters.EventFilter;
import service.notifications.NotificationService;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.TreeMap;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 09.04.2017.
 */
// Класс для оптимизации встреч
public class SlotOptimizer {

    private static LoadingServiceImp loadingService = new LoadingServiceImp();

    // 2017-04-15 1) Пользовательский оптимизатор расписания
    public static void optimizeItForUser(Integer user_id, Integer meeting_id, String opt_period_date_start, String opt_period_date_end) throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException, ExecutionException, CloneNotSupportedException {
        // Среди полученных событий возможны сочетания вариантов из обычных событий расписания юзера, события-копии текущей оптимизируемой встречи, и, что важно, событий-копий других встреч!
        // Это надо аккуратно обыграть

        // 1 Получаем из сейвера финальную точку сохранения по составному ключу:
        ArrayList<Event> events = SlotSaver.getEventFinalPoint(user_id, meeting_id, opt_period_date_start, opt_period_date_end);

        // 2 Разделяем полученные события на два типа: обычные события расписания и события-отображения встреч
        ArrayList<Event> baseEvents = new ArrayList<>();
        ArrayList<Event> anyDuplicateEvents = new ArrayList<>();

        for (Event event : events) { // такой костыль, потому что непонятно почему в базу не сохрняется поле у обычных событий
            if (event.getType_event() == null){
                event.setType_event(Event.BASE_EVENT);
                baseEvents.add(event);
                break;
            }
            if (event.getType_event().equals(Event.BASE_EVENT)) {
                baseEvents.add(event);
            } else { // else if (event.getType_event().equals(Event.DUPLICATE_EVENT)) {
                anyDuplicateEvents.add(event);
            }
        }
        // 3 Среди списка событий-дубликатов ищем то, которое мы оптимизируем, для этого используем информацию об айди встречи,
        // по ней получаем встречу и проверяем, есть ли айди какой-либо копии из duplicateEvents среди дубликатов встречи:
        Meeting meeting = new SlotManager().getMeeting(meeting_id); // получаем встречу
        ArrayList<Integer> ids_duplicates = meeting.getDuplicateIDs(); // в этот список получаем из встречи все айдишники дубликатов
        Event optimDuplicate = null; // заготовка для искомого среди всех дубликатов дубликата оптимизируемой встречи
        for (int i = 0; i < anyDuplicateEvents.size(); i++) { // не через forech, потому что он делает копию, и тогда contains не сработает // Обходим весь список событий-дуликатов, полученных из точки сохранения, и проверяем кандидатов на совпадение айди с одним из айди копий встречи
            Event candidate = anyDuplicateEvents.get(i);
            if (ids_duplicates.contains(candidate.getId())) {
                // Нашли его! Сохраняем, удаляем из списка прочих дубликатов, чтобы он там не мешал нам, и выходим
                optimDuplicate = candidate;
                anyDuplicateEvents.remove(candidate);
                break;
            }
        }

        // 4 Если не нашли дубликат оптимизируемой встречи (а это фигово, такого, по идее, быть не может, но вдруг, тогда надо подстраховаться), то выходим из метода
        if (optimDuplicate == null) {
            System.out.println("Случилось невозможное! Пока мы оптимизировали, куда-то делся дубликат Вашей встречи из Вашего же расписания...");
            System.out.println("Не зная как быть дальше, оптимизатор прекратил свою работу. Проверьте, все ли в порядке с Вашим расписанием?");

            // Сохраняем сообщение
            String message = "Внимание! Непредвиденная ошибка оптимизации! Пожалуйста, проверьте свое расписание на наличие совпадающих по времени задач";
            SlotSaver.addMessage(user_id, meeting_id, message, opt_period_date_start, opt_period_date_end);

            return;
        }

        // 5 Если все в порядке, прежде чем что-либо оптимизировать, проверяем, нет ли перекрытий прочих событий-дубликатов с нашим дубликатом
        ArrayList<Event> overlapDuplicates = new SlotManager().getOverlapEvents(anyDuplicateEvents, optimDuplicate);

        // 6 Если есть перекрытия, нужно отправить пользователю уведомление о перекрытии в расписании двух встреч,
        // и тут уже нет смысла дальше оптимизировать, пользователь должен препринять сначала меры для изменения перекрытия
        // (такое перекрытие убирается только вручную - передвинуть или отменить одну из встреч, оставив ту, в которой он больше заинтересован):
        if (overlapDuplicates != null) {
            if (overlapDuplicates.size() != 0) {
                System.out.println("Внимание! Перекрытие двух встреч в расписании пользователя! Пользователький оптимизатор прекратил работу! ");
                System.out.println("Пожалуйста, примите меры для разнесения во времени перекрывающихся встреч или оставьте только одну.");

                // Сохраняем сообщение
                String message = "Внимание! Перекрытие двух встреч в расписании пользователя! Оптимизация невозможна";
                SlotSaver.addMessage(user_id, meeting_id, message, opt_period_date_start, opt_period_date_end);

                // Формируем уведомление пользователю (может быть, отправителем сделать сервис-юзера (систем-юзера)? Но пока как будто сам себе шлет):
                Notification notification = new Notification("Внимание! Обнаружено перекрытие двух встреч в Вашем расписании. " +
                        "До устранения данной проблемы работа оптимизатора заблокирована. Примите необходимые меры и повторите запрос на оптимизацию", user_id, user_id, Notification.MEETING_OVERLAP);
                // Тут еще можно вставить ссылку на страницу, куда выведутся все несоответсвия, но пока без нее
                // и прикрепляем его к пользователю (или, если он оффлайн, просто автоматом переносится в базу)
                NotificationService.sendNotification(notification);
                return;
            }
        }

        // 7 Если нет никаких проблем, продолжаем оптимизацию. Проверяем на наличие перекрытий обычных событий с дубликатом встречи:
        // Берем все события, кроме нашего дубликата
        ArrayList<Event> overlapEvents = new SlotManager().getOverlapEvents(baseEvents, optimDuplicate);

        // 8 Если нет никаких перекрытий, все хорошо, оптимизировать ничего не надо, можно вывести уведомление пользователю об успешной оптимизации (перегражать страницу не надо, все норм):
        if (overlapEvents == null || overlapEvents.size() == 0) {
            System.out.println("Проверка завершена! Ваше расписание не нуждается в оптимизации! Можете сохранить свое расписание.");
            System.out.println("Пользовательский оптимизатор успешно завершил свою работу.");

            // Сохраняем сообщение
            String message = "Проверка завершена! Ваше расписание не нуждается в оптимизации! Можете сохранить свое расписание";
            SlotSaver.addMessage(user_id, meeting_id, message, opt_period_date_start, opt_period_date_end);

            // Формируем уведомление пользователю (может быть, отправителем сделать сервис-юзера (систем-юзера)? Но пока как будто сам себе шлет):
            Notification notification = new Notification("Проверка завершена! Ваше расписание не нуждается в оптимизации! Можете сохранить свое расписание.", user_id, user_id, Notification.OPTIMIZATION_IS_GOOD);
            // и прикрепляем его к пользователю (или, если он оффлайн, просто автоматом переносится в базу)
            NotificationService.sendNotification(notification);
            return;
        }

        // 9 Иначе, если перекрытия есть, вот тут уже самое время подгрузить настройки пользовательского оптимизатора (если они, конечно, у нас будут)
        // и автоматически разнести по свободным слотам перекрывающиеся события в соотвествии с настройками логики оптимизации
        // (пока сделаю тут логику "чем выше приоритет перекрывающегося события, тем короче смещение от начальной позиции")
        // Разделяем обычные перекрывающиеся события по приоритетам:
        ArrayList<Event> highPriorityEvents = new ArrayList<>(); // Список перекрывающихся событий с высоким приоритетом
        ArrayList<Event> middlePriorityEvents = new ArrayList<>(); // Список перекрывающихся событий со средним приоритетом
        ArrayList<Event> lowPriorityEvents = new ArrayList<>(); // Список перекрывающихся событий с низким приоритетом
        for (Event event : overlapEvents) {
            if (event.getPriority().equals(Event.PRIOR_HIGH)) {
                highPriorityEvents.add(event); // К самым приоритетным
            } else if (event.getPriority().equals(Event.PRIOR_MIDDLE)) {
                middlePriorityEvents.add(event); // к среднеприоритетным
            } else if (event.getPriority().equals(Event.PRIOR_LOW)) {
                lowPriorityEvents.add(event); // к низкоприоритетным
            }
        }

        // И еще сделаем список для тех, которые не поместятся в свободные слоты - с ними нужно будет потом решить, как поступить
        ArrayList<Event> nonContainEvents = new ArrayList<>();

        // И нам нужен будет список тех событий, которые не перекрываются с дубликатом:
        ArrayList<Event> eventsWithoutPriority = new ArrayList<>();
        for(int i = 0; i < events.size(); i++){
            Event event = events.get(i);
            if (!overlapEvents.contains(event)){
                eventsWithoutPriority.add(event);
            }
        }

        // eventsWithoutPriority.removeAll(overlapEvents);

        // Вытаскиваем свободные слоты слева и справа от нашего дубликата встречи
        ArrayList<Slot> allFreeSlots = new SlotManager().getAllFreeSlots(eventsWithoutPriority, opt_period_date_start, opt_period_date_end); // слева и справа теперь вместе

        // Трижды вызываем вспомогательный метод распределения по свободным слотам перекрывающихся событий
        // для списоков перекрывающихся событий трех разных приоритетов:
        if (highPriorityEvents.size() > 0) repozitionEvents(eventsWithoutPriority, highPriorityEvents, optimDuplicate, opt_period_date_start, opt_period_date_end, nonContainEvents, allFreeSlots); // для высокоприоритетных
        if (middlePriorityEvents.size() > 0) repozitionEvents(eventsWithoutPriority, middlePriorityEvents, optimDuplicate, opt_period_date_start, opt_period_date_end, nonContainEvents, allFreeSlots); // для среднеприоритетных
        if (lowPriorityEvents.size() > 0) repozitionEvents(eventsWithoutPriority, lowPriorityEvents, optimDuplicate, opt_period_date_start, opt_period_date_end, nonContainEvents, allFreeSlots); // для низкоприоритетных


        // 10 Проверяем, все ли события нормально перераспределились по свободным слотам: // (т.е. если список с теми, кто не поместился, пуст, то все хорошо)
        // Иначе если не все события поместились, перенесем-ка их правее указанного периода
        // для этого вытащим крайнее правое событие из базы (т.е. событие с максимальным айди), определим, когда оно заканчивается,
        // и последовательно рядком друг за другом запишем оставшиеся события (кстати, тут можно даже сохранить их расположение друг относительно друга,
        // просто сместив на один и тот же период, например, перенести на несколько недель вперед. Но это потом
        if (nonContainEvents.size() != 0) {
            ArrayList<Integer> idss = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.LAST_FOR_CURRENT_USER));
            Integer final_event_id = idss.get(0); // солянка, так вот пришлось определять последний айди события в базе // Integer final_event_id = new DBHelp().generationID(Converter.EVENT) - 1;
            DataObject dataObject = new DBHelp().getObjectsByIdAlternative(final_event_id); // Получаем датаобджек последнего события
            Event final_event = new Converter().ToEvent(dataObject); // Конвертируем в событие
            LocalDateTime final_event_end = final_event.getEnd(); // Получаем дату окончания финального события
            // Обходим все оставшиеся события
            for(int i = 0; i < nonContainEvents.size(); i++){
                Event event = nonContainEvents.get(i);
                Duration duration = event.getDlitelnost(); // получаем продолжительность текущего перемещаемого события
                // Cмещаем начальную границу текущего события в самый конец последнего:
                event.setDate_begin(DateConverter.dateToString(final_event_end));
                // Смещаем переменную окончания последнего события на длительность текущего события:
                final_event_end = final_event_end.plus(duration);
                // И переписываем вторую границу перемещаемого события:
                event.setDate_end(DateConverter.dateToString(final_event_end));
            }
            //  после всего этого все должно быть хорошо))

            // а это прошлый алгоритм смещения:
            /*
            // Обходим все оставшиеся события
            for(int i = 0; i < nonContainEvents.size(); i++){
                Event event = nonContainEvents.get(i);
                LocalDateTime event_start = event.getStart();
                LocalDateTime event_end = event.getEnd();
                // И смещаем его вправо на столько недель, насколько нужно, чтобы оно ушло за крайнюю правую границу последнего события:
                while (event_start.isBefore(final_event_end)){
                    event_start = event_start.plusWeeks(1);
                    event_end = event_end.plusWeeks(1);
                }
                // Записываем новые границы
                event.setDate_begin(DateConverter.dateToString(event_start));
                event.setDate_end(DateConverter.dateToString(event_end));
            }
            */

        }

        // 11 Осталось только объединить результаты и занести их в слот-сейвер:
        ArrayList<Event> resultEvents = new ArrayList<>();
        resultEvents.addAll(eventsWithoutPriority);
        resultEvents.addAll(highPriorityEvents);
        resultEvents.addAll(middlePriorityEvents);
        resultEvents.addAll(lowPriorityEvents);
        resultEvents.addAll(nonContainEvents);
        // Заносим в слот-сейвер
        for (Event event : resultEvents) {
            SlotSaver.updateEvent(user_id, meeting_id, event, opt_period_date_start, opt_period_date_end);
        }

        // Сохраняем сообщение
        String message = "Ваше расписание за указанный период успешно оптимизировано! Можете сохранить свое расписание";
        SlotSaver.addMessage(user_id, meeting_id, message, opt_period_date_start, opt_period_date_end);

        // 12 И отправить уведомление юзеру об успешном окончании оптимизации его расписания:

        System.out.println("Ваше расписание за указанный период успешно оптимизировано! Можете сохранить свое расписание.");
        System.out.println("Пользовательский оптимизатор успешно завершил свою работу.");
        // Формируем уведомление пользователю (может быть, отправителем сделать сервис-юзера (систем-юзера)? Но пока как будто сам себе шлет):
        Notification notification = new Notification("Ваше расписание за указанный период успешно оптимизировано! Можете сохранить свое расписание.", user_id, user_id, Notification.OPTIMIZATION_IS_GOOD);
        // и прикрепляем его к пользователю (или, если он оффлайн, просто автоматом переносится в базу)
        NotificationService.sendNotification(notification);
    }



    // 2017-04-15 2) Вспомогательнй метод распределения по свободным слотам перекрывающихся событий. Параметры:
    // --- 1 events - исходный список событий пользователя за определеный оптимизируемый период (нужен для определения свободных слотов)
    // --- 2 priorityEvents - текущий список событий одного приоритета, перекрывающихся с событием-дубликатом optimDuplicate оптимизируемой всмтречи
    // --- 3 optimDuplicate - событие-дубликат
    // --- 4 opt_period_date_start - левая временнАя граница оптимизируемого периода
    // --- 5 opt_period_date_end - правая временнАя граница оптимизируемого периода
    // --- 6 nonContainEvents - список, куда будем сохранять те события, которые не удалось разместить в свободные слоты
    // --- 7 ArrayList<Slot> allFreeSlots - список свободных слотов
    private static void repozitionEvents(ArrayList<Event> events, ArrayList<Event> priorityEvents, Event optimDuplicate, String opt_period_date_start, String opt_period_date_end, ArrayList<Event> nonContainEvents, ArrayList<Slot> allFreeSlots) throws IllegalAccessException, ParseException, ExecutionException, SQLException, NoSuchMethodException, InvocationTargetException {
        // Теперь отсортируем в порядке от большей продолжительности к меньшей, чтобы обходя события сначала пытаться распределить наиболее протяженные,
        // а потом поменьше, тем самым исключить случаи, когда маленькие события займут большие свободные слоты
        Collections.sort(priorityEvents, Collections.reverseOrder());

        // Вытаскиваем свободные слоты слева и справа от нашего дубликата встречи
        //ArrayList<Slot> allFreeSlots = new SlotManager().getAllFreeSlots(events, opt_period_date_start, opt_period_date_end); // слева и справа теперь вместе

        // И пробуем вписать сначала наши перекрывающиеся события текущего приоритета:
        for (int i = 0; i < priorityEvents.size(); i++) {
            Event event = priorityEvents.get(i);
            Boolean flag_perenosa = false; // Флаг переноса, показывает, сумели ли мы найти текущему событию новое место среди свободных слотов или нет
            // Вытаскиваем даты:
            LocalDateTime ev_start = DateConverter.stringToDate(event.getDate_begin());
            LocalDateTime ev_end = DateConverter.stringToDate(event.getDate_end());
            // и продолжительность
            Duration ev_duration = event.getDlitelnost();

            // Ищем место слева и справа:
            for (int j = 0; j < allFreeSlots.size(); j++) {
                Slot slot = allFreeSlots.get(j);
                // Вытаскиваем даты:
                LocalDateTime sl_start = slot.getStart();
                LocalDateTime sl_end = slot.getEnd();
                // и продолжительность
                Duration sl_duration = DateConverter.dlitelnost(slot.getString_start(), slot.getString_end());

                // Проверяем условие возможности вписать в свободный слот наше текущее событие, которое взяли на i-ом шаге обхода списка: // Вот тут надо по длительности СДЕЛАТЬ!!!
                // Если длительность свободного слота такая же или превосходит длительность события (вычев из длительности слота дительность события не получили отрицательное число), то
                if (!sl_duration.minus(ev_duration).isNegative()) { // if (sl_start.isBefore(ev_start) & sl_end.isAfter(ev_end)) { // если событие "вмещается", то переписываем ему границы (даты начала и конца):

                    // Определяемся с логикой левой и правой вставки:
                    if (sl_start.isBefore(optimDuplicate.getStart())){
                        // то свободный слот находится слева от дубликата встречи, используем "левую" локику вставки:

                        // Чтобы наименьшим образом сместиться влево от нашей встречи, нужно чтобы окончание перемещаемого события совпало с окончанием свободного слота,
                        // а начало нужно переместить на расстояние продолжительности события внутрь свободного слота:
                        Duration duration = Duration.between(ev_start, ev_end); // Продолжительность
                        LocalDateTime left = sl_end.minus(duration); // левая граница
                        LocalDateTime right = sl_end; // правая граница

                        // Меняем значения дат события:

                        event.setDate_begin(DateConverter.dateToString(left));
                        event.setDate_end(DateConverter.dateToString(right));

                        // И укоротить этот слот
                        slot.setEnd(event.getStart());
                        sl_duration = DateConverter.dlitelnost(slot.getString_start(), slot.getString_end());

                    }
                    else{
                        // Иначе свободный слот справа от дубликата встречи, задействуем "правую" логику:

                        // Чтобы наименьшим образом сместиться вправо от нашей встречи, нужно чтобы начало перемещаемого события совпало с началом свободного слота,
                        // а окончание нужно переместить на расстояние продолжительности события внутрь свободного слота:
                        Duration duration = Duration.between(ev_start, ev_end); // Продолжительность
                        LocalDateTime left = sl_start; // Левая граница
                        LocalDateTime right = sl_start.plus(duration); // правая граница

                        // Меняем значения дат события:
                        event.setDate_begin(DateConverter.dateToString(left));
                        event.setDate_end(DateConverter.dateToString(right));

                        // И укоротить этот слот
                        slot.setStart(event.getEnd());
                        sl_duration = DateConverter.dlitelnost(slot.getString_start(), slot.getString_end());

                    }

                    // выставить флаг переноса, чтобы повторно не перемещать событие это, и выйти из внутреннего цикла по j
                    flag_perenosa = true;
                    break;
                }
            }

            // Проверяем, не пора ли перейти к другому шагу по i:
            if (flag_perenosa) continue;

            // Если ни слева, ни справа нет места среди свободных слотов под наше событие, то перемещаем его во временное хранилище:
            nonContainEvents.add(event);
            // и удалить это событие из приоритетного списка
            priorityEvents.remove(i);
            i--; // А это потому что мы удалили из списка

        }
    }


    // 2017-04-18 3) Метод для поиска всех проблемных (перекрывающихся) дубликатов встреч в расписании пользователя
    public static TreeMap<Event, ArrayList<Event>> findUserProblemDuplicate() throws ParseException, InvocationTargetException, SQLException, IllegalAccessException, NoSuchMethodException {

        // 0 Подготавливаем результирующую мапу:
        TreeMap<Event, ArrayList<Event>> resultMap = new TreeMap<>();

        // 1 Определяем текущую дату-время, она будет выступать левой границей для выборки из базы всех событий пользователя):
        LocalDateTime start = LocalDateTime.now();
        String st_start = DateConverter.dateToString(start);

        // 2 Определяем дату самого последнего события в таймлайне пользовательского расписания,
        // для этого вытаскиваем самое последнее по дате событие и уже из него - дату его окончания:
        ArrayList<Integer> idss = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.LAST_FOR_CURRENT_USER));
        if (!idss.isEmpty()) {
            Integer final_event_id = idss.get(0);
            DataObject dataObject = new DBHelp().getObjectsByIdAlternative(final_event_id); // Получаем датаобджек последнего события
            Event final_event = new Converter().ToEvent(dataObject); // Конвертируем в событие
            LocalDateTime end = final_event.getEnd(); // Получаем дату окончания финального события
            String st_end = DateConverter.dateToString(end);


            // 3 Вытаскиваем из базы все события пользователя за период, ограниченный этими двумя датами:
            ArrayList<Integer> idsAllEvents = loadingService.getListIdFilteredAlternative(new EventFilter(EventFilter.FOR_CURRENT_USER, EventFilter.BETWEEN_TWO_DATES, st_start, st_end));
            ArrayList<DataObject> aldoAllEvents = loadingService.getListDataObjectByListIdAlternative(idsAllEvents);
            ArrayList<Event> events = new Converter().ToEvent(aldoAllEvents);

            // 4 Теперь нужно обойти список событий и разделить его на две части - обычные события и дубликаты встреч:
            ArrayList<Event> baseEvents = new ArrayList<>();
            ArrayList<Event> duplicates = new ArrayList<>();
            for (Event event : events) {
                if (event.getType_event().equals(Event.BASE_EVENT)) {
                    // имеем дело с обычным событием
                    baseEvents.add(event);
                } else {
                    // иначе имеем дело с дубликатом встречи
                    duplicates.add(event);
                }
            }

            // 5 А теперь обойдем список дубликатов, и для каждого дубликата поищем перекрывающиеся с ним события в списке обычных событий
            SlotManager slm = new SlotManager();
            ArrayList<Event> problems = new ArrayList<>();
            for (Event event : duplicates) {
                ArrayList<Event> problemBaseEvents = slm.getOverlapEvents(baseEvents, event);
                if (problemBaseEvents == null || problemBaseEvents.size() == 0)
                    continue; // переходим к следующему шагу, если перекрытий не нашли
                // иначе заносим в мапу проблемную копию встречи и список перекрывающихся событий:
                resultMap.put(event, problemBaseEvents);
            }
        }


        return resultMap;
    }




    // Админский оптимизатор встречи
    public static void optimizeItForAdmin(Integer user_id, Integer meeting_id, String opt_period_date_start, String opt_period_date_end){
        /////
    }
}
