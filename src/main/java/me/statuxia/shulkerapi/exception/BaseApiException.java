package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class BaseApiException extends ApiException {

    public static final BaseApiException NO_DATA
        = new BaseApiException("exception.BaseApiException.NO_DATA", 1001);
    public static final BaseApiException INCORRECT_DATA
        = new BaseApiException("exception.BaseApiException.INCORRECT_DATA", 1001);

    public BaseApiException(String message, int code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
