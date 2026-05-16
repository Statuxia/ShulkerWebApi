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
    public static final CardException CARD_DISABLED
        = new CardException("exception.CardException.CARD_DISABLED", 1405);
    public static final CardException PAYMENT_CARD_DISABLED
        = new CardException("exception.CardException.PAYMENT_CARD_DISABLED", 1406);
    public static final CardException CARD_ENABLED
        = new CardException("exception.CardException.CARD_ENABLED", 1407);
    public static final CardException PAYMENT_FROM_DIRECT
        = new CardException("exception.CardException.PAYMENT_FROM_DIRECT", 1408);
    public static final CardException UNKNOWN_RECEIVER_CARD
        = new CardException("exception.CardException.UNKNOWN_RECEIVER_CARD", 1409);
    public static final FundsException SAME_CARD_RECEIVER
        = new FundsException("exception.CardException.SAME_CARD_RECEIVER", 1410);
    public static final CardException RECEIVER_CARD_DISABLED
        = new CardException("exception.CardException.RECEIVER_CARD_DISABLED", 1411);
    public static final CardException INVALID_PAYMENT_PIN
        = new CardException("exception.CardException.INVALID_PAYMENT_PIN", 1412);
    public static final CardException WRONG_CARD_TYPE_CREATION
        = new CardException("exception.CardException.WRONG_CARD_TYPE_CREATION", 1413);
    public static final CardException UNSUPPORTED_FOR_ADMIN_CARD
        = new CardException("exception.CardException.UNSUPPORTED_FOR_ADMIN_CARD", 1414);
    public static final CardException NOT_GROUP_CARD
        = new CardException("exception.CardException.NOT_GROUP_CARD", 1415);
    public static final CardException NO_DIRECT_CARD
        = new CardException("exception.CardException.NO_DIRECT_CARD", 1416);
    public static final CardException UNKNOWN_MEMBER
        = new CardException("exception.CardException.UNKNOWN_MEMBER", 1417);
    public static final CardException MEMBER_IS_CARD_OWNER
        = new CardException("exception.CardException.MEMBER_IS_CARD_OWNER", 1418);
    public static final CardException GROUP_CARD_SENDER
        = new CardException("exception.CardException.GROUP_CARD_SENDER", 1419);
    public static final CardException OPERATION_NOT_PERMITTED
        = new CardException("exception.CardException.OPERATION_NOT_PERMITTED", 1420);

    public CardException(String message, int code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
