package me.statuxia.shulkerapi.controller.advice;

import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    public Object handle(NoResourceFoundException exception, HttpServletResponse response) {
        return handle(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Object handle(HttpRequestMethodNotSupportedException exception, HttpServletResponse response) {
        return handle(response, HttpStatus.METHOD_NOT_ALLOWED);
    }


    @ExceptionHandler(CredentialsExpiredException.class)
    @ResponseBody
    public Object handle(CredentialsExpiredException exception, HttpServletResponse response) {
        final Map<String, Object> json = handle(response, HttpStatus.FORBIDDEN);
        json.put("message", exception.getMessage());
        logger.debug("response:{}", json);
        return json;
    }

    @ExceptionHandler(AuthenticationException.class)
    @ResponseBody
    public Object handle(AuthenticationException exception, HttpServletResponse response) {
        final Map<String, Object> json = handle(response, HttpStatus.UNAUTHORIZED);
        json.put("message", exception.getMessage());
        logger.debug("response:{}", json);
        return json;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleAny(Exception exception, HttpServletResponse response) {
        final Map<String, Object> json = handle(response, HttpStatus.INTERNAL_SERVER_ERROR);
        logger.error("response:{}", json, exception);
        return json;
    }

    private Map<String, Object> handle(HttpServletResponse response, HttpStatus status) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());

        final Map<String, Object> json = new HashMap<>();
        json.put("code", status.value());
        json.put("message", status.getReasonPhrase());
        json.put("timestamp", System.currentTimeMillis());

        return json;
    }
}
