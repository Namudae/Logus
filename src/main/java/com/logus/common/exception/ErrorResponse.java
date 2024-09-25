package com.logus.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public class ErrorResponse {
    private final int code; //400
//    private HttpStatus status; //Bad Request
    private final String message; //잘못된 요청입니다
    private final List<FieldException> errors;

    @Builder
    public ErrorResponse(int code, String message, List<FieldException> errors) {
        this.code = code;
        this.message = message;
        this.errors = (errors == null) ? new ArrayList<>() : errors;
    }

    public static ErrorResponse createError(ErrorCode errorCode, BindingResult bindingResult) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(FieldException.create(bindingResult))
                .build();
    }

    public static ErrorResponse createError(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

}
