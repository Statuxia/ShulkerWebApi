package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.GameAccountBalanceHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional
public interface GameAccountBalanceHistoryDAO extends JpaRepository<GameAccountBalanceHistory, Long> {
}
