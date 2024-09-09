package com.logus.blog.controller;

import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 글 여러개 조회
     * + Pageable 추가해야됨
     */
    @GetMapping("/{blogAddress}/posts")
    public List<PostResponseDto> selectAllBlogPosts(@PathVariable("blogAddress") String blogAddress) {
        return postService.selectAllBlogPosts(blogAddress);
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
     * 글 등록(임시)
     */
    @PostMapping("/{blogAddress}/postv1")
    public String createPostV1(@PathVariable("blogAddress") String blogAddress,
                             @RequestBody PostRequestDto postRequestDto) {
        Long postId = postService.createPost(postRequestDto);
        return "redirect:/" + blogAddress + "/" + postId;
    }

    /**
     * 글 등록(+사진 첨부)
     */
//    @PostMapping("/{blogAddress}/post")
//    public String createPost(@PathVariable("blogAddress") String blogAddress,
//                             @ModelAttribute PostRequestDto postRequestDto,
//                             @RequestParam("images") MultipartFile[] images) throws IOException {
//
//        if (images != null) {
//            for (MultipartFile image : images) {
//                String fullPath = fileDir + image.getOriginalFilename(); //file.getOriginalFilename: 사용자가 업로드한 파일명
//                image.transferTo(new File(fullPath)); //file.transferto(): 파일저장
//            }
//        }
//
//        Long postId = postService.createPost(postRequestDto);
//        return "redirect:/" + blogAddress + "/" + postId;
//    }
}
