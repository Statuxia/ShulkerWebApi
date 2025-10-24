package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dto.search.impl.BankCardSearchDTO;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.CardStyleType;
import me.statuxia.shulkerapi.model.Fine;
import me.statuxia.shulkerapi.model.GameAccount;

import java.util.UUID;

public interface BankCardService {

    BankCard getPaymentCard(BankCardSearchDTO dto, String paymentCardPin);

    /**
     * Списание средств
     */
    void withdrawFunds(BankCard card, Long amount);

    /**
     * Изменение стиля карты
     */
    void changeStyle(BankCard card, CardStyleType styleType);

    /**
     * Начисление средств
     */
    void depositFunds(BankCard card, Long amount);

    /**
     * Перевод средств
     */
    void transferFunds(BankCard card, BankCard receiverCard, Long amount, String message);

    /**
     * Откат средств
     */
    void rollbackFunds(UUID historyUuid, GameAccount actionBy);

    /**
     * Возврат средств
     */
    void restoreFunds(UUID historyUuid, GameAccount actionBy);

    /**
     * Оплата штрафов
     */
    void payFine(BankCard card, Long value, Fine fine);
}
