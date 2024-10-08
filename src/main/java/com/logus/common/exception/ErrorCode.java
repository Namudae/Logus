package com.logus.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

    /* 1001~: 공통 */
    INPUT_VALUE_INVALID(1001, HttpStatus.BAD_REQUEST, "입력값이 올바르지 않습니다."),
    FILE_NOT_FOUND(1002, HttpStatus.BAD_REQUEST, "존재하지 않는 파일입니다."),
    IMAGE_SERVER_ERROR(1003, HttpStatus.INTERNAL_SERVER_ERROR, "이미지 서버 오류입니다."),
    AMAZON_SERVICE_ERROR(1004, HttpStatus.INTERNAL_SERVER_ERROR, "파일 업로드에 실패했습니다."),

    /* 2001~: 회원 */
    MEMBER_NOT_FOUND(2001, HttpStatus.NOT_FOUND, "존재하지 않는 회원입니다."),

    /* 3001~: 블로그 */
    BLOG_NOT_FOUND(3001, HttpStatus.NOT_FOUND, "존재하지 않는 페이지입니다."),
    POST_NOT_FOUND(3002, HttpStatus.NOT_FOUND, "존재하지 않는 페이지입니다."),
    COMMENT_NOT_FOUND(3003, HttpStatus.NOT_FOUND, "존재하지 않는 댓글입니다."),

    /* 1000번대 이하: 상태 코드 */
    INTERNAL_SERVER_ERROR(500, HttpStatus.INTERNAL_SERVER_ERROR, "내부 서버 오류입니다.");

    private final int code;
    private final HttpStatus status;
    private final String message;
}
