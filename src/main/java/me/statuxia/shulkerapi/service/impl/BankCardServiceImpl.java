package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.BankCardDAO;
import me.statuxia.shulkerapi.dao.BankCardOperationHistoryDAO;
import me.statuxia.shulkerapi.exception.CardHistoryException;
import me.statuxia.shulkerapi.exception.FundsException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import me.statuxia.shulkerapi.model.BankCardOperationHistory;
import me.statuxia.shulkerapi.model.BankOperationState;
import me.statuxia.shulkerapi.service.BankCardService;
import me.statuxia.shulkerapi.service.CardHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class BankCardServiceImpl implements BankCardService {

    private final BankCardDAO bankCardDAO;
    private final CardHistoryService cardHistoryService;
    private final BankCardOperationHistoryDAO bankCardOperationHistoryDAO;

    @Autowired
    public BankCardServiceImpl(
        BankCardDAO bankCardDAO,
        CardHistoryService cardHistoryService, BankCardOperationHistoryDAO bankCardOperationHistoryDAO
    ) {
        this.bankCardDAO = bankCardDAO;
        this.cardHistoryService = cardHistoryService;
        this.bankCardOperationHistoryDAO = bankCardOperationHistoryDAO;
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

    /**
     * Откат средств
     */
    @Override
    public void rollbackFunds(UUID historyUuid) {
        final List<BankCardOperationHistory> operations = bankCardOperationHistoryDAO.findByUuid(historyUuid);
        final List<BankCard> updatedCards = new ArrayList<>();
        final List<BankCardOperationHistory> updatedOperations = new ArrayList<>();
        for (BankCardOperationHistory operation : operations) {
            if (BankOperationState.ROLLBACK.equals(operation.getState())) {
                throw CardHistoryException.WRONG_HISTORY_OPERATION_TYPE;
            }

            operation.setState(BankOperationState.ROLLBACK);
            final BankCard card = operation.getCard();
            final Long currency = card.getCurrency();
            card.setCurrency(currency + (operation.getValue() * -1));

            updatedCards.add(card);
            updatedOperations.add(operation);
        }

        updatedCards.forEach(bankCardDAO::save);
        updatedOperations.forEach(bankCardOperationHistoryDAO::save);
    }

    /**
     * Возврат средств
     */
    @Override
    public void restoreFunds(UUID historyUuid) {
        final List<BankCardOperationHistory> operations = bankCardOperationHistoryDAO.findByUuid(historyUuid);
        final List<BankCard> updatedCards = new ArrayList<>();
        final List<BankCardOperationHistory> updatedOperations = new ArrayList<>();
        for (BankCardOperationHistory operation : operations) {
            if (!BankOperationState.ROLLBACK.equals(operation.getState())) {
                throw CardHistoryException.WRONG_HISTORY_OPERATION_TYPE;
            }

            operation.setState(BankOperationState.RESTORE);
            final BankCard card = operation.getCard();
            final Long currency = card.getCurrency();
            card.setCurrency(currency + (operation.getValue()));

            updatedCards.add(card);
            updatedOperations.add(operation);
        }

        updatedCards.forEach(bankCardDAO::save);
        updatedOperations.forEach(bankCardOperationHistoryDAO::save);
    }
}
