package me.statuxia.shulkerapi.dao;

import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.CardType;
import me.statuxia.shulkerapi.model.GameAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Transactional
public interface BankCardDAO extends JpaRepository<BankCard, Long> {

    Optional<BankCard> findByNumber(String number);

    Optional<BankCard> findByNumberAndOwner(String number, GameAccount owner);

    List<BankCard> findByOwner(GameAccount owner);

    List<BankCard> findByOwnerAndType(GameAccount owner, CardType type);

    Long countByOwnerAndType(GameAccount owner, CardType type);
}
