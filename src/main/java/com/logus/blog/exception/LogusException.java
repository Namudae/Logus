package com.logus.blog.exception;

import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
//최상위 Exception 클래스 만들고 다른 클래스에서 상속
public abstract class LogusException extends RuntimeException {

    public final Map<String, String> validation = new HashMap<>();

    public LogusException(String message) {
        super(message);
    }

    public LogusException(String message, Throwable cause) {
        super(message, cause);
    }

    public abstract int getStatusCode();

    public void addValidation(String fieldName, String message) {
        validation.put(fieldName, message);
    }
}
