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
    @PostMapping("/register/blog")
    public ApiResponse<Map<String, Long>> createBlog(@RequestBody BlogRequestDto blogRequestDto) {
        Long blogId = blogService.createBlog(blogRequestDto);
        return ApiResponse.ok(Map.of("blogId", blogId));
    }

    /**
     * 블로그 정보 변경
     */
    @PutMapping("/blog/setting")
    public ApiResponse<Map<String, Long>> updateBlog(@RequestParam("blogId") Long blogId,
                                                     @RequestBody BlogRequestDto blogRequestDto) {
        blogService.updateBlog(blogId, blogRequestDto);
        return ApiResponse.ok(Map.of("blogId", blogId));
    }

    /**
     * 블로그 주소 중복 확인
     */
    @GetMapping("/blog/address-dupl")
    public ApiResponse<String> duplicateBlogAddress(@RequestParam String blogAddress) {
        blogService.duplicateBlogAddress(blogAddress);
        return ApiResponse.ok();
    }

    /**
     * 시리즈 조회
     */
    @GetMapping("/series")
    public ApiResponse<List<SeriesResponseDto>> selectSeries(@RequestParam("blogId") Long blogId) {
        List<SeriesResponseDto> series = blogService.selectSeries(blogId);

        return ApiResponse.ok(series);
    }

    /**
     * 블로그 id 조회
     */
    @GetMapping("/blog-id")
    public ApiResponse<Map<String, Long>> getBlogIdByAddress(@RequestParam("blogAddress") String blogAddress) {
        Long blogId = blogService.getBlogIdByAddress(blogAddress);

        return ApiResponse.ok(Map.of("blogId", blogId));
    }

}
