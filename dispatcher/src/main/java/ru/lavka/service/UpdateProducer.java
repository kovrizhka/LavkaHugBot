package ru.lavka.service;

import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Для передачи апдейтов в rabbitmq
 */
public interface UpdateProducer {

    /**
     * @param rabbitQueue название очереди для брокера rabbitmq
     * @param update данные
     */
    void produce(String rabbitQueue, Update update);

}
