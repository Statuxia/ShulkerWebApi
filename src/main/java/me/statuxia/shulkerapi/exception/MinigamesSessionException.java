package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class MinigamesSessionException extends ApiException {

    public static final AccountException SESSION_NOT_STARTED
        = new AccountException("exception.MinigamesSessionException.SESSION_NOT_STARTED", 3000);

    public MinigamesSessionException(String message, int code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
