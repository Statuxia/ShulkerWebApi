package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class AuthenticationException extends ApiException {

    public static final AuthenticationException UNKNOWN_TOKEN
        = new AuthenticationException("exception.AuthenticationException.UNKNOWN_TOKEN", 1101);
    public static final AuthenticationException TOKEN_LIMIT_REACHED
        = new AuthenticationException("exception.AuthenticationException.TOKEN_LIMIT_REACHED", 1102);
    public static final AuthenticationException TOKEN_DISABLED
        = new AuthenticationException("exception.AuthenticationException.TOKEN_DISABLED", 1103);
    public static final AuthenticationException DISCORD_ACCOUNT_EXPIRED
        = new AuthenticationException("exception.AuthenticationException.DISCORD_ACCOUNT_EXPIRED", 1104);
    public static final AuthenticationException TOKEN_HAS_NO_LIMITATION
        = new AuthenticationException("exception.AuthenticationException.TOKEN_HAS_NO_LIMITATION", 1105);

    public AuthenticationException(String message, int code) {
        super(message, HttpStatus.UNAUTHORIZED, code);
    }
}
