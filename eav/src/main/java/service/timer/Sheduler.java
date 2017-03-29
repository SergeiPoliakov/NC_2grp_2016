package service.timer;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import service.statistics.StaticticLogger;
import service.tags.TagNodeTree;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;

/**
 * Created by Hroniko on 29.03.2017.
 */
// Класс планировщика заданий для хранения всех методов-шедулеров приложения (логировние по таймеру, обновление данных и пр.)
@Component
@EnableScheduling
public class Sheduler {

    // Запуск сброса логов из очереди в базу по таймеру
    @Scheduled(fixedDelay = 1000*2*60) // 1 раз в 2 минуты
    public static void logTimer() throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        StaticticLogger.tictack();
    }

    // Автосохранение тег-нодов из дерева тегов в базу по таймеру
    @Scheduled(fixedDelay = 1000*2*60) // 1 раз в 2 минуты
    public static void tagTimer() throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        TagNodeTree.tictack();
    }

}
