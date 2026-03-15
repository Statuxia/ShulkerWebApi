package me.statuxia.shulkerapi.service;

import me.statuxia.shulkerapi.dto.CardHistoryAdditionalData;
import me.statuxia.shulkerapi.dto.search.impl.BankCardSearchDTO;
import me.statuxia.shulkerapi.model.*;

import java.util.List;
import java.util.UUID;

public interface BankCardService {

    BankCard getPaymentCard(BankCardSearchDTO dto, String paymentCardPin);

    /**
     * Списание средств
     */
    void withdrawFunds(BankCard card, Long amount, boolean withAdminIncrease);

    /**
     * Списание средств с дополнительными данными для истории
     */
    void withdrawFunds(
        BankCard card,
        Long amount,
        boolean withAdminIncrease,
        List<CardHistoryAdditionalData> additionalData
    );

    /**
     * Изменение стиля карты
     */
    void changeStyle(BankCard card, CardStyleType styleType);

    /**
     * Начисление средств
     */
    void depositFunds(BankCard card, Long amount);

    /**
     * Начисление средств с дополнительными данными для истории
     */
    void depositFunds(BankCard card, Long amount, List<CardHistoryAdditionalData> additionalData);

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

    /**
     * Пополнение админского счета
     */
    void increaseAdminCard(UUID historyUuid, BankCardHistoryType type, Long amount, BankCard from);
}
