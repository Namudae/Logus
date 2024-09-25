package com.logus.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 1001~: 공통 */
    INPUT_VALUE_INVALID(1001, HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),

    /* 500 GLOBAL */
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류입니다."),


    /* 404 NOT_FOUND: Resource를 찾을 수 없음 */
    BLOG_NOT_FOUND(404, HttpStatus.NOT_FOUND, "존재하지 않는 페이지입니다."),
    POST_NOT_FOUND(404, HttpStatus.NOT_FOUND, "존재하지 않는 페이지입니다."),
    MEMBER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다.");

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */

    private final int code;
    private final HttpStatus status;
    private final String message;
}
