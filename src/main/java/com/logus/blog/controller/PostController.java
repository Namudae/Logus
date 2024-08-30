package com.logus.blog.controller;

import com.logus.blog.dto.PostDto;
import com.logus.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 글 여러개 조회
     * + Pageable 추가
     */
    @GetMapping("/{blogAddress}")
    public List<PostDto.PostResponse> searchPostT1(@PathVariable("blogAddress") String blogAddress) {
        return postService.getAllPosts(blogAddress);
    }

    /**
     * 글 한개 조회
     */
}
