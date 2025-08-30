package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardHistoryType;

public interface CardHistoryService {

    void writeCreateCard(BankCard card);

    void writeUpdatePin(BankCard card);

    void writeDisable(BankCard card, boolean disable);

    void writeChangeCurrency(
        BankCard card, BankCardHistoryType type,
        Long from, Long to, Long diff,
        String message
    );
}
