package me.statuxia.shulkerapi.utils;

import me.statuxia.shulkerapi.exception.CardException;
import me.statuxia.shulkerapi.model.BankCard;
import me.statuxia.shulkerapi.model.CardType;

public class AdminCardHelper {

    public static final String ADMIN_CARD_NUMBER = "0000 0000";

    private AdminCardHelper() {
    }

    public static void unsupportedForAdminCard(BankCard card) {
        if (card == null) {
            return;
        }

        if (CardType.ADMIN.equals(card.getType())) {
            throw CardException.UNSUPPORTED_FOR_ADMIN_CARD;
        }
    }
}
