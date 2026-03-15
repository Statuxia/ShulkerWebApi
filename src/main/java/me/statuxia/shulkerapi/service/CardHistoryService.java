package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dto.ChangeCurrencyDTO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.GameAccount;

public interface CardHistoryService {

    void writeCreateCard(BankCard card);

    void writeUpdatePin(BankCard card, GameAccount actionBy, boolean isAdmin);

    void writeDisable(BankCard card, GameAccount actionBy, boolean disable);

    void writeUpdateName(BankCard card, GameAccount actionBy, boolean isAdmin);

    void writeChangeCurrency(ChangeCurrencyDTO dto);

    void writeUpdateGroupCardSetting(BankCard card);

    void writeAddGroupCardMember(BankCard card);

    void writeRemoveGroupCardMember(BankCard card);

    void writeUpdateGroupCardMemberPin(BankCard card);

    void writeUpdateGroupCardMemberSetting(BankCard card);
}
