package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class MeException extends ApiException {

    public static final MeException NO_ARGS
        = new MeException("exception.MeException.NO_ARGS", 1801);

    public MeException(String message, int code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
