package com.logus.common.service;

import com.logus.blog.entity.Blog;
import com.logus.blog.entity.Visit;
import com.logus.blog.repository.VisitRepository;
import com.logus.blog.service.BlogService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class VisitLogService {

    private final BlogService blogService;
    @Autowired
    private VisitRepository visitRepository;

    public void trackVisitor(HttpServletRequest request, HttpServletResponse response) {
        // 1. 쿠키에서 방문자 고유 ID를 확인
        String visitorId = getVisitorIdFromCookie(request);

        if (visitorId == null) {
            // 2. 쿠키가 없으면 새로운 ID를 생성하고 쿠키에 설정
            visitorId = UUID.randomUUID().toString(); // 고유한 방문자 ID 생성
            setVisitorIdCookie(response, visitorId);
        }

        //블로그 방문일 경우 blogId 저장
        Long blogId = null;
        if (request.getRequestURI().startsWith("/blog-info")) {
            String blogIdParam = request.getParameter("blogId");
            if (blogIdParam != null) {
                blogId = Long.parseLong(blogIdParam); // blogId 파라미터가 있으면 Long으로 변환
            }

        }
        Blog blog = null;
        if (blogId != null) {
            blog = blogService.getById(blogId);
        }

        // 3. 방문 기록을 DB에 저장
        saveVisitorToDatabase(visitorId, blog);
    }

    private String getVisitorIdFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("visitorId".equals(cookie.getName())) {
                    return cookie.getValue();
                }
            }
        }
        return null;
    }

    private void setVisitorIdCookie(HttpServletResponse response, String visitorId) {
//        Cookie cookie = new Cookie("visitorId", visitorId);
//        cookie.setMaxAge(60 * 60 * 24 * 90); // 90일 동안 유효
//        cookie.setPath("/"); // 모든 경로에 대해 유효
//        cookie.setHttpOnly(true); // XSS 방지
////        cookie.setSecure(true); // HTTPS 환경에서만 전송
//        response.addCookie(cookie);

        ResponseCookie cookie = ResponseCookie.from("visitorId", visitorId)
                .httpOnly(true)       // HttpOnly 설정
                .secure(true)         // HTTPS에서만 사용
                .sameSite("None")     // SameSite 설정
                .path("/")
                .maxAge(60 * 60 * 24 * 90) // 90일
                .build();

        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }

    private void saveVisitorToDatabase(String visitorId, Blog blog) {
        // 오늘 날짜 가져오기
        LocalDate today = LocalDate.now();

        //1. 사이트방문(blog 없음) > 빈값 insert
        //2. 블로그 방문(blog 있음) > blogId 마다 insert
        //기타. blog 있는데 처음 방문 > 빈값 insert, 블로그 insert(총 두개)

        if (visitRepository.countByVisitorIdAndCreateDate(visitorId, today) == 0) {
            Visit visit = Visit.builder()
                    .sessionId(visitorId)
                    .createDate(today)
                    .build();
            visitRepository.save(visit);
        }
        if (blog != null) {
            //같은값 있는지 확인
            if (visitRepository.countByVisitorIdAndCreateDateAndBlogId(visitorId, today, blog.getId()) == 0) {
                Visit visit = Visit.builder()
                        .sessionId(visitorId)
                        .blog(blog)
                        .createDate(today)
                        .build();
                visitRepository.save(visit);
            }
        }

    }
}
