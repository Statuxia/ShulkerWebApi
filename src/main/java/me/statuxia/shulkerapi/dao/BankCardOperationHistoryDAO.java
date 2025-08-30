package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.BankCardOperationHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
public interface BankCardOperationHistoryDAO extends JpaRepository<BankCardOperationHistory, Long> {

    List<BankCardOperationHistory> findByUuid(UUID uuid);
}
