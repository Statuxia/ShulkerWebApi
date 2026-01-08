package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class AccountException extends ApiException {

    public static final AccountException UNKNOWN_ACCOUNT
        = new AccountException("exception.AccountException.UNKNOWN_ACCOUNT", 1201);
    public static final AccountException UNKNOWN_ACTION_ACCOUNT
        = new AccountException("exception.AccountException.UNKNOWN_ACTION_ACCOUNT", 1202);
    public static final AccountException NICKNAME_ALREADY_TAKEN
        = new AccountException("exception.AccountException.NICKNAME_ALREADY_TAKEN", 1203);
    public static final AccountException ALREADY_LINKED_ACCOUNT
        = new AccountException("exception.AccountException.ALREADY_LINKED_ACCOUNT", 1204);
    public static final AccountException NOT_PAID_ACCOUNT
        = new AccountException("exception.AccountException.NOT_PAID_ACCOUNT", 1205);
    public static final AccountException UNKNOWN_LINK_ACCOUNT_STORAGE
        = new AccountException("exception.AccountException.UNKNOWN_LINK_ACCOUNT_STORAGE", 1206);
    public static final AccountException LINK_NO_MAIN_ACCOUNT
        = new AccountException("exception.AccountException.LINK_NO_MAIN_ACCOUNT", 1207);
    public static final AccountException LINK_MAIN_ACCOUNT
        = new AccountException("exception.AccountException.LINK_MAIN_ACCOUNT", 1208);
    public static final AccountException ACCESS_DENIED
        = new AccountException("exception.AccountException.ACCESS_DENIED", 1209);
    public static final AccountException NO_LINKED_ACCOUNTS
        = new AccountException("exception.AccountException.NO_LINKED_ACCOUNTS", 1210);

    public AccountException(String message, int code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
