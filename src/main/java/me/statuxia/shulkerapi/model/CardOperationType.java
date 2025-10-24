package me.statuxia.shulkerapi.model;

public enum CardOperationType {

    DEPOSIT,
    WITHDRAW,
    /**
     * Перевод (отправка)
     */
    TRANSFER_OUT,
    /**
     * Перевод (получение)
     */
    TRANSFER_IN
}
