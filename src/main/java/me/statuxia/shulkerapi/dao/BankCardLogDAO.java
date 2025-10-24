package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Transactional
public interface BankCardLogDAO extends JpaRepository<BankCardLog, Long> {

    List<BankCardLog> findByUuidAndCard(UUID uuid, BankCard bankCard);
}
