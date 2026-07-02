package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.BankCardLogDAO;
import me.statuxia.shulkerapi.dao.BankCardOperationHistoryDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardDAO;
import me.statuxia.shulkerapi.dao.impl.CardStyleDAO;
import me.statuxia.shulkerapi.dao.impl.FineDAO;
import me.statuxia.shulkerapi.dto.CardHistoryAdditionalData;
import me.statuxia.shulkerapi.dto.ChangeCurrencyDTO;
import me.statuxia.shulkerapi.dto.search.impl.BankCardSearchDTO;
import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.exception.CardHistoryException;
import me.statuxia.shulkerapi.exception.CardStyleException;
import me.statuxia.shulkerapi.exception.FundsException;
import me.statuxia.shulkerapi.model.*;
import me.statuxia.shulkerapi.service.BankCardService;
import me.statuxia.shulkerapi.service.CardHistoryService;
import me.statuxia.shulkerapi.service.PinAdapter;
import me.statuxia.shulkerapi.swagger.controller.card.InvalidPaymentPinOperation;
import me.statuxia.shulkerapi.swagger.controller.card.PaymentCardDisabledOperation;
import me.statuxia.shulkerapi.swagger.controller.card.PaymentFromDirectOperation;
import me.statuxia.shulkerapi.swagger.controller.card.UnknownPaymentCardOperation;
import me.statuxia.shulkerapi.utils.CardHistoryUtils;
import me.statuxia.shulkerapi.utils.CardLogDataBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static me.statuxia.shulkerapi.utils.AdminCardHelper.buildSearchDTO;

@Service
@Transactional
public class BankCardServiceImpl implements BankCardService {
    protected final Logger logger = LoggerFactory.getLogger(getClass());

    private final CardStyleDAO cardStyleDAO;
    private final BankCardDAO bankCardDAO;
    private final BankCardLogDAO bankCardLogDAO;
    private final CardHistoryService cardHistoryService;
    private final BankCardOperationHistoryDAO bankCardOperationHistoryDAO;
    private final FineDAO fineDAO;
    private final BankCardService service;
    private final PinAdapter pinAdapter;

    @Autowired
    public BankCardServiceImpl(
        CardStyleDAO cardStyleDAO, BankCardDAO bankCardDAO, BankCardLogDAO bankCardLogDAO,
        CardHistoryService cardHistoryService, BankCardOperationHistoryDAO bankCardOperationHistoryDAO,
        FineDAO fineDAO, PinAdapter pinAdapter
    ) {
        this.cardStyleDAO = cardStyleDAO;
        this.bankCardDAO = bankCardDAO;
        this.bankCardLogDAO = bankCardLogDAO;
        this.cardHistoryService = cardHistoryService;
        this.bankCardOperationHistoryDAO = bankCardOperationHistoryDAO;
        this.fineDAO = fineDAO;
        this.service = this;
        this.pinAdapter = pinAdapter;
    }

    @Override
    @UnknownPaymentCardOperation
    @PaymentCardDisabledOperation
    @PaymentFromDirectOperation
    @InvalidPaymentPinOperation
    public BankCard getPaymentCard(BankCardSearchDTO dto, String paymentCardPin) {
        final Optional<BankCard> paymentCard = bankCardDAO.find(dto);
        if (paymentCard.isEmpty()) {
            throw CardException.UNKNOWN_PAYMENT_CARD;
        }

        final BankCard card = paymentCard.get();
        if (card.isDisabled()) {
            throw CardException.PAYMENT_CARD_DISABLED;
        }

        if (!CardType.DIRECT.equals(card.getType())) {
            throw CardException.PAYMENT_FROM_DIRECT;
        }

        if (!pinAdapter.pinMatches(paymentCardPin, card.getPin())) {
            throw CardException.INVALID_PAYMENT_PIN;
        }

        return card;
    }

    /**
     * Списание средств
     */
    @Override
    public void withdrawFunds(BankCard card, Long amount, boolean withAdminIncrease) {
        withdrawFunds(card, amount, withAdminIncrease, Collections.emptyList());
    }

    /**
     * Списание средств с дополнительными данными для истории
     */
    @Override
    public void withdrawFunds(
        BankCard card,
        Long amount,
        boolean withAdminIncrease,
        List<CardHistoryAdditionalData> additionalData
    ) {
        if (amount <= 0) {
            throw FundsException.AMOUNT_GREATER_ZERO;
        }

        final Long oldCurrency = card.getCurrency();
        if (oldCurrency < amount) {
            throw FundsException.NOT_ENOUGH_FUNDS;
        }

        card.setCurrency(oldCurrency - amount);
        final Long newCurrency = card.getCurrency();
        final UUID historyUuid = UUID.randomUUID();

        bankCardDAO.save(card);
        final ChangeCurrencyDTO dto = new ChangeCurrencyDTO(
            card, BankCardHistoryType.WITHDRAW,
            oldCurrency, newCurrency, newCurrency - oldCurrency
        ).setHistoryUuid(historyUuid);
        additionalData.forEach(dto::addAdditionalData);
        cardHistoryService.writeChangeCurrency(dto);

        if (withAdminIncrease) {
            service.increaseAdminCard(historyUuid, BankCardHistoryType.WITHDRAW, amount, card);
        }
    }

