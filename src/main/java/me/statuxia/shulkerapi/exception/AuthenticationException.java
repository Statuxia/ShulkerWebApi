package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends ApiException {

    public static final AuthenticationException UNKNOWN_SESSION_TOKEN
        = new AuthenticationException("exception.AuthenticationException.UNKNOWN_SESSION_TOKEN", 1101);
    public static final AuthenticationException UNKNOWN_ADMIN_TOKEN
        = new AuthenticationException("exception.AuthenticationException.UNKNOWN_ADMIN_TOKEN", 1102);
    public static final AuthenticationException UNKNOWN_DEV_TOKEN
        = new AuthenticationException("exception.AuthenticationException.UNKNOWN_DEV_TOKEN", 1103);
    public static final AuthenticationException DISCORD_ACCOUNT_EXPIRED
        = new AuthenticationException("exception.AuthenticationException.DISCORD_ACCOUNT_EXPIRED", 1104);

    public AuthenticationException(String message, int code) {
        super(message, HttpStatus.UNAUTHORIZED, code);
    }
}
