package com.logus.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    //new...
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handException(Exception e) {
//        e.printStackTrace();
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(500, INTERNAL_SERVER_ERROR, e.getMessage()));
    }

    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleLogusException(CustomException e) {
//        e.printStackTrace();
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ErrorResponse.of(errorCode));
    }

    // @Valid 또는 @Validated로 발생하는 유효성 검사 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
        Map<String, String> errors = new HashMap<>();
        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ErrorResponse illegalExHandler(IllegalArgumentException e) {
//        log.error("[exceptionHandler] ex", e);
//        return new ErrorResponse("Bad", e.getMessage());
//    }

//    @ExceptionHandler
//    public ResponseEntity<ErrorResponse> logusExHandler(LogusException e) {
//        log.error("[exceptionHandler] ex", e);
//        ErrorResponse errorResponse = new ErrorResponse("USER-EX", e.getMessage());
//        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//    }

//    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
//    @ExceptionHandler
//    public ErrorResponse exHandler(Exception e) {
//        log.error("[exceptionHandler] ex", e);
//        return new ErrorResponse("EX", "내부 오류");
//    }

}
