package ru.lavka.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lavka.entity.AppPhoto;

public interface AppPhotoDAO extends JpaRepository<AppPhoto, Long> {
}
