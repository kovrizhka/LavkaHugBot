package ru.lavka.controller;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Configuration
public class BotConfig {

//    @Bean
//    public BotApiObject telegramBotsApi(TelegramBot telegramBot) throws TelegramApiException {
//        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
//        telegramBotsApi.registerBot(telegramBot);
//        return telegramBotsApi;
//    }

    @Bean
    public TelegramBotsLongPollingApplication TelegramBotsLongPollingApplication(TelegramBot telegramBot) throws TelegramApiException {
        TelegramBotsLongPollingApplication botsLongPollingApplication = new TelegramBotsLongPollingApplication();
        botsLongPollingApplication.registerBot(telegramBot.getBotToken(), telegramBot);
        return botsLongPollingApplication;
    }
}