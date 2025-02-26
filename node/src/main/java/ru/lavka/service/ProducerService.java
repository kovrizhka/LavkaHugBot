package ru.lavka.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
    public void produceAnswer(SendMessage sendMessage);
}
