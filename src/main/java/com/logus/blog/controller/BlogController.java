package com.logus.blog.controller;

import com.logus.blog.dto.BlogRequestDto;
import com.logus.blog.dto.BlogResponseDto;
import com.logus.blog.entity.Blog;
import com.logus.blog.repository.BlogRepository;
import com.logus.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;

    /**
     * 블로그 정보 조회
     */

    /**
     * 개인 블로그 등록
     */
    @PostMapping("/newblog")
    public ResponseEntity<Long> createBlog(@RequestBody BlogRequestDto blogRequestDto) {
        Long blogId = blogService.createBlog(blogRequestDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(blogId); // 201 Created
    }


}
