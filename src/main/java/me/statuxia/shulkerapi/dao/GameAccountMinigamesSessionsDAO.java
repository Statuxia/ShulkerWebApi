package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.GameAccount;
import me.statuxia.shulkerapi.model.GameAccountMinigamesSessions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Transactional
public interface GameAccountMinigamesSessionsDAO extends JpaRepository<GameAccountMinigamesSessions, Long> {

    Optional<GameAccountMinigamesSessions> findByGameAccount(GameAccount gameAccount);
}
