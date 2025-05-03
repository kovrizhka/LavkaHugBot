package ru.lavka.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.lavka.entity.AppUser;

public interface AppUserDAO extends JpaRepository<AppUser, Long> {

    AppUser findAppUserByTelegramUserId(Long telegramUserId);
}
