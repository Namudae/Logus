package com.logus.blog.controller;

import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 글 여러개 조회
     * + Pageable 추가
     */
    @GetMapping("/{blogAddress}/posts")
    public List<PostResponseDto> selectAllPosts(@PathVariable("blogAddress") String blogAddress) {
        return postService.selectAllPosts(blogAddress);
    }

    /**
     * 글 한개 조회
     */
    @GetMapping("/{blogAddress}/{postId}")
    public PostResponseDto selectPost(@PathVariable("blogAddress") String blogAddress,
                                      @PathVariable("postId") Long postId) {
        return postService.selectPost(blogAddress, postId);
    }

    /**
     * 글 등록
     */
    @PostMapping("/{blogAddress}/newpost")
    public String createPost(@PathVariable("blogAddress") String blogAddress,
                             @RequestBody PostRequestDto postRequestDto) {
        Long postId = postService.createPost(postRequestDto);
        return "redirect:/" + blogAddress + "/" + postId;
    }
}
