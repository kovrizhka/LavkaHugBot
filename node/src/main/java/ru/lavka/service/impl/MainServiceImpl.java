package ru.lavka.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.lavka.dao.AppUserDAO;
import ru.lavka.dao.RawDataDao;
import ru.lavka.entity.AppDocument;
import ru.lavka.entity.AppPhoto;
import ru.lavka.entity.AppUser;
import ru.lavka.entity.RawData;
import ru.lavka.entity.enums.UserStateEnum;
import ru.lavka.service.FileService;
import ru.lavka.service.MainService;
import ru.lavka.service.ProducerService;
import ru.lavka.service.enums.ServiceCommandsEnum;

@Slf4j
@Service
public class MainServiceImpl implements MainService {
    private final RawDataDao rawDataDAO;
    private final ProducerService producerService;
    private final AppUserDAO appUserDAO;
    private final FileService fileService;

    public MainServiceImpl(RawDataDao rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO, FileService fileService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
        this.appUserDAO = appUserDAO;
        this.fileService = fileService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var userState = appUser.getUserStateEnum();
        var text = update.getMessage().getText();
        var output = "";

        var serviceCommand = ServiceCommandsEnum.fromCmd(text);
        if (ServiceCommandsEnum.CANCEL.equals(serviceCommand)) {
            output = cancelProcess(appUser);
        } else if (userState.equals(UserStateEnum.BASIC_STATE)) {
            output = processServiceCommands(appUser, text);
        } else if (userState.equals(UserStateEnum.WAITING_FOR_EMAIL)) {
            //todo добавить обработку электронной почты
        } else {
            log.error("Unknown state {}", userState);
            output = "Неизвестная ошибка. Введите /cancel и попробуйте снова!";
        }

        var chatId = update.getMessage().getChatId();
        sendAnswer(chatId, output);
    }

    @Override
    public void processPhotoMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();

        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppPhoto photo = fileService.processPhoto(update.getMessage());
            var answer = "Фото получено, собираю данные...";
            sendAnswer(chatId, answer);
        } catch (FileUploadException e) {
            log.error(e.getMessage());
            var errorMessage = "К сожалению, не удалось загрузить фото. Попробуйте позже.";
            sendAnswer(chatId, errorMessage);
        }
    }

    //TODO переделать логику (убрать Not и изменить return'ы)
    private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
        var userState = appUser.getUserStateEnum();
        if (!appUser.getIsActivated()) {
            var errorText = "Учетная запись не активирована, пройдите регистрацию /registration";
            sendAnswer(chatId, errorText);
            return true;
        } else if (!userState.equals(UserStateEnum.BASIC_STATE)) {
            var errorText = "Отмените текущее действие /cancel";
            sendAnswer(chatId, errorText);
            return true;
        }
        return false;
    }

    @Override
    public void processDocMessage(Update update) {
        saveRawData(update);
        var appUser = findOrSaveAppUser(update);
        var chatId = update.getMessage().getChatId();
        if (isNotAllowToSendContent(chatId, appUser)) {
            return;
        }

        try {
            AppDocument doc = fileService.processDoc(update.getMessage());
            //TODO добавить генерацию ссылки для скачивания
            var answer = "Документ успешно загружен, ссылку я конечно же не скину :)";
            sendAnswer(chatId, answer);
        } catch (FileUploadException e) {
            log.error(e.getMessage(), e);
            var errorMessage = "К сожалению, не удалось загрузить файл. Попробуйте позже.";
            sendAnswer(chatId, errorMessage);
        }
    }

    private void sendAnswer(Long chatId, String output) {
        SendMessage answerMessage = new SendMessage();
        answerMessage.setChatId(chatId);
        answerMessage.setText(output);
        producerService.produceAnswer(answerMessage);
    }

    private String processServiceCommands(AppUser appUser, String text) {
        var cmd = ServiceCommandsEnum.fromCmd(text);
        if (ServiceCommandsEnum.REGISTRATION.equals(cmd)) {
            //TODO ДОДЕЛАТЬ
            return "Временно недоступна";
        } else if (ServiceCommandsEnum.HELP.equals(cmd)) {
            return help();
        } else if (ServiceCommandsEnum.START.equals(cmd)) {
            return "Привет в магазинчике! Чтобы посмотреть список доступных команд введите /help";
        } else {
            return "Неизвестная команда! Чтобы посмотреть список доступных команд введите /help";
        }
    }

    private String help() {
        return "Список доступных команд:\n" +
                "/cancel - отмена выполнения текущей команды\n" +
                "/registration - регистрация пользователя";
    }

    private String cancelProcess(AppUser appUser) {
        appUser.setUserStateEnum(UserStateEnum.BASIC_STATE);
        appUserDAO.save(appUser);
        return "Команда отменена!";
    }

    private AppUser findOrSaveAppUser(Update update) {
        User telegramUser = update.getMessage().getFrom();

        AppUser appUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());

        if (appUser == null) {
            AppUser transientAppUser = AppUser.builder()
                    .telegramUserId(telegramUser.getId())
                    .username(telegramUser.getUserName())
                    .firstName(telegramUser.getFirstName())
                    .lastName(telegramUser.getLastName())
                    .isActivated(true) //TODO изменить значение по-умолчанию после добавления регистрации
                    .userStateEnum(UserStateEnum.BASIC_STATE)
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
