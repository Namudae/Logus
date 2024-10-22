package com.logus.common.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.exception.ErrorResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;

import static jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RequiredArgsConstructor
public class Http403Handler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

//    @Override
//    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
//        log.error("[인증오류] 403");
//
//        ErrorResponse errorResponse = ErrorResponse.builder()
//                .code(403)
//                .message("접근할 수 없습니다.")
//                .build();
//
//        response.setContentType(APPLICATION_JSON_VALUE);
//        response.setCharacterEncoding(UTF_8.name());
//        response.setStatus(SC_FORBIDDEN);
//        objectMapper.writeValue(response.getWriter(), errorResponse);
//    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        // 응답 상태를 403으로 설정
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        // ErrorResponse를 이용해 커스텀 응답
        ErrorResponse errorResponse = ErrorResponse.createError(ErrorCode.UNAUTHORIZED_REQUEST);

        // ErrorResponse를 JSON으로 변환해 응답
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

}
