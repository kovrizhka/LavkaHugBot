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

    private final UpdateController updateController;

    public TelegramBot(UpdateController updateController) {
        this.updateController = updateController;
    }

    @PostConstruct
    private void injectTelegramBotToController() {
        telegramClient = new OkHttpTelegramClient(botToken);
        updateController.registerBot(this);
    }

    public void sendAnswerMessage(SendMessage message) {
        if (message != null) {
            try {
                telegramClient.execute(message);
            } catch (TelegramApiException e) {
                log.error(e);
            }
        }
    }

    /**
     * Самый первый метод куда что-то приходит из бота.
     * @param update это сообщение или любое другое действие от пользователя, адресованное боту.
     */
    @Override
    public void consume(Update update) {
        updateController.processUpdate(update);
    }
}