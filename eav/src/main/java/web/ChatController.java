package web;

/**
 * Created by Hroniko on 11.05.2017.
 */

import org.springframework.stereotype.Controller;

import org.springframework.web.bind.annotation.*;

import service.chat.ChatManager;
import service.chat.ChatRequest;
import service.chat.ChatResponse;

import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

// Класс-контроллер для работы со чатом встречи
@Controller
public class ChatController {

    // 2017-05-11 К запросу на получение ВСЕХ сообщений данной встречи
    @RequestMapping(value = "/getAllMessagesChat", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    @ResponseBody
    public ArrayList<ChatResponse> getAllMessagesChat(@RequestBody ChatRequest chatRequest) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {

        return new ChatManager().getAll(chatRequest);
    }


    // 2017-05-11 К запросу на получение некоторых (начиная с определенного айди) сообщений данной встречи
    @RequestMapping(value = "/getMessagesChatAfterId", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    @ResponseBody
    public ArrayList<ChatResponse> getMessagesChatAfterId(@RequestBody ChatRequest chatRequest) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {

        return new ChatManager().getMessagesAfterId(chatRequest);
    }

    // 2017-05-11 К запросу на отправку сообщения со страницы и последующее получение некоторых (начиная с определенного айди) сообщений данной встречи
    @RequestMapping(value = "/sendMessageChat", method = RequestMethod.POST, headers = {"Content-type=application/json"})
    @ResponseBody
    public ArrayList<ChatResponse> sendMessageChat(@RequestBody ChatRequest chatRequest) throws SQLException, InvocationTargetException, NoSuchMethodException, ParseException, IllegalAccessException, ExecutionException {
        new ChatManager().sendMessage(chatRequest); // Крепим к сейверу новое сообщение
        return new ChatManager().getMessagesAfterId(chatRequest); // и подгружаем все новые сообщения
    }





}
