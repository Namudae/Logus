package com.logus.common.exception;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.SdkClientException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Slf4j
@RestControllerAdvice
public class ExceptionController {

    // @Valid 또는 @Validated로 발생하는 유효성 검사 예외 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidationExceptions(MethodArgumentNotValidException e) {
        log.error("[ExceptionHandler] Validation error: ", e);
        return ResponseEntity.status(ErrorCode.INPUT_VALUE_INVALID.getStatus())
                .body(ErrorResponse.createError(ErrorCode.INPUT_VALUE_INVALID, e.getBindingResult()));
    }

    // 커스텀
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(CustomException e) {
        log.error("[ExceptionHandler] CustomException: ", e);
        return ResponseEntity.status(e.getErrorCode().getStatus())
                .body(ErrorResponse.createError(e.getErrorCode()));
    }

    @ExceptionHandler(SdkClientException.class)
    public ResponseEntity<ErrorResponse> handleSdkClientException(SdkClientException e) {
        log.error("[ExceptionHandler] SdkClientException: {}", e.getMessage());
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.createError(ErrorCode.AMAZON_SERVICE_ERROR));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception e) {
        log.error("[ExceptionHandler] Exception: ", e);
        return ResponseEntity.status(INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.createError(ErrorCode.INTERNAL_SERVER_ERROR));
    }


}
