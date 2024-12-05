package com.logus.blog.controller;

import com.logus.blog.dto.*;
import com.logus.blog.entity.Post;
import com.logus.blog.service.BlogService;
import com.logus.blog.service.PostService;
import com.logus.common.controller.ApiResponse;
import com.logus.common.security.JwtService;
import com.logus.member.entity.Member;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
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

    private final BlogService blogService;
    private final PostService postService;

    /**
     * 블로그의 전체게시글 조회
     * + Pageable
     * http://localhost:8082/posts?blogId=1&size=10&page=0
     */
    @GetMapping("/posts")
    public ApiResponse<Page<PostListResponseDto>> selectAllBlogPosts(@RequestParam("blogId") Long blogId,
                                                                     @RequestParam(value = "seriesId", required = false) Long seriesId,
                                                                     Pageable pageable) {
        Page<PostListResponseDto> posts = postService.selectAllBlogPosts(blogId, seriesId, pageable);

        return ApiResponse.ok(posts);
    }

    /**
     * 블로그 내부 검색
     * http://localhost:8082/posts/blog-search?blogId=1&keyword=번째
     */
    @GetMapping("/posts/search")
    public ApiResponse<Page<PostListResponseDto>> searchBlogPosts(@RequestParam("blogId") Long blogId,
                                                                  @RequestParam(value="keyword", required = false) String keyword,
                                                                  @RequestParam(defaultValue = "ALL") String condition,
                                                                  Pageable pageable) {
        Page<PostListResponseDto> pagePosts = postService.searchBlogPosts(blogId, keyword, condition, pageable);

        return ApiResponse.ok(pagePosts);
    }

    /**
     * 블로그 내부 검색(블로그멤버용)
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') || @blogService.hasPermissionToBlog(#blogId, 'BLOG', 'EDITOR', authentication)")
    @GetMapping("/blog/posts")
    public ApiResponse<Page<PostListResponseDto>> searchBlogPostsByMember(@RequestParam("blogId") Long blogId,
                                                                  @RequestParam(value="keyword", required = false) String keyword,
                                                                  @RequestParam(defaultValue = "ALL") String condition,
                                                                  Pageable pageable) {
        Page<PostListResponseDto> pagePosts = postService.searchBlogPostsByMember(blogId, keyword, condition, pageable);

        return ApiResponse.ok(pagePosts);
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
     * 글 단건 조회
     */
    @GetMapping("/posts/{postId}")
    public ApiResponse<PostResponseDto> selectPost(@PathVariable("postId") Long postId) {

        return ApiResponse.ok(postService.selectPost(postId));
    }


    /**
     * 글 등록
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') || @blogService.hasPermissionToBlog(#postRequestDto.blogId, 'BLOG', 'EDITOR', authentication)")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') || @postService.hasPermissionToPost(#postId, authentication)")
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
    @PreAuthorize("hasRole('ROLE_ADMIN') || @postService.hasPermissionToPostMember(#postId, authentication)")
    @DeleteMapping("/posts/{postId}")
    public ApiResponse<String> deletePost(@PathVariable("postId") Long postId) throws MethodArgumentNotValidException {
        postService.deletePost(postId);
        return ApiResponse.ok();
    }

    /**
     * 임시저장 게시글 조회
     */
    @GetMapping("/posts/temp")
    public ApiResponse<TempPostResponseDto> selectTempPost(@RequestParam("blogId") Long blogId) {
        return ApiResponse.ok(postService.selectTempPost(blogId));
    }

    /**
     * 좋아요
     */
    @PostMapping("/like/{postId}")
    public ApiResponse<Map<String, Boolean>> createLike(@PathVariable("postId") Long postId) {
        boolean like = postService.createLike(postId);
        return ApiResponse.ok(Map.of("like", like));
    }

    /**
     * 좋아요 취소
     */
    @DeleteMapping("/like/{postId}")
    public ApiResponse<Map<String, Boolean>> deleteLike(@PathVariable("postId") Long postId) {
        boolean like = postService.deleteLike(postId);
        return ApiResponse.ok(Map.of("like", like));
    }

    /**
     * 메인 페이지 조회
     * - 트렌드, 최신
     */
    @GetMapping("/main")
    public ApiResponse<Page<MainGridResponse>> mainPosts(MainGridCondition condition,
                                                                Pageable pageable) {
        Page<MainGridResponse> posts = postService.selectMainPosts(condition, pageable);

        return ApiResponse.ok(posts);
    }

    /**
     * 메인 페이지 검색
     * - 반환타입 결정 (MainGridResponse or PostListResponseDto)
     * /main/search?keyword=테스트
     */
    @GetMapping("/main/search")
    public ApiResponse<Page<PostListResponseDto>> searchPostsMain(@RequestParam("keyword") String keyword,
                                                         Pageable pageable) {
        Page<PostListResponseDto> posts = postService.searchPostsMain(keyword, pageable);

        return ApiResponse.ok(posts);
    }


}
