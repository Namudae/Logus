package com.logus.common.filter;

import com.logus.common.service.VisitLogService;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class VisitFilter implements Filter {

    @Autowired
    private VisitLogService visitLogService;

    public VisitFilter(VisitLogService visitLogService) {
        this.visitLogService = visitLogService;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        // 방문자 추적 서비스 호출
        visitLogService.trackVisitor(httpRequest, httpResponse);

        // 다음 필터 체인으로 요청 전달
        chain.doFilter(request, response);
    }

//    @Override
//    protected void doFidoFilterlterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        // 방문자 추적 서비스 호출 (모든 URL에서 호출)
//        visitLogService.trackVisitor(request, response);
//
//        filterChain.doFilter(request, response);
//    }
}
