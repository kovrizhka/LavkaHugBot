package ru.lavka.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lavka.entity.AppDocument;

public interface AppDocumentDAO extends JpaRepository<AppDocument, Long> {
}
