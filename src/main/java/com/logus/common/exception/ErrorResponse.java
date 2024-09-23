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
    private int status; //400
    private HttpStatus code; //Bad Request
    private String message; //잘못된 요청입니다
    private List<FieldException> errors;

    @Builder
    public ErrorResponse(int status, HttpStatus code, String message, List<FieldException> errors) {
        this.status = status;
        this.code = code;
        this.message = message;
        this.errors = (errors == null) ? new ArrayList<>() : errors;
    }

    public static ErrorResponse createError(ErrorCode errorCode, BindingResult bindingResult) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .errors(FieldException.create(bindingResult))
                .build();
    }

    public static ErrorResponse createError(ErrorCode errorCode) {
        return ErrorResponse.builder()
                .status(errorCode.getStatus())
                .code(errorCode.getCode())
                .message(errorCode.getMessage())
                .build();
    }

    //    public static ErrorResponse of(int status, HttpStatus code, String message) {
//        return new ErrorResponse(status, code, message);
//    }
//
//    public static ErrorResponse of(ErrorCode errorCode) {
//        int status = errorCode.getStatus();
//        HttpStatus code = errorCode.getCode();
//        String message = errorCode.getMessage();
//        return new ErrorResponse(status, code, message);
//    }
}
