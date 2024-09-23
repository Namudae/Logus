package com.logus.common.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class FieldException {
    private String field;
    private String value;
    private String reason;

    @Builder
    public FieldException(String field, String value, String reason) {
        this.field = field;
        this.value = value;
        this.reason = reason;
    }

    public static List<FieldException> create(BindingResult bindingResult) {
        List<FieldError> fieldException = bindingResult.getFieldErrors();
        return fieldException.stream()
                .map(error -> new FieldException(
                        error.getField(),
                        (error.getRejectedValue() == null) ? null : error.getRejectedValue().toString(),
                        error.getDefaultMessage()))
                .collect(Collectors.toList());
    }
}
