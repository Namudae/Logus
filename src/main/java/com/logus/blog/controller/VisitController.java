package com.logus.blog.controller;

import com.logus.blog.dto.VisitBlogDto;
import com.logus.blog.service.BlogService;
import com.logus.blog.service.VisitService;
import com.logus.common.controller.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;
    private final BlogService blogService;

    /**
     * 월간 방문자수 조회
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') || @blogService.hasPermissionToBlog(#blogId, 'BLOG', 'EDITOR', authentication)")
    @GetMapping("/blog/statistics")
    public ApiResponse<VisitBlogDto> blogStastics(@RequestParam("blogId") Long blogId) {
        VisitBlogDto dto = visitService.selectBlogStastics(blogId);

        return ApiResponse.ok(dto);
    }
}
