package com.logus.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 1001~: 공통 */
    INPUT_VALUE_INVALID(1001, HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

    /* 2001~: 회원 */
    MEMBER_NOT_FOUND(2001, HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),

    /* 3001~: 블로그 */
    BLOG_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "존재하지 않는 페이지입니다."),
    POST_NOT_FOUND(3002, HttpStatus.NOT_FOUND, "존재하지 않는 페이지입니다."),

    /* 1000번대 이하: 상태 코드 */
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류입니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
