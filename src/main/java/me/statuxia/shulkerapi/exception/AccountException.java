package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class AccountException extends ApiException {

    public static final AccountException UNKNOWN_ACCOUNT
        = new AccountException("exception.AccountException.UNKNOWN_ACCOUNT", 1201);
    public static final AccountException UNKNOWN_ACTION_ACCOUNT
        = new AccountException("exception.AccountException.UNKNOWN_ACTION_ACCOUNT", 1202);
    public static final AccountException NICKNAME_ALREADY_TAKEN
        = new AccountException("exception.AccountException.NICKNAME_ALREADY_TAKEN", 1203);

    public AccountException(String message, int code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
