package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardHistory;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
public interface BankCardHistoryDAO extends JpaRepository<BankCardHistory, Long> {

    List<BankCardHistory> findByUuid(UUID uuid);

    List<BankCardHistory> findByCard(BankCard card);

    List<BankCardHistory> findByCardAndTypeIn(BankCard card, List<BankCardHistoryType> types);

    List<BankCardHistory> findByCard(BankCard card, Pageable pageable);

    Long countByCard(BankCard card);

    List<BankCardHistory> findByCardAndTypeIn(BankCard card, List<BankCardHistoryType> types, Pageable pageable);

    Long countByCardAndTypeIn(BankCard card, List<BankCardHistoryType> types);
}
