package ru.lavka.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.lavka.dao.RawDataDAO;
import ru.lavka.entity.RawData;
import ru.lavka.service.MainService;
import ru.lavka.service.ProducerService;

@Service
public class MainServiceImpl implements MainService {
    private final RawDataDAO rawDataDAO;
    private final ProducerService producerService;

    public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService) {
        this.rawDataDAO = rawDataDAO;
        this.producerService = producerService;
    }

    @Override
    public void processTextMessage(Update update) {
        saveRawData(update);

        Message recievedMessage = update.getMessage();
        SendMessage answerMessage = new SendMessage();
        answerMessage.setChatId(recievedMessage.getChatId().toString());
        answerMessage.setText("Hello from NODE!");
        producerService.produceAnswer(answerMessage);
    }

    private void saveRawData(Update update) {
        RawData rawData = RawData.builder()
                .event(update)
                .build();
        rawDataDAO.save(rawData);
    }
}
