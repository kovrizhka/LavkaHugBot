package ru.lavka.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lavka.entity.BinaryContent;

public interface BinaryContentDAO extends JpaRepository<BinaryContent, Long> {
}
