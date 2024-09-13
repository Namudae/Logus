package com.logus.common.exception;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.http.HttpStatus;

@Data
@AllArgsConstructor
public class ErrorResponse {
    private int status; //400
    private HttpStatus code; //Bad Request
    private String message; //잘못된 요청입니다

    public static ErrorResponse of(int status, HttpStatus code, String message) {
        return new ErrorResponse(status, code, message);
    }

    public static ErrorResponse of(ErrorCode errorCode) {
        int status = errorCode.getStatus();
        HttpStatus code = errorCode.getCode();
        String message = errorCode.getMessage();
        return new ErrorResponse(status, code, message);
    }
}
