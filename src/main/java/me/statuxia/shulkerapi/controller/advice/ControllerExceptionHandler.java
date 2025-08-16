package me.statuxia.shulkerapi.controller.advice;

import jakarta.servlet.http.HttpServletResponse;
import me.statuxia.shulkerapi.exception.ApiException;
import me.statuxia.shulkerapi.response.ApiExceptionResponse;
import me.statuxia.shulkerapi.service.impl.MessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

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
        final ApiExceptionResponse apiResponse = handleDefaultException(response, HttpStatus.BAD_REQUEST);
        apiResponse.setMessage(exception.getMessage());
        return apiResponse;
    }

    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @ResponseBody
    public Object handle(HttpRequestMethodNotSupportedException exception, HttpServletResponse response) {
        return handleDefaultException(response, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    @ResponseBody
    public Object handle(HttpMediaTypeNotSupportedException exception, HttpServletResponse response) {
        final ApiExceptionResponse apiResponse = handleDefaultException(response, HttpStatus.BAD_REQUEST);
        apiResponse.setMessage(getMessageService().message(
            "exception.contentType",
            List.of(exception.getContentType())
        ));
        return apiResponse;
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Object handle(HttpMessageNotReadableException exception, HttpServletResponse response) {
        final ApiExceptionResponse apiResponse = handleDefaultException(response, HttpStatus.BAD_REQUEST);
        apiResponse.setMessage(getMessageService().message("exception.messageBody"));
        return apiResponse;
    }

    @ExceptionHandler(ApiException.class)
    @ResponseBody
    public Object handle(ApiException exception, HttpServletResponse response) {
        final ApiExceptionResponse apiResponse = handleApiException(response, exception);
        logger.debug("response:{}", apiResponse);
        return apiResponse;
    }

    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Object handleAny(Exception exception, HttpServletResponse response) {
        final ApiExceptionResponse apiResponse = handleDefaultException(response, HttpStatus.INTERNAL_SERVER_ERROR);
        logger.error("response:{}", apiResponse, exception);
        return apiResponse;
    }

    private ApiExceptionResponse handleDefaultException(HttpServletResponse response, HttpStatus status) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(status.value());


        return new ApiExceptionResponse(DEFAULT_ERROR_CODE_VALUE, status.getReasonPhrase());
    }

    private ApiExceptionResponse handleApiException(HttpServletResponse response, ApiException exception) {
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(exception.getStatus().value());

        return new ApiExceptionResponse(exception.getCode(), getMessageService().message(exception.getMessage()));
    }

    public MessageService getMessageService() {
        return messageService;
    }
}
