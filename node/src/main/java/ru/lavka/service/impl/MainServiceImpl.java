package ru.lavka.service.impl;

import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.lavka.dao.AppUserDAO;
import ru.lavka.dao.RawDataDao;
import ru.lavka.entity.AppUser;
import ru.lavka.entity.RawData;
import ru.lavka.entity.enums.UserState;
import ru.lavka.service.MainService;
import ru.lavka.service.ProducerService;

@Service
public class MainServiceImpl implements MainService {
    private final RawDataDao rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;

    public MainServiceImpl(RawDataDao rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        var textMessage = update.getMessage();
        var telegramUser = textMessage.getFrom();
        var appUser = findOrSaveAppUser(telegramUser);

        Message recievedMessage = update.getMessage();
        SendMessage answerMessage = new SendMessage();
        answerMessage.setChatId(recievedMessage.getChatId().toString());
        answerMessage.setText("Hello from NODE!");
        producerService.produceAnswer(answerMessage);
    }

    private AppUser findOrSaveAppUser(User telegramUser) {
        AppUser appUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());

        if (appUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActivated(true) //TODO изменить значение по-умолчанию после добавления регистрации
                    .userState(UserState.ACTIVATED)
                    .build();
            return appUserDAO.save(transientAppUser);
        }

        return appUser;
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
