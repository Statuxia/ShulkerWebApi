package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class FineException extends ApiException {

    public static final FineException UNKNOWN_FINE
        = new FineException("exception.FineException.UNKNOWN_FINE", 1901);
    public static final FineException FINE_STATUS_FINALIZED
        = new FineException("exception.FineException.FINE_STATUS_FINALIZED", 1902);
    public static final FineException DUE_DATE_IN_PAST
        = new FineException("exception.FineException.DUE_DATE_IN_PAST", 1903);

    public FineException(String message, int code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
