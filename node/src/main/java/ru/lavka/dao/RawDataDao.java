package ru.lavka.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lavka.entity.RawData;

public interface RawDataDao extends JpaRepository<RawData, Long> {

}
