package com.logus.blog.controller;

import com.logus.blog.dto.PostListResponseDto;
import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.Post;
import com.logus.blog.service.PostService;
import com.logus.common.controller.ApiResponse;
import com.logus.common.security.JwtService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
     * 블로그의 전체게시글 조회
     * + Pageable
     * http://localhost:8082/posts?blogId=1&size=10&page=0
     */
    @GetMapping("/posts")
    public ApiResponse<Page<PostListResponseDto>> selectAllBlogPosts(@RequestParam("blogId") Long blogId,
                                                                     @RequestParam(value = "seriesId", required = false) Long seriesId,
                                                                     Pageable pageable,
                                                                     HttpServletRequest request) {
        Page<PostListResponseDto> posts = postService.selectAllBlogPosts(blogId, seriesId, pageable, request);

        return ApiResponse.ok(posts);
    }

    /**
     * 글 한개 조회
     */
    @GetMapping("/posts/{postId}")
    public ApiResponse<PostResponseDto> selectPost(@PathVariable("postId") Long postId, HttpServletRequest request) {

        return ApiResponse.ok(postService.selectPost(postId, request));
    }


    /**
     * 글 등록
     */
    @PostMapping("/posts")
    public ApiResponse<Map<String, Long>> createPost(@RequestPart("requestDto") @Valid PostRequestDto postRequestDto,
                                                     @RequestPart(value = "thumbImg", required = false) MultipartFile thumbImg
    ) throws MethodArgumentNotValidException, IOException {
        Long postId = postService.createPost(postRequestDto, thumbImg);
        return ApiResponse.ok(Map.of("postId", postId));
    }

    /**
     * 글 수정
     */
    @PreAuthorize("@postService.hasPermissionToPost(#postId, authentication)")
    @PutMapping("/posts/{postId}")
    public ApiResponse<Map<String, Long>> updatePost(@PathVariable("postId") Long postId,
                                                     @RequestPart("requestDto") @Valid PostRequestDto postRequestDto,
                                                     @RequestPart(value = "thumbImg", required = false) MultipartFile thumbImg,
                                                     @RequestParam(value = "deleteThumb", required = false, defaultValue = "false") Boolean deleteThumb
    ) throws MethodArgumentNotValidException, IOException {
        postService.updatePost(postId, postRequestDto, thumbImg, deleteThumb);
        return ApiResponse.ok(Map.of("postId", postId));
    }

    /**
     * 글 삭제
     */
//    @PreAuthorize("hasRole('ROLE_USER') && hasPermission(#postId, 'Post', 'DELETE')")
    @PreAuthorize("@postService.hasPermissionToPost(#postId, authentication)")
    @DeleteMapping("/posts/{postId}")
    public ApiResponse<String> deletePost(@PathVariable("postId") Long postId) throws MethodArgumentNotValidException {
        postService.deletePost(postId);
        return ApiResponse.ok();
    }

    /**
     * 임시저장 게시글 조회
     */
    public ApiResponse<PostResponseDto> selectTempPost(@RequestParam("blogId") Long blogId, HttpServletRequest request) {

        return ApiResponse.ok(postService.selectTempPost(blogId, request));
    }


    /**
     * 태그 검색(블로그 내부)
     * http://localhost:8082/posts/tag-search?blogId=1&tag=JAVA&size=10&page=0
     */
    @GetMapping("/posts/tag-search")
    public ApiResponse<Page<PostListResponseDto>> searchBlogPostsByTag(@RequestParam("blogId") Long blogId,
                                                                       @RequestParam("tag") String tag,
                                                                       Pageable pageable) {
        Page<PostListResponseDto> pagePosts = postService.searchBlogPostsByTag(blogId, tag, pageable);
        return ApiResponse.ok(pagePosts);
    }


    /**
     * 블로그 내부 검색(제목+내용)
     * + Pageable
     * http://localhost:8082/blog-search?blogId=1&size=10&page=0&keyword=번째
     */
    @GetMapping("/posts/blog-search")
    public ApiResponse<Page<PostListResponseDto>> searchBlogPosts(@RequestParam("blogId") Long blogId,
                                                                 @RequestParam(value="keyword", required = false) String keyword,
                                                                 Pageable pageable) {
        Page<PostListResponseDto> pagePosts = postService.searchBlogPosts(blogId, keyword, pageable);

        return ApiResponse.ok(pagePosts);
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
}
