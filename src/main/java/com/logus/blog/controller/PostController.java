package com.logus.blog.controller;

import com.logus.blog.dto.PostListResponseDto;
import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.Post;
import com.logus.blog.service.PostService;
import com.logus.common.controller.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 블로그의 전체게시글 조회(Pageable 없는 버전)
     */
//    @GetMapping("/{blogAddress}/posts")
//    public List<PostResponseDto> selectAllBlogPosts(@PathVariable("blogAddress") String blogAddress) {
//        return postService.selectAllBlogPosts(blogAddress);
//    }

    /**
     * 블로그의 전체게시글 조회
     * + Pageable
     */
    @GetMapping("/{blogAddress}/posts")
    public ApiResponse<Page<PostListResponseDto>> selectAllBlogPosts(@PathVariable("blogAddress") String blogAddress, Pageable pageable) {
        Page<PostListResponseDto> posts = postService.selectAllBlogPosts(blogAddress, pageable);

        return ApiResponse.ok(posts);
    }

    /**
     * 블로그 내부 검색(제목+내용)
     * + Pageable
     * http://localhost:8082/blog1/search?page=0&size=10&keyword=1번째
     */
    @GetMapping("/{blogAddress}/search")
    public ApiResponse<Page<PostListResponseDto>> searchBlogPosts(@PathVariable("blogAddress") String blogAddress,
                                                                 @RequestParam(value="keyword", required = false) String keyword,
                                                                 Pageable pageable) {
        Page<PostListResponseDto> pagePosts = postService.searchBlogPosts(blogAddress, keyword, pageable);

        return ApiResponse.ok(pagePosts);
    }

    /**
     * 글 한개 조회
     */
    @GetMapping("/{blogAddress}/{postId}")
    public ApiResponse<PostResponseDto> selectPost(@PathVariable("blogAddress") String blogAddress,
                                      @PathVariable("postId") Long postId) {

        return ApiResponse.ok(postService.selectPost(blogAddress, postId));
    }

    /**
     * 글 등록(임시)
     */
//    @PostMapping("/{blogAddress}/postv1")
//    public String createPostV1(@PathVariable("blogAddress") String blogAddress,
//                             @RequestBody PostRequestDto postRequestDto) {
//        Long postId = postService.createPost(postRequestDto, null);
//        return "redirect:/" + blogAddress + "/" + postId;
////        return ApiResponse.ok(new PostResponseDto(postId));
//    }

    /**
     * 글 등록(+사진 첨부)
     * + 썸네일 작업중
     */
    @PostMapping("/{blogAddress}/post")
    public ApiResponse<Map<String, Long>> createPost(@PathVariable("blogAddress") String blogAddress,
                             @RequestPart("requestDto") @Valid PostRequestDto postRequestDto,
                             @RequestPart(value = "thumbImg", required = false) MultipartFile thumbImg
    ) throws MethodArgumentNotValidException, IOException {
        Long postId = postService.createPost(postRequestDto, thumbImg);
        return ApiResponse.ok(Map.of("postId", postId));
    }

    /**
     * 글 수정
     * - 지우는 사진 어떻게할지... 프론트에서 지우는 순간 메서드 호출할수 있는지 or 수정 전후 비교?
     */
    @PatchMapping("/{blogAddress}/post/{postId}")
    public ApiResponse<Map<String, Long>> updatePost(@PathVariable("blogAddress") String blogAddress,
                                                     @PathVariable("postId") Long postId,
                                                     @RequestPart("requestDto") @Valid PostRequestDto postRequestDto
    ) throws MethodArgumentNotValidException {
        postService.updatePost(postId, postRequestDto);
        return ApiResponse.ok(Map.of("postId", postId));
    }


    /**
     * 글 삭제
     *  - 댓글 delYn 처리
     *  - postTag delete
     *  - attachment db delete(s3에서도 삭제)
     */


}
