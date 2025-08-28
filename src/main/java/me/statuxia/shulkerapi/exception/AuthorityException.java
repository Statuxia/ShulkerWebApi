package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class AuthorityException extends ApiException {

    public static final AuthenticationException AUTHORITY_ACCESS_DENIED
        = new AuthenticationException("exception.AuthorityException.AUTHORITY_ACCESS_DENIED", 1301);

    public AuthorityException(String message, int code) {
        super(message, HttpStatus.FORBIDDEN, code);
    }
}
