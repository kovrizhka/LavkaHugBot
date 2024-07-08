package ru.lavka.controller;

import lombok.extern.log4j.Log4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.lavka.utils.MessageUtils;

@Component
@Log4j
public class UpdateController {
    private TelegramBot telegramBot;
    private MessageUtils messageUtils;

    public UpdateController(MessageUtils messageUtils) {
        this.messageUtils = messageUtils;
    }

    public void registerBot(TelegramBot telegramBot) {
        this.telegramBot = telegramBot;
    }


    /**
     * Метод запускает валидацию апдейта, полученного от пользователя
     */
    private void processUpdate(Update update) {
        if (update == null) {
            log.error("Update is null");
            return;
        }

        if (update.hasMessage() && update.getMessage().hasText()) {
            distributeMessagesByType(update);
        } else {
            log.error("Update has no text, or invalid text");
        }
    }


    /**
     * Метод распределяет апдейт в зависимости от типа сообщения
     */
    private void distributeMessagesByType(Update update) {
        Message message = update.getMessage();
        if (message.hasText()) {
            processTextMessage(update);
        } else if (message.hasDocument()) {
            processDocumentMessage(update);
        } else {
            handleUnsupportedMessage(update);
        }
    }

    private void handleUnsupportedMessage(Update update) {
        String answerText = "Вы отправили сообщение с временно неподдерживаемым типом данных.";
        SendMessage unsupportedMessage = messageUtils.generateSendMessageWithText(update, answerText);

        sendAnswerThroughBot(unsupportedMessage);
    }

    private void sendAnswerThroughBot(SendMessage sendMessage) {
        telegramBot.sendAnswerMessage(sendMessage);
    }

    /**
     * Метод запускает обработку текстового сообщения
     */
    private void processTextMessage(Update update) {
    }

    private void processDocumentMessage(Update update) {
    }
}
