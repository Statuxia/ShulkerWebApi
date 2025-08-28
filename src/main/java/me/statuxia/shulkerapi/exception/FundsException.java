package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class FundsException extends ApiException {

    public static final FundsException NOT_ENOUGH_FUNDS
        = new FundsException("exception.FundsException.NOT_ENOUGH_FUNDS", 1501);
    public static final FundsException AMOUNT_GREATER_ZERO
        = new FundsException("exception.FundsException.AMOUNT_GREATER_ZERO", 1502);

    public FundsException(String message, int code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
