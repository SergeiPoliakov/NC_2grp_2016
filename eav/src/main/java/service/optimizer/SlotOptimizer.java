package service.optimizer;

import dbHelp.DBHelp;
import entities.DataObject;
import entities.Event;
import entities.Meeting;
import entities.Notification;
import service.converter.Converter;
import service.converter.DateConverter;
import service.notifications.NotificationService;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.concurrent.ExecutionException;

/**
 * Created by Hroniko on 09.04.2017.
 */
// Класс для оптимизации встреч
public class SlotOptimizer {

    // 2017-04-15 1) Пользовательский оптимизатор расписания
    public static void optimizeItForUser(Integer user_id, Integer meeting_id, String opt_period_date_start, String opt_period_date_end) throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException, ExecutionException {
        // Среди полученных событий возможны сочетания вариантов из обычных событий распсиания юзера, события-копии текущей оптимизируемой встречи, и, что важно, событий-копий других встреч!
        // Это надо аккуратно обыграть

        // 1 Получаем из сейвера финальную точку сохранения по составному ключу:
        ArrayList<Event> events = SlotSaver.getEventFinalPoint(user_id, meeting_id, opt_period_date_start, opt_period_date_end);

        // 2 Разделяем полученные события на два типа: обычные события расписания и события-отображения встреч
        ArrayList<Event> baseEvents = new ArrayList<>();
        ArrayList<Event> anyDuplicateEvents = new ArrayList<>();
        for (Event event : events) { // такой костыль, потому что непонятно почему в базу не сохрняетмя поле у обычных событий
            if (event.getType_event() == null){
                event.setType_event(Event.BASE_EVENT);
                baseEvents.add(event);
                break;
            }
            if (event.getType_event().equals(Event.BASE_EVENT)) {
                baseEvents.add(event);
            } else if (event.getType_event().equals(Event.DUPLICATE_EVENT)) {
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
        ArrayList<Event> overlapEvents = new SlotManager().getOverlapEvents(baseEvents, optimDuplicate);

        // 8 Если нет никаких перекрытий, все хорошо, оптимизировать ничего не надо, можно вывести уведомление пользователю об успешной оптимизации (перегражать страницу не надо, все норм):
        if (overlapEvents == null || overlapEvents.size() == 0) {
            System.out.println("Проверка завершена! Ваше расписание не нуждается в оптимизации! Можете сохранить свое расписание.");
            System.out.println("Пользовательский оптимизатор успешно завершил свою работу.");
            // Формируем уведомление пользователю (может быть, отправителем сделать сервис-юзера (систем-юзера)? Но пока как будто сам себе шлет):
            Notification notification = new Notification("Проверка завершена! Ваше расписание не нуждается в оптимизации! Можете сохранить свое расписание.", user_id, user_id, Notification.OPTIMIZATION_IS_GOOD);
            // и прикрепляем его к пользователю (или, если он оффлайн, просто автоматом переносится в базу)
            NotificationService.sendNotification(notification);
            return;
        }

        // 9 Иначе, если перекрытия есть, вот тут уже самое время подгрузить настройки пользоватльского оптимизатора (если они, конечно, у нас будут)
        // и автоматически разнести по свободным слотам перекрывающиеся события в соотвествии с настройками логики оптимизации
        // (пока сделаю тут логику "чем выше приоритет перекрывающегося события, тем короче смещение от начальной позиции")
        // Разделяем обычные события по приоритетам:
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

        // Трижды вызываем вспомогательный метод распределения по свободным слотам перекрывающихся событий
        // для списоков перекрывающихся событий трех разных приоритетов:
        repozitionEvents(events, highPriorityEvents, optimDuplicate, opt_period_date_start, opt_period_date_end, nonContainEvents); // для высокоприоритетных
        repozitionEvents(events, middlePriorityEvents, optimDuplicate, opt_period_date_start, opt_period_date_end, nonContainEvents); // для среднеприоритетных
        repozitionEvents(events, lowPriorityEvents, optimDuplicate, opt_period_date_start, opt_period_date_end, nonContainEvents); // для низкоприоритетных


        // 10 Проверяем, все ли события нормально перераспределились по свободным слотам: // (т.е. если список с теми, кто не поместился, пуст, то все хорошо)
        // Иначе если не все события поместились, перенесем-ка их правее указанного периода
        // для этого вытащим крайнее правое событие из базы (т.е. событие с максимальным айди), определим, когда оно заканчивается,
        // и последовательно рядком друг за другом запишем оставшиеся события (кстати, тут можно даже сохранить их расположение друг относительно друга,
        // просто сместив на один и тот же период, например, перенести на несколько недель вперед. Так и сделаю
        if (nonContainEvents.size() != 0) {
            Integer final_event_id = new DBHelp().generationID(Converter.EVENT) - 1; // солянка, так вот пришлось определять последний айди события в базе
            DataObject dataObject = new DBHelp().getObjectsByIdAlternative(final_event_id); // Получаем датаобджек последнего события
            Event final_event = new Converter().ToEvent(dataObject); // Конвертируем в событие
            LocalDateTime final_event_end = final_event.getEnd(); // Получаем дату окончания финального события
            // Обходим все оставшиеся события
            for(int i = 0; i < nonContainEvents.size(); i++){
                Event event = nonContainEvents.get(i);
                LocalDateTime event_start = event.getStart();
                LocalDateTime event_end = event.getEnd();
                // И смещаем его вправо на столько недель, насколько нужно, чтобы оно ушло за крайнюю правую границу последнего события:
              //  while (event_start.isBefore(final_event_end)){
                    event_start.plusWeeks(1);
                    event_end.plusWeeks(1);
               // }
                // Записываем новые границы
                event.setDate_begin(DateConverter.dateToString(event_start));
                event.setDate_end(DateConverter.dateToString(event_end));
            }
            //  после всего этого все должно быть хорошо))
        }

        // 11 Осталось только объединить результаты и занести их в слот-сейвер:
        ArrayList<Event> resultEvents = new ArrayList<>();
        resultEvents.addAll(highPriorityEvents);
        resultEvents.addAll(middlePriorityEvents);
        resultEvents.addAll(lowPriorityEvents);
        resultEvents.addAll(nonContainEvents);
        // Заносим в слот-сейвер
        for (Event event : resultEvents) {
            SlotSaver.updateEvent(user_id, meeting_id, event, opt_period_date_start, opt_period_date_end);
        }

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
    private static void repozitionEvents(ArrayList<Event> events, ArrayList<Event> priorityEvents, Event optimDuplicate, String opt_period_date_start, String opt_period_date_end, ArrayList<Event> nonContainEvents) throws IllegalAccessException, ParseException, ExecutionException, SQLException, NoSuchMethodException, InvocationTargetException {
        // Теперь отсортируем в порядке от большей продолжительности к меньшей, чтобы обходя события сначала пытаться распределить наиболее протяженные,
        // а потом поменьше, тем самым исключить случаи, когда маленькие события займут большие свободные слоты
        Collections.sort(priorityEvents, Collections.reverseOrder());

        // Вытаскиваем свободные слоты слева и справа от нашего дубликата встречи
        ArrayList<Slot> leftFreeSlots = new SlotManager().getAllFreeSlots(events, opt_period_date_start, optimDuplicate.getDate_begin()); // слева
        ArrayList<Slot> rightFreeSlots = new SlotManager().getAllFreeSlots(events, optimDuplicate.getDate_end(), opt_period_date_end); // справа

        // И пробуем вписать сначала наши перекрывающиеся события наивысшего приоритета:
        for (int i = 0; i < priorityEvents.size(); i++) {
            Event event = priorityEvents.get(i);
            Boolean flag_perenosa = false; // Флаг переноса, показывает, сумели ли мы найти текущеу событию новое мето среди свободных слотов, или нет
            // Вытаскиваем даты:
            LocalDateTime ev_start = DateConverter.stringToDate(event.getDate_begin());
            LocalDateTime ev_end = DateConverter.stringToDate(event.getDate_end());

            // Ищем место слева:
            for (int j = 0; j < leftFreeSlots.size(); j++) {
                Slot slot = leftFreeSlots.get(j);
                // Вытаскиваем даты:
                LocalDateTime sl_start = slot.getStart();
                LocalDateTime sl_end = slot.getEnd();

                // Проверяем условие возможности вписать в свободный слот наше текущее событие, которое взяли на i-ом шаге обхода списка:
                if (sl_start.isBefore(ev_start) & sl_end.isAfter(ev_end)) { // если событие "вмещается", то переписываем ему границы (даты начала и конца):

                    // Чтобы наименьшим образом сместиться влево от нашей встречи, нужно чтобы окончание перемещаемого события совпало с окончанием свободного слота,
                    // а начало нужно переместить на расстояние продолжительности события внутрь свободного слота:
                    Duration duration = Duration.between(sl_start, sl_end); // Продолжительность
                    LocalDateTime left = sl_end.minus(duration); // левая граница
                    LocalDateTime right = sl_end; // правая граница

                    // Меняем значения дат события:
                    event.setDate_begin(DateConverter.dateToString(left));
                    event.setDate_end(DateConverter.dateToString(right));

                    // И надо с учетом перемещенного события пересчитать теперь свободные слоты слева
                    leftFreeSlots = new SlotManager().getAllFreeSlots(events, opt_period_date_start, optimDuplicate.getDate_begin()); // слева

                    // выставить флаг переноса, чтобы повторно не перемещать событие это, и выйти из внутреннего цикла по j
                    flag_perenosa = true;
                    break;
                }
            }

            // Проверяем, не пора ли перейти к другому шагу по i:
            if (flag_perenosa) continue;

            // Иначе не смогли найти место слева, ищем его справа:

            for (int j = 0; j < rightFreeSlots.size(); j++) {
                Slot slot = rightFreeSlots.get(j);
                // Вытаскиваем даты:
                LocalDateTime sl_start = slot.getStart();
                LocalDateTime sl_end = slot.getEnd();

                // Проверяем условие возможности вписать в свободный слот наше текущее событие, которое взяли на i-ом шаге обхода списка:
                if (sl_start.isBefore(ev_start) & sl_end.isAfter(ev_end)) { // если событие "вмещается", то переписываем ему границы (даты начала и конца):

                    // Чтобы наименьшим образом сместиться вправо от нашей встречи, нужно чтобы начало перемещаемого события совпало с началом свободного слота,
                    // а окончание нужно переместить на расстояние продолжительности события внутрь свободного слота:
                    Duration duration = Duration.between(sl_start, sl_end); // Продолжительность
                    LocalDateTime left = sl_start; // Левая граница
                    LocalDateTime right = sl_start.plus(duration); // правая граница

                    // Меняем значения дат события:
                    event.setDate_begin(DateConverter.dateToString(left));
                    event.setDate_end(DateConverter.dateToString(right));

                    // И надо с учетом перемещенного события пересчитать теперь свободные слоты справа
                    rightFreeSlots = new SlotManager().getAllFreeSlots(events, optimDuplicate.getDate_end(), opt_period_date_end); // справа

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


    // Админский оптимизатор встречи
    public static void optimizeItForAdmin(Integer user_id, Integer meeting_id, String opt_period_date_start, String opt_period_date_end){
        /////
    }
}
