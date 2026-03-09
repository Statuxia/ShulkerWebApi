package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.MinigamesAccountSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface MinigamesAccountSessionsDAO extends JpaRepository<MinigamesAccountSessions, Long> {

    Optional<MinigamesAccountSessions> findByGameAccount(GameAccount gameAccount);
}
