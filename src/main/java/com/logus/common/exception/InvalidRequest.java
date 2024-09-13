package com.logus.common.exception;

import lombok.Getter;

/**
 * status -> 400
 */
@Getter
public class InvalidRequest extends LogusException {

    private static final String MESSAGE = "잘못된 요청입니다.";

    public InvalidRequest() {
        super(MESSAGE);
    }

    public InvalidRequest(String fieldName, String message) {
        super(message);
        addValidation(fieldName, message);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }

}
