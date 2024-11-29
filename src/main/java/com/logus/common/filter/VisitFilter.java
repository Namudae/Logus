package com.logus.common.filter;

import com.logus.common.service.VisitService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class VisitFilter extends OncePerRequestFilter {

    @Autowired
    private VisitService visitorTrackingService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        // 방문자 추적 서비스 호출 (모든 URL에서 호출)
        visitorTrackingService.trackVisitor(request, response);

        filterChain.doFilter(request, response);
    }
}
