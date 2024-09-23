package com.logus.common.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    //***하는중
    // @Valid 또는 @Validated로 발생하는 유효성 검사 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
//        Map<String, String> errors = new HashMap<>();
//        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
//        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
        log.error("[ExceptionHandler] Validation error: ", e);
        ErrorResponse response = ErrorResponse.createError(ErrorCode.INPUT_VALUE_INVALID, e.getBindingResult());
        return ResponseEntity.status(ErrorCode.INPUT_VALUE_INVALID.getCode()).body(response);
    }
    //ㄴvalid 수정전
//    @ExceptionHandler(MethodArgumentNotValidException.class)
//    public ResponseEntity<Object> handleValidationExceptions(MethodArgumentNotValidException e) {
//        Map<String, String> errors = new HashMap<>();
//        e.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
//        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
//    }

    //new...
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
//        ErrorCode errorCode = e.getErrorCode();
//        return ResponseEntity
//                .status(errorCode.getStatus())
//                .body(ErrorResponse.of(errorCode));

        log.error("[ExceptionHandler] CustomException: ", e);
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ErrorResponse.createError(e.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
//        return ResponseEntity
//                .status(INTERNAL_SERVER_ERROR)
//                .body(ErrorResponse.of(500, INTERNAL_SERVER_ERROR, e.getMessage()));

        log.error("[ExceptionHandler] Exception: ", e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.createError(ErrorCode.INTERNAL_SERVER_ERROR));
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
