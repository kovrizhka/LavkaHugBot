package ru.lavka.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lavka.entity.RawData;

public interface RawDataDAO extends JpaRepository<RawData, Long> {

}
