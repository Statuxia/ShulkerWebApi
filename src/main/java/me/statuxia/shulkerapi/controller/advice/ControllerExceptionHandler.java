package me.statuxia.shulkerapi.controller.advice;

import jakarta.servlet.http.HttpServletResponse;
import me.statuxia.shulkerapi.exception.ApiException;
import me.statuxia.shulkerapi.service.impl.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class ControllerExceptionHandler {

    public static final int DEFAULT_ERROR_CODE_VALUE = 1000;

    private final Logger logger = LoggerFactory.getLogger(getClass());
    private final MessageService messageService;

    @Autowired
    public ControllerExceptionHandler(MessageService messageService) {
        this.messageService = messageService;
    }

    @ExceptionHandler(NoResourceFoundException.class)
    @ResponseBody
    public Object handle(NoResourceFoundException exception, HttpServletResponse response) {
        return handleDefaultException(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    @ResponseBody
    public Object handle(MissingServletRequestParameterException exception, HttpServletResponse response) {
        final Map<String, Object> json = handleDefaultException(response, HttpStatus.BAD_REQUEST);
        json.put("message", exception.getMessage());
        return json;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Object handle(HttpRequestMethodNotSupportedException exception, HttpServletResponse response) {
        return handleDefaultException(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public Object handle(ApiException exception, HttpServletResponse response) {
        final Map<String, Object> json = handleApiException(response, exception);
        logger.debug("response:{}", json);
        return json;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleAny(Exception exception, HttpServletResponse response) {
        final Map<String, Object> json = handleDefaultException(response, HttpStatus.INTERNAL_SERVER_ERROR);
        logger.error("response:{}", json, exception);
        return json;
    }

    private Map<String, Object> handleDefaultException(HttpServletResponse response, HttpStatus status) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());

        final Map<String, Object> json = new HashMap<>();
        json.put("code", DEFAULT_ERROR_CODE_VALUE);
        json.put("message", status.getReasonPhrase());
        json.put("timestamp", System.currentTimeMillis());

        return json;
    }

    private Map<String, Object> handleApiException(HttpServletResponse response, ApiException exception) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(exception.getStatus().value());

        final Map<String, Object> json = new HashMap<>();
        json.put("code", exception.getCode());
        json.put("message", getMessageService().message(exception.getMessage()));
        json.put("timestamp", System.currentTimeMillis());

        return json;
    }

    public MessageService getMessageService() {
        return messageService;
    }
}
