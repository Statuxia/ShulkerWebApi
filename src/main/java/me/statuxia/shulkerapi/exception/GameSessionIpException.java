package me.statuxia.shulkerapi.exception;

import org.springframework.http.HttpStatus;

public class GameSessionIpException extends ApiException {
    public static final GameSessionIpException UNKNOWN_GAME_SESSION
        = new GameSessionIpException("exception.GameSessionIpException.UNKNOWN_GAME_SESSION", 2001);
    public static final GameSessionIpException UNSUPPORTED_REQUEST_STATE
        = new GameSessionIpException("exception.GameSessionIpException.UNSUPPORTED_REQUEST_STATE", 2002);
    public static final GameSessionIpException UNSUPPORTED_TO_CHANGE_STATE
        = new GameSessionIpException("exception.GameSessionIpException.UNSUPPORTED_TO_CHANGE_STATE", 2003);

    public GameSessionIpException(String message, int code) {
        super(message, HttpStatus.BAD_REQUEST, code);
    }
}
