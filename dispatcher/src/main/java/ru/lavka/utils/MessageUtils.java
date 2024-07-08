package ru.lavka.utils;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Класс для различных вариантов ответных сообщений, направляемых пользователю
 */

@Component
public class MessageUtils {

    public SendMessage generateSendMessageWithText(Update update, String text) {
        SendMessage sendMessage = new SendMessage();

        long chatId = update.getMessage().getChatId();

        sendMessage.setChatId(chatId);
        sendMessage.setText(text);

        return sendMessage;
    }

}
