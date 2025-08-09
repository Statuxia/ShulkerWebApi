package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class ApiException extends RuntimeException {

    public static final int DEFAULT_ERROR_CODE_VALUE = 1000;

    private final HttpStatus status;
    private final int code;

    public ApiException(String message, HttpStatus status) {
        super(message);
        this.status = status;
        this.code = DEFAULT_ERROR_CODE_VALUE;
    }

    public ApiException(String message, HttpStatus status, int code) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public int getCode() {
        return code;
    }
}
