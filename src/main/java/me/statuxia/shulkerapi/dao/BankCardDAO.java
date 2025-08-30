package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.CardType;
import me.statuxia.shulkerapi.model.GameAccount;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface BankCardDAO extends JpaRepository<BankCard, Long> {

    Optional<BankCard> findByNumber(String number);

    Optional<BankCard> findByNumberAndGameAccount(String number, GameAccount gameAccount);

    List<BankCard> findByGameAccount(GameAccount gameAccount);

    Long countByGameAccount(GameAccount gameAccount);

    List<BankCard> findByGameAccount(GameAccount gameAccount, Pageable pageable);

    List<BankCard> findByGameAccountAndType(GameAccount gameAccount, CardType type);

    Long countByGameAccountAndType(GameAccount gameAccount, CardType type);
}
