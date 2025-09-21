package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.BankCardLogDAO;
import me.statuxia.shulkerapi.dao.BankCardOperationHistoryDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dto.CardHistoryAdditionalData;
import me.statuxia.shulkerapi.dto.ChangeCurrencyDTO;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.exception.CardHistoryException;
import me.statuxia.shulkerapi.exception.FundsException;
import me.statuxia.shulkerapi.model.*;
import me.statuxia.shulkerapi.service.BankCardService;
import me.statuxia.shulkerapi.service.CardHistoryService;
import me.statuxia.shulkerapi.utils.CardHistoryUtils;
import me.statuxia.shulkerapi.utils.CardLogDataBuilder;
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
    private final BankCardLogDAO bankCardLogDAO;
    private final CardHistoryService cardHistoryService;
    private final BankCardOperationHistoryDAO bankCardOperationHistoryDAO;

    @Autowired
    public BankCardServiceImpl(
        BankCardDAO bankCardDAO, BankCardLogDAO bankCardLogDAO,
        CardHistoryService cardHistoryService, BankCardOperationHistoryDAO bankCardOperationHistoryDAO
    ) {
        this.bankCardDAO = bankCardDAO;
        this.bankCardLogDAO = bankCardLogDAO;
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
        cardHistoryService.writeChangeCurrency(new ChangeCurrencyDTO(
            card, BankCardHistoryType.WITHDRAW,
            oldCurrency, newCurrency, newCurrency - oldCurrency
        ));
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
        cardHistoryService.writeChangeCurrency(new ChangeCurrencyDTO(
            card, BankCardHistoryType.DEPOSIT,
            oldCurrency, newCurrency, newCurrency - oldCurrency
        ));
    }

    @Override
    public void transferFunds(BankCard card, BankCard receiverCard, Long amount, String message) {
        if (amount <= 0) {
            throw FundsException.AMOUNT_GREATER_ZERO;
        }

        if (card.getId().equals(receiverCard.getId())) {
            throw CardException.SAME_CARD_RECEIVER;
        }

        Long oldCurrency = card.getCurrency();
        card.setCurrency(oldCurrency - amount);
        Long newCurrency = card.getCurrency();
        bankCardDAO.save(card);
        cardHistoryService.writeChangeCurrency(new ChangeCurrencyDTO(
            card, BankCardHistoryType.TRANSFER_FROM,
            oldCurrency, newCurrency, newCurrency - oldCurrency
        ).setMessage(message).addAdditionalData(new CardHistoryAdditionalData("receiver", receiverCard.getNumber())));

        oldCurrency = receiverCard.getCurrency();
        receiverCard.setCurrency(oldCurrency + amount);
        newCurrency = receiverCard.getCurrency();
        bankCardDAO.save(receiverCard);
        cardHistoryService.writeChangeCurrency(new ChangeCurrencyDTO(
            receiverCard, BankCardHistoryType.TRANSFER_TO,
            oldCurrency, newCurrency, newCurrency - oldCurrency
        ).setMessage(message).addAdditionalData(new CardHistoryAdditionalData("sender", card.getNumber())));
    }

    /**
     * Откат средств
     */
    @Override
    public void rollbackFunds(UUID historyUuid, GameAccount actionBy) {
        final List<BankCardOperationHistory> operations = bankCardOperationHistoryDAO.findByUuid(historyUuid);
        final List<BankCard> updatedCards = new ArrayList<>();
        final List<BankCardOperationHistory> updatedOperations = new ArrayList<>();
        final List<BankCardLog> createdLogs = new ArrayList<>();
        for (BankCardOperationHistory operation : operations) {
            if (BankOperationState.ROLLBACK.equals(operation.getState())) {
                throw CardHistoryException.WRONG_HISTORY_OPERATION_TYPE;
            }

            operation.setState(BankOperationState.ROLLBACK);
            final BankCard card = operation.getCard();
            final Long currency = card.getCurrency();
            card.setCurrency(currency + (operation.getValue() * -1));

            final BankCardLog log = CardHistoryUtils.build(card, actionBy.getName(), operation.getUuid());
            log.setData(new CardLogDataBuilder().action(BankCardLogType.RESTORE).getData());

            updatedCards.add(card);
            updatedOperations.add(operation);
            createdLogs.add(log);

        }

        updatedCards.forEach(bankCardDAO::save);
        updatedOperations.forEach(bankCardOperationHistoryDAO::save);
        createdLogs.forEach(bankCardLogDAO::save);
    }

    /**
     * Возврат средств
     */
    @Override
    public void restoreFunds(UUID historyUuid, GameAccount actionBy) {
        final List<BankCardOperationHistory> operations = bankCardOperationHistoryDAO.findByUuid(historyUuid);
        final List<BankCard> updatedCards = new ArrayList<>();
        final List<BankCardOperationHistory> updatedOperations = new ArrayList<>();
        final List<BankCardLog> createdLogs = new ArrayList<>();
        for (BankCardOperationHistory operation : operations) {
            if (!BankOperationState.ROLLBACK.equals(operation.getState())) {
                throw CardHistoryException.WRONG_HISTORY_OPERATION_TYPE;
            }

            operation.setState(BankOperationState.RESTORE);
            final BankCard card = operation.getCard();
            final Long currency = card.getCurrency();
            card.setCurrency(currency + (operation.getValue()));

            final BankCardLog log = CardHistoryUtils.build(card, actionBy.getName(), operation.getUuid());
            log.setData(new CardLogDataBuilder().action(BankCardLogType.RESTORE).getData());

            updatedCards.add(card);
            updatedOperations.add(operation);
            createdLogs.add(log);
        }

        updatedCards.forEach(bankCardDAO::save);
        updatedOperations.forEach(bankCardOperationHistoryDAO::save);
        createdLogs.forEach(bankCardLogDAO::save);
    }
}
