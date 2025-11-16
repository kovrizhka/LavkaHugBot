package ru.lavka.controller;

import jakarta.annotation.PostConstruct;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

/**
 * Класс компонент конфигурации, который описывает подключение к телеграм боту.
 */

@Component
@Log4j2
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {

    private TelegramClient telegramClient;

    @Value("${bot.name}")
    private String botName;

    @Getter
    @Value("${bot.token}")
    private String botToken;

    private UpdateController updateController;

    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
    }

    @PostConstruct
    private void injectTelegramBotToController() {
        telegramClient = new OkHttpTelegramClient(botToken);
        updateController.registerBot(this);
    }

//    @Override
    public void onUpdateReceived(Update update) {
        updateController.processUpdate(update);
//        if (update.hasMessage() && update.getMessage().hasText()) {
//            System.out.println("Сообщение получено");
//            String chatId = update.getMessage().getChatId().toString();
//            String text = update.getMessage().getText();
//            log.debug("Сообщение из чата: " + chatId + ": " + text);
//
//            SendMessage response = new SendMessage(chatId, "Привет от Полины :P");
//
//            sendAnswerMessage(response);
//        }
    }

//    @Override
    public String getBotUsername() {
        return botName;
    }
//
//    @Override
//    public String getBotToken() {
//        return botToken;
//    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                telegramClient.execute(message);
//                execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    @Override
    public void consume(Update update) {
        updateController.processUpdate(update);
    }
}