package com.logus.blog.exception;

import com.logus.common.exception.LogusException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * status -> 404
 */
@ResponseStatus(HttpStatus.NOT_FOUND)
public class PostNotFound extends LogusException {

    private static final String MESSAGE = "존재하지 않는 글입니다.";

    public PostNotFound() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 404;
    }
}
