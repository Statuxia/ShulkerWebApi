package me.statuxia.shulkerapi.service.impl;

import me.statuxia.shulkerapi.dao.BankCardHistoryDAO;
import me.statuxia.shulkerapi.dao.BankCardOperationHistoryDAO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardHistory;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import me.statuxia.shulkerapi.model.BankCardOperationHistory;
import me.statuxia.shulkerapi.service.CardHistoryService;
import me.statuxia.shulkerapi.utils.CardHistoryDataBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

import static me.statuxia.shulkerapi.utils.CardHistoryUtils.build;

@Transactional
@Service
public class CardHistoryServiceImpl implements CardHistoryService {

    private final BankCardHistoryDAO bankCardHistoryDAO;
    private final BankCardOperationHistoryDAO bankCardOperationHistoryDAO;
    private final MessageService messageService;

    @Autowired
    public CardHistoryServiceImpl(
        BankCardHistoryDAO bankCardHistoryDAO, BankCardOperationHistoryDAO bankCardOperationHistoryDAO,
        MessageService messageService
    ) {
        this.bankCardHistoryDAO = bankCardHistoryDAO;
        this.bankCardOperationHistoryDAO = bankCardOperationHistoryDAO;
        this.messageService = messageService;
    }

    @Override
    public void writeCreateCard(BankCard card) {
        final BankCardHistory history = write(card, BankCardHistoryType.CREATE_CARD);
        bankCardHistoryDAO.save(history);
    }

    @Override
    public void writeUpdatePin(BankCard card) {
        bankCardHistoryDAO.save(write(card, BankCardHistoryType.UPDATE_PIN));
    }

    public void writeDisable(BankCard card, boolean disable) {
        bankCardHistoryDAO.save(write(
            card,
            disable ? BankCardHistoryType.DISABLE_CARD : BankCardHistoryType.ENABLE_CARD
        ));
    }

    @Override
    public void writeChangeCurrency(
        BankCard card, BankCardHistoryType type,
        Long from, Long to, Long diff,
        String message
    ) {
        final BankCardHistory history = build(card, type);
        history.setHistoryData(
            new CardHistoryDataBuilder()
                .description(message)
                .valueChange(messageService.message("value.change", List.of(
                    from, to,
                    diff > 0 ? "+" + diff : String.valueOf(diff)
                )))
                .getData()
        );
        history.setUuid(UUID.randomUUID());
        bankCardHistoryDAO.save(history);

        final BankCardOperationHistory operationHistory = build(history, diff);
        bankCardOperationHistoryDAO.save(operationHistory);
    }

    private BankCardHistory write(BankCard card, BankCardHistoryType type) {
        final BankCardHistory history = build(card, type);
        history.setUuid(UUID.randomUUID());
        return history;
    }
}
