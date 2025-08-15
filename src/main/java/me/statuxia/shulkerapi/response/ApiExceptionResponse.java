package me.statuxia.shulkerapi.response;

import java.util.StringJoiner;

public class ApiExceptionResponse {

    private int code;
    private String message;
    private Long timestamp;

    public ApiExceptionResponse() {
    }

    public ApiExceptionResponse(int code, String message) {
        this.code = code;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ApiExceptionResponse.class.getSimpleName() + "[", "]")
            .add("code=" + code)
            .add("message='" + message + "'")
            .add("timestamp=" + timestamp)
            .toString();
    }
}
