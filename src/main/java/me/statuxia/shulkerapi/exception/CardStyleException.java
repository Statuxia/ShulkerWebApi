package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class CardStyleException extends ApiException {

    public static final CardStyleException UNKNOWN_STYLE
        = new CardStyleException("exception.CardStyleException.UNKNOWN_STYLE", 1701);
    public static final CardStyleException NOT_FOR_PURCHASE
        = new CardStyleException("exception.CardStyleException.NOT_FOR_PURCHASE", 1702);

    public CardStyleException(String message, int code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
