package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dto.ChangeCurrencyDTO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.GameAccount;

public interface CardHistoryService {

    void writeCreateCard(BankCard card);

    void writeUpdatePin(BankCard card, boolean isAdmin);

    void writeDisable(BankCard card, GameAccount actionBy, boolean disable);

    void writeChangeCurrency(ChangeCurrencyDTO dto);
}
