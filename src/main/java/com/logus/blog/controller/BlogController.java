package com.logus.blog.controller;

import com.logus.blog.dto.BlogRequestDto;
import com.logus.blog.dto.BlogResponseDto;
import com.logus.blog.entity.Blog;
import com.logus.blog.repository.BlogRepository;
import com.logus.blog.service.BlogService;
import com.logus.common.controller.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    /**
     * 블로그 정보 조회
     */

    /**
     * 블로그 등록
     */
    @PostMapping("/newblog")
    public ApiResponse<Map<String, Long>> createBlog(@RequestBody BlogRequestDto blogRequestDto) {
        Long blogId = blogService.createBlog(blogRequestDto);
        return ApiResponse.ok(Map.of("blogId", blogId));
    }


}
