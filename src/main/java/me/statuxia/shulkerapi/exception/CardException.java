package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class CardException extends ApiException {

    public static final CardException UNKNOWN_CARD
        = new CardException("exception.CardException.UNKNOWN_CARD", 1401);
    public static final CardException UNKNOWN_PAYMENT_CARD
        = new CardException("exception.CardException.UNKNOWN_PAYMENT_CARD", 1402);
    public static final CardException INVALID_PIN
        = new CardException("exception.CardException.INVALID_PIN", 1403);
    public static final CardException TOO_MANY_DIRECT_CARDS
        = new CardException("exception.CardException.TOO_MANY_ACCOUNTS", 1404);

    public CardException(String message, int code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
