package com.logus.blog.controller;

import com.logus.blog.dto.VisitBlogDto;
import com.logus.blog.service.VisitService;
import com.logus.common.controller.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class VisitController {

    private final VisitService visitService;

    /**
     * 월간 방문자수 조회
     */
    @GetMapping("/blog/statistics")
    public ApiResponse<VisitBlogDto> blogStastics(@RequestParam("blogId") Long blogId) {
        VisitBlogDto dto = visitService.selectBlogStastics(blogId);

        return ApiResponse.ok(dto);
    }
}
