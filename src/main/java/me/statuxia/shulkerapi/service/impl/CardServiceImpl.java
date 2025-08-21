package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.exception.FundsException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CardServiceImpl implements CardService {

    private final BankCardDAO bankCardDAO;

    @Autowired
    public CardServiceImpl(BankCardDAO bankCardDAO) {
        this.bankCardDAO = bankCardDAO;
    }

    /**
     * Списание средств
     */
    @Override
    public void withdrawFunds(BankCard card, Long amount) {
        if (amount <= 0) {
            throw FundsException.AMOUNT_GREATER_ZERO;
        }

        if (card.getCurrency() < amount) {
            throw FundsException.NOT_ENOUGH_FUNDS;
        }

        card.setCurrency(card.getCurrency() - amount);
        bankCardDAO.save(card);
    }

    /**
     * Начисление средств
     */
    @Override
    public void depositFunds(BankCard card, Long amount) {
        if (amount <= 0) {
            throw FundsException.AMOUNT_GREATER_ZERO;
        }

        card.setCurrency(card.getCurrency() + amount);
        bankCardDAO.save(card);
    }
}
