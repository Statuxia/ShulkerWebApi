package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class DiscordIntegrationApiException extends ApiException {

    public static final DiscordIntegrationApiException NO_DATA
        = new DiscordIntegrationApiException("exception.DiscordIntegrationApiException.NO_DATA", 1001);

    public DiscordIntegrationApiException(String message, int code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
