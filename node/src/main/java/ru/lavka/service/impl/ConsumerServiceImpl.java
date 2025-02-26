package ru.lavka.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.lavka.service.ConsumerService;

@Service
public class ConsumerServiceImpl implements ConsumerService {
    @Override
    public void consumeTextMessageUpdate(Update update) {

    }

    @Override
    public void consumeDocMessageUpdate(Update update) {

    }
}
