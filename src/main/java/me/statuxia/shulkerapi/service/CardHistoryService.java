package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.BankCardHistoryType;
import me.statuxia.shulkerapi.model.GameAccount;

public interface CardHistoryService {

    void writeCreateCard(BankCard card);

    void writeUpdatePin(BankCard card, boolean isAdmin);

    void writeDisable(BankCard card, GameAccount actionBy, boolean disable);

    void writeChangeCurrency(
        BankCard card, BankCardHistoryType type,
        Long from, Long to, Long diff,
        String message
    );
}
