package ru.lavka.service.impl;

import lombok.extern.log4j.Log4j2;
import org.apache.tomcat.util.http.fileupload.FileUploadException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import ru.lavka.dao.AppDocumentDAO;
import ru.lavka.dao.AppPhotoDAO;
import ru.lavka.dao.BinaryContentDAO;
import ru.lavka.entity.AppDocument;
import ru.lavka.entity.AppPhoto;
import ru.lavka.entity.BinaryContent;
import ru.lavka.service.FileService;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

@Service
@Log4j2
public class FileServiceImpl implements FileService {

    @Value("${bot.token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;

    private final AppDocumentDAO appDocumentDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final AppPhotoDAO appPhotoDAO;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO, AppPhotoDAO appPhotoDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.appPhotoDAO = appPhotoDAO;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) throws FileUploadException {
        Document telegramDoc = telegramMessage.getDocument();
        String fileId = telegramDoc.getFileId(); //получаем id документа
        ResponseEntity<String> response = getFilePath(fileId); // кидаем запрос на сервер телеги
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            Document tgDocument = telegramMessage.getDocument();
            AppDocument transientAppDoc = buildTransientAppDoc(tgDocument, persistentBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        } else {
            throw new FileUploadException("Response status from Telegram is not OK: " + response);
        }
    }

    @Override
    public AppPhoto processPhoto(Message telegramMessage) throws FileUploadException {
        //TODO пока что обработка только одного фото
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(0);
        String fileId = telegramPhoto.getFileId(); //получаем id документа
        ResponseEntity<String> response = getFilePath(fileId); // кидаем запрос на сервер телеги
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto tgPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoDAO.save(tgPhoto);
        } else {
            throw new FileUploadException("Response status from Telegram is not OK: " + response);
        }
    }

    private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) throws FileUploadException {
        String filePath = getFilePath(response);
        byte[] fileInByte = downloadFile(filePath);
        BinaryContent transientBinaryContent = BinaryContent.builder().fileAsArrayOfBytes(fileInByte).build();
        BinaryContent persistentBinaryContent = binaryContentDAO.save(transientBinaryContent);
        return persistentBinaryContent;
    }

    private static String getFilePath(ResponseEntity<String> response) {
        JSONObject jsonObject = new JSONObject(response.getBody());
        String filePath = String.valueOf(jsonObject.getJSONObject("result").getString("file_path"));
        return filePath;
    }

    private AppDocument buildTransientAppDoc(Document tgDocument, BinaryContent persistentBinaryContent) {
        return AppDocument.builder()
                .telegramFileId(tgDocument.getFileId())
                .docName(tgDocument.getFileName())
                .binaryContent(persistentBinaryContent)
                .mimeType(tgDocument.getMimeType())
                .fileSize(tgDocument.getFileSize())
                .build();
    }

    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                .telegramFileId(telegramPhoto.getFileId())
                .binaryContent(persistentBinaryContent)
                .fileSize(telegramPhoto.getFileSize())
                .build();
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(fileInfoUri, HttpMethod.GET, request, String.class, token, fileId);
    }

    private byte[] downloadFile(String filePath) throws FileUploadException {
        String fullUri = fileStorageUri.replace("{token}", token).replace("{filePath}", filePath);

        URL urlObj = null;
        try {
            urlObj = new URL(fullUri);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        //todo скачивать батчами наверно
        try (InputStream inputStream = urlObj.openStream()) {
            return inputStream.readAllBytes();
        } catch (IOException e) {
            throw new RuntimeException(urlObj.toExternalForm(), e);
        }

    }

}
