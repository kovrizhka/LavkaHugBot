package ru.lavka.service.impl;

import lombok.extern.log4j.Log4j;
import lombok.extern.log4j.Log4j2;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.lavka.controller.UpdateController;
import ru.lavka.service.AnswerConsumer;

import static ru.lavka.model.RabbitQueue.ANSWER_MESSAGE;

@Service
@Log4j2
public class AnswerConsumerImpl implements AnswerConsumer {
    private final UpdateController updateController;

    public AnswerConsumerImpl(UpdateController updateController) {
        this.updateController = updateController;
    }

    @Override
    @RabbitListener(queues = ANSWER_MESSAGE)
    public void consume(SendMessage message) {
        updateController.setView(message);
    }
}
