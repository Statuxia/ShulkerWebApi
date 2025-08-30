package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.exception.FundsException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import me.statuxia.shulkerapi.service.BankCardService;
import me.statuxia.shulkerapi.service.CardHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class BankCardServiceImpl implements BankCardService {

    private final BankCardDAO bankCardDAO;
    private final CardHistoryService cardHistoryService;

    @Autowired
    public BankCardServiceImpl(
        BankCardDAO bankCardDAO,
        CardHistoryService cardHistoryService
    ) {
        this.bankCardDAO = bankCardDAO;
        this.cardHistoryService = cardHistoryService;
    }

    /**
     * Списание средств
     */
    @Override
    public void withdrawFunds(BankCard card, Long amount) {
        if (amount <= 0) {
            throw FundsException.AMOUNT_GREATER_ZERO;
        }

        final Long oldCurrency = card.getCurrency();
        if (oldCurrency < amount) {
            throw FundsException.NOT_ENOUGH_FUNDS;
        }

        card.setCurrency(oldCurrency - amount);
        final Long newCurrency = card.getCurrency();

        bankCardDAO.save(card);
        cardHistoryService.writeChangeCurrency(
            card, BankCardHistoryType.WITHDRAW,
            oldCurrency, newCurrency, newCurrency - oldCurrency,
            null
        );
    }

    /**
     * Пополнение средств
     */
    @Override
    public void depositFunds(BankCard card, Long amount) {
        if (amount <= 0) {
            throw FundsException.AMOUNT_GREATER_ZERO;
        }

        final Long oldCurrency = card.getCurrency();
        card.setCurrency(oldCurrency + amount);
        final Long newCurrency = card.getCurrency();
        bankCardDAO.save(card);
        cardHistoryService.writeChangeCurrency(
            card, BankCardHistoryType.DEPOSIT,
            oldCurrency, newCurrency, newCurrency - oldCurrency,
            null
        );
    }
}
