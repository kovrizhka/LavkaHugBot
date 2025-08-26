package ru.lavka.service;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.lavka.entity.AppDocument;
import ru.lavka.entity.AppPhoto;

public interface FileService {
    AppDocument processDoc(Message telegramMessage) throws FileUploadException;
    AppPhoto processPhoto(Message telegramMessage) throws FileUploadException;
}
