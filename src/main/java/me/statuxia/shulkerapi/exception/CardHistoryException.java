package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class CardHistoryException extends ApiException {

    public static final CardHistoryException UNKNOWN_HISTORY
        = new CardHistoryException("exception.CardHistoryException.UNKNOWN_HISTORY", 1601);
    public static final CardHistoryException WRONG_HISTORY_TYPE
        = new CardHistoryException("exception.CardHistoryException.WRONG_HISTORY_TYPE", 1602);
    public static final CardHistoryException WRONG_HISTORY_OPERATION_TYPE
        = new CardHistoryException("exception.CardHistoryException.WRONG_HISTORY_OPERATION_TYPE", 1603);

    public CardHistoryException(String message, int code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
