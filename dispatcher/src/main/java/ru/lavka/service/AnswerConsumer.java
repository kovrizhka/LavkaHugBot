package ru.lavka.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

/**
 * Принимает ответы из брокера RabbitMQ и передает дальше в {@link ru.lavka.controller.UpdateController}
 */
public interface AnswerConsumer {
    void consume(SendMessage message);
}