    /**
     * Изменение стиля карты
     */
    @Override
    public void changeStyle(BankCard card, CardStyleType styleType) {
        final Optional<CardStyle> optCardStyle = cardStyleDAO.findById(styleType);
        if (optCardStyle.isEmpty()) {
            throw CardStyleException.UNKNOWN_STYLE;
        }

        final CardStyle cardStyle = optCardStyle.get();

        final Long amount = cardStyle.getPrice();
        if (amount < 0) {
            throw CardStyleException.NOT_FOR_PURCHASE;
        }

        final Long oldCurrency = card.getCurrency();
        if (oldCurrency < amount) {
            throw FundsException.NOT_ENOUGH_FUNDS;
        }

        final UUID historyUuid = UUID.randomUUID();
        card.setCurrency(oldCurrency - amount);
        card.setCardStyle(cardStyle.getType());
        card.updatePatternSeed();
        final Long newCurrency = card.getCurrency();

        bankCardDAO.save(card);
        cardHistoryService.writeChangeCurrency(new ChangeCurrencyDTO(
            card, BankCardHistoryType.CHANGE_STYLE,
            oldCurrency, newCurrency, newCurrency - oldCurrency
        ).setHistoryUuid(historyUuid));

        service.increaseAdminCard(historyUuid, BankCardHistoryType.CHANGE_STYLE, amount, card);
    }

    /**
     * Пополнение средств
     */
    @Override
    public void depositFunds(BankCard card, Long amount) {
        depositFunds(card, amount, Collections.emptyList());
    }

    /**
     * Пополнение средств с дополнительными данными для истории
     */
    @Override
    public void depositFunds(BankCard card, Long amount, List<CardHistoryAdditionalData> additionalData) {
        if (amount <= 0) {
            throw FundsException.AMOUNT_GREATER_ZERO;
        }

        final Long oldCurrency = card.getCurrency();
        card.setCurrency(oldCurrency + amount);
        final Long newCurrency = card.getCurrency();
        bankCardDAO.save(card);
        final ChangeCurrencyDTO dto = new ChangeCurrencyDTO(
            card, BankCardHistoryType.DEPOSIT,
            oldCurrency, newCurrency, newCurrency - oldCurrency
        );
        additionalData.forEach(dto::addAdditionalData);
        cardHistoryService.writeChangeCurrency(dto);
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
        if (oldCurrency < amount) {
            throw FundsException.NOT_ENOUGH_FUNDS;
        }

        final UUID historyUuid = UUID.randomUUID();
        card.setCurrency(oldCurrency - amount);
        Long newCurrency = card.getCurrency();
        bankCardDAO.save(card);
        final ChangeCurrencyDTO senderDTO = new ChangeCurrencyDTO(
            card, BankCardHistoryType.TRANSFER_FROM,
            oldCurrency, newCurrency, newCurrency - oldCurrency
        )
            .setMessage(message)
            .addAdditionalData(new CardHistoryAdditionalData("receiver", receiverCard.getNumber()))
            .setHistoryUuid(historyUuid);
        cardHistoryService.writeChangeCurrency(senderDTO);

        oldCurrency = receiverCard.getCurrency();
        receiverCard.setCurrency(oldCurrency + amount);
        newCurrency = receiverCard.getCurrency();
        bankCardDAO.save(receiverCard);
        final ChangeCurrencyDTO receiverDTO = new ChangeCurrencyDTO(
            receiverCard, BankCardHistoryType.TRANSFER_TO,
            oldCurrency, newCurrency, newCurrency - oldCurrency
        )
            .setMessage(message)
            .addAdditionalData(new CardHistoryAdditionalData("sender", card.getNumber()))
            .setHistoryUuid(historyUuid);
        cardHistoryService.writeChangeCurrency(receiverDTO);
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
            log.setData(new CardLogDataBuilder().action(BankCardLogType.ROLLBACK).getData());

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

    /**
     * Оплата штрафов
     */
    @Override
    public void payFine(BankCard card, Long amount, Fine fine) {
        if (amount <= 0) {
            throw FundsException.AMOUNT_GREATER_ZERO;
        }

        final Long oldCurrency = card.getCurrency();
        if (oldCurrency < amount) {
            throw FundsException.NOT_ENOUGH_FUNDS;
        }

        final UUID historyUuid = UUID.randomUUID();
        card.setCurrency(oldCurrency - amount);
        final Long newCurrency = card.getCurrency();
        bankCardDAO.save(card);
        final ChangeCurrencyDTO senderDTO = new ChangeCurrencyDTO(
            card, BankCardHistoryType.PAY_FINE,
            oldCurrency, newCurrency, newCurrency - oldCurrency
        ).setHistoryUuid(historyUuid);
        cardHistoryService.writeChangeCurrency(senderDTO);

        fine.setStatus(FineStatus.PAYED);
        fine.setStatusDate(DateTime.now());
        fineDAO.save(fine);

        service.increaseAdminCard(historyUuid, BankCardHistoryType.PAY_FINE, amount, card);
    }

    /**
     * Пополнение админского счета
     */
    @Override
    public void increaseAdminCard(UUID historyUuid, BankCardHistoryType type, Long amount, BankCard from) {
        final Optional<BankCard> card = bankCardDAO.find(buildSearchDTO());
        logger.debug("card: {}", card);
        if (card.isEmpty()) {
            return;
        }

        final BankCard bankCard = card.get();
        final Long oldCurrency = bankCard.getCurrency();
        bankCard.setCurrency(oldCurrency + amount);


        final ChangeCurrencyDTO receiverDTO = new ChangeCurrencyDTO(
            bankCard, BankCardHistoryType.ADMIN_TRANSFER,
            oldCurrency, bankCard.getCurrency(), bankCard.getCurrency() - oldCurrency
        )
            .addAdditionalData(new CardHistoryAdditionalData("sender", from.getNumber()))
            .addAdditionalData(new CardHistoryAdditionalData("historyType", type.name()))
            .setHistoryUuid(historyUuid);
        cardHistoryService.writeChangeCurrency(receiverDTO);
        bankCardDAO.save(bankCard);
    }
}
