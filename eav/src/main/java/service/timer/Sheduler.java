package service.timer;

import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import service.chat.ChatSaver;
import service.statistics.StatisticLogger;
import service.statistics.StatisticSaver;
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
        StatisticLogger.tictack();
    }

    // Автосохранение тег-нодов из дерева тегов в базу по таймеру
    @Scheduled(fixedDelay = 1000*2*60) // 1 раз в 2 минуты
    public static void tagTimer() throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        TagNodeTree.tictack();
    }

    // Авточистка хранителя статистик (в памяти) по таймеру
    @Scheduled(fixedDelay = 1000*60) // 1 раз в 1 минуту
    public static void statTimer() throws InvocationTargetException, SQLException, IllegalAccessException, ParseException, NoSuchMethodException {
        StatisticSaver.tictack();
    }

    // 2017-05-11 Автосброс в базу новых сообщений из чатов встреч по таймеру
    @Scheduled(fixedDelay = 1000*60) // 1 раз в 1 минуту
    public static void chatTimer() throws SQLException, NoSuchMethodException, IllegalAccessException, ParseException, InvocationTargetException {
        ChatSaver.tictack();
    }

}
