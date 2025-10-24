package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.BankCardLogDAO;
import me.statuxia.shulkerapi.dao.BankCardOperationHistoryDAO;
import me.statuxia.shulkerapi.dao.impl.BankCardHistoryDAO;
import me.statuxia.shulkerapi.dto.CardHistoryAdditionalData;
import me.statuxia.shulkerapi.dto.ChangeCurrencyDTO;
import me.statuxia.shulkerapi.dto.FromToDiff;
import me.statuxia.shulkerapi.exception.AccountException;
import me.statuxia.shulkerapi.model.*;
import me.statuxia.shulkerapi.service.CardHistoryService;
import me.statuxia.shulkerapi.utils.CardHistoryDataBuilder;
import me.statuxia.shulkerapi.utils.CardHistoryUtils;
import me.statuxia.shulkerapi.utils.CardLogDataBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static me.statuxia.shulkerapi.utils.CardHistoryUtils.build;

@Transactional
@Service
public class CardHistoryServiceImpl implements CardHistoryService {

    private final BankCardHistoryDAO bankCardHistoryDAO;
    private final BankCardLogDAO bankCardLogDAO;
    private final BankCardOperationHistoryDAO bankCardOperationHistoryDAO;
    private final MessageService messageService;

    @Autowired
    public CardHistoryServiceImpl(
        BankCardHistoryDAO bankCardHistoryDAO, BankCardLogDAO bankCardLogDAO,
        BankCardOperationHistoryDAO bankCardOperationHistoryDAO,
        MessageService messageService
    ) {
        this.bankCardHistoryDAO = bankCardHistoryDAO;
        this.bankCardLogDAO = bankCardLogDAO;
        this.bankCardOperationHistoryDAO = bankCardOperationHistoryDAO;
        this.messageService = messageService;
    }

    @Override
    public void writeCreateCard(BankCard card) {
        final BankCardHistory history = write(card, BankCardHistoryType.CREATE_CARD);
        bankCardHistoryDAO.save(history);
    }

    @Override
    public void writeUpdatePin(BankCard card, GameAccount actionBy, boolean isAdmin) {
        final List<BankCardLog> logs = new ArrayList<>();
        final BankCardHistory history = write(card, BankCardHistoryType.UPDATE_PIN);
        if (isAdmin) {
            if (actionBy == null) {
                throw AccountException.UNKNOWN_ACTION_ACCOUNT;
            }
            history.setHistoryData(new CardHistoryDataBuilder().markAsAdmin().getData());
            final BankCardLog log = CardHistoryUtils.build(card, actionBy.getName(), history.getUuid());
            log.setData(
                new CardLogDataBuilder()
                    .action(BankCardLogType.UPDATE_PIN)
                    .getData()
            );
            logs.add(log);
        }

        bankCardHistoryDAO.save(history);
        bankCardLogDAO.saveAll(logs);
    }

    public void writeDisable(BankCard card, GameAccount actionBy, boolean disable) {
        final BankCardHistory history = write(
            card,
            disable ? BankCardHistoryType.DISABLE_CARD : me.statuxia.shulkerapi.model.BankCardHistoryType.ENABLE_CARD
        );
        history.setHistoryData(new CardHistoryDataBuilder().markAsAdmin().getData());

        final BankCardLog log = CardHistoryUtils.build(card, actionBy.getName(), history.getUuid());
        log.setData(
            new CardLogDataBuilder()
                .action(disable ? BankCardLogType.DISABLE_CARD : BankCardLogType.ENABLE_CARD)
                .getData()
        );

        bankCardHistoryDAO.save(history);
        bankCardLogDAO.save(log);
    }

    @Override
    public void writeChangeCurrency(ChangeCurrencyDTO dto) {
        final BankCard card = dto.getCard();
        final BankCardHistoryType type = dto.getType();
        final String message = dto.getMessage();
        final FromToDiff fromToDiff = dto.getFromToDiff();
        final List<CardHistoryAdditionalData> additionalData = dto.getAdditionalData();

        final BankCardHistory history = build(card, type);
        history.setHistoryData(
            new CardHistoryDataBuilder()
                .description(message)
                .valueChange(messageService.message(
                    "value.change", List.of(
                        fromToDiff.from(), fromToDiff.to(),
                        fromToDiff.diff() > 0 ? "+" + fromToDiff.diff() : String.valueOf(fromToDiff.diff())
                    )
                ))
                .fromTo(fromToDiff.from(), fromToDiff.to())
                .additional(additionalData)
                .getData()
        );
        history.setUuid(dto.getHistoryUuid() == null ? UUID.randomUUID() : dto.getHistoryUuid());
        bankCardHistoryDAO.save(history);

        final BankCardOperationHistory operationHistory = build(history, fromToDiff.diff());
        bankCardOperationHistoryDAO.save(operationHistory);
    }

    private BankCardHistory write(BankCard card, BankCardHistoryType type) {
        final BankCardHistory history = build(card, type);
        history.setUuid(UUID.randomUUID());
        return history;
    }
}
