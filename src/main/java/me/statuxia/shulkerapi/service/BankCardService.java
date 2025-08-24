package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.model.BankCard;

public interface BankCardService {

    /**
     * Списание средств
     */
    void withdrawFunds(BankCard card, Long amount);

    /**
     * Начисление средств
     */
    void depositFunds(BankCard card, Long amount);
}
