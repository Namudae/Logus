package com.logus.blog.controller;

import com.logus.blog.dto.BlogMemberResponseDto;
import com.logus.blog.dto.BlogRequestDto;
import com.logus.blog.dto.BlogResponseDto;
import com.logus.blog.dto.SeriesResponseDto;
import com.logus.blog.service.BlogService;
import com.logus.common.controller.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    /**
     * 블로그 정보 조회
     */
    @GetMapping("/blog-info")
    public ApiResponse<BlogResponseDto> blogInfo(@RequestParam("blogId") Long blogId) {
        BlogResponseDto dto = blogService.selectBlogInfo(blogId);

        return ApiResponse.ok(dto);
    }

    /**
     * 블로그 등록
     */
    @PostMapping("/newblog")
    public ApiResponse<Map<String, Long>> createBlog(@RequestBody BlogRequestDto blogRequestDto) {
        Long blogId = blogService.createBlog(blogRequestDto);
        return ApiResponse.ok(Map.of("blogId", blogId));
    }

    /**
     * 시리즈 조회
     */
    @GetMapping("/series")
    public ApiResponse<List<SeriesResponseDto>> selectSeries(@RequestParam("blogId") Long blogId) {
        List<SeriesResponseDto> series = blogService.selectSeries(blogId);

        return ApiResponse.ok(series);
    }

}
