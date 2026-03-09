package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.MinigamesAccountSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface MinigamesAccountSessionDAO extends JpaRepository<MinigamesAccountSession, Long> {

    Optional<MinigamesAccountSession> findByGameAccount(GameAccount gameAccount);
}
