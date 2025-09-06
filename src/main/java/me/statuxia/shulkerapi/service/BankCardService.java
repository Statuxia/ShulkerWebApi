package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.BankCard;

import java.util.UUID;

public interface BankCardService {

    /**
     * Списание средств
     */
    void withdrawFunds(BankCard card, Long amount);

    /**
     * Начисление средств
     */
    void depositFunds(BankCard card, Long amount);

    /**
     * Откат средств
     */
    void rollbackFunds(UUID historyUuid);

    /**
     * Возврат средств
     */
    void restoreFunds(UUID historyUuid);
}
