package com.logus.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 500 GLOBAL */
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류입니다."),

    /* 400 BAD_REQUEST : 잘못된 요청 */

    /* 404 NOT_FOUND: Resource를 찾을 수 없음 */
    BLOG_NOT_FOUND(404, HttpStatus.NOT_FOUND, "존재하지 않는 페이지입니다."),
    POST_NOT_FOUND(404, HttpStatus.NOT_FOUND, "존재하지 않는 페이지입니다."),
    MEMBER_NOT_FOUND(404, HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다.");

    /* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */


    private final int status;
    private final HttpStatus code;
    private final String message;
}
