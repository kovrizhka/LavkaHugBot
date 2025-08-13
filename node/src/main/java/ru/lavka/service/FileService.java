package ru.lavka.service;

import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.telegram.telegrambots.meta.api.objects.Message;
import ru.lavka.entity.AppDocument;

public interface FileService {
    AppDocument processDoc(Message externalMessage) throws FileUploadException;
}
