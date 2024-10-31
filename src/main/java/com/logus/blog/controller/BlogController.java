package com.logus.blog.controller;

import com.logus.blog.dto.*;
import com.logus.blog.service.BlogFacadeService;
import com.logus.blog.service.BlogService;
import com.logus.common.controller.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class BlogController {

    private final BlogService blogService;
    private final BlogFacadeService blogFacadeService;

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
    @PostMapping("/blog/setting")
    public ApiResponse<Map<String, Long>> createBlog(@RequestBody @Valid BlogRequestDto blogRequestDto) {
        Long blogId = blogService.createBlog(blogRequestDto);
        return ApiResponse.ok(Map.of("blogId", blogId));
    }

    /**
     * 블로그 정보 변경
     */
    @PutMapping("/blog/setting")
    public ApiResponse<Map<String, Long>> updateBlog(@RequestParam("blogId") Long blogId,
                                                     @RequestBody @Valid BlogRequestDto blogRequestDto) {
        blogService.updateBlog(blogId, blogRequestDto);
        return ApiResponse.ok(Map.of("blogId", blogId));
    }

    /**
     * 블로그 삭제
     */
//    @PreAuthorize("hasRole('ROLE_ADMIN') || @blogService.hasPermissionToBlogOwner(#blogId, authentication)")
    @PreAuthorize("hasRole('ROLE_ADMIN') || @blogService.hasPermissionToBlog(#blogId, 'BLOG', 'OWNER', authentication)")
    @DeleteMapping("/blog/setting")
    public ApiResponse<Map<String, Long>> deleteBlog(@RequestParam("blogId") Long blogId) {
        blogFacadeService.deleteBlog(blogId);
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
     * 블로그 id 조회
     */
    @GetMapping("/blog-id")
    public ApiResponse<Map<String, Long>> getBlogIdByAddress(@RequestParam("blogAddress") String blogAddress) {
        Long blogId = blogService.getBlogIdByAddress(blogAddress);

        return ApiResponse.ok(Map.of("blogId", blogId));
    }

    /**
     * 블로그 권한 조회
     */
    @GetMapping("/blog/auth")
    public ApiResponse<List<BlogMemberResponseDto>> selectBlogAuth(@RequestParam("blogId") Long blogId) {
        List<BlogMemberResponseDto> members = blogService.selectBlogAuth(blogId);

        return ApiResponse.ok(members);
    }

    /**
     * 블로그 권한 수정
     */
    @PutMapping("/blog/auth")
    public ApiResponse<String> updateBlogAuth(@RequestParam("blogId") Long blogId,
                                              @RequestBody List<BlogMemberRequestDto> blogMemberRequestDtos) {
        blogService.updateBlogAuth(blogId, blogMemberRequestDtos);

        return ApiResponse.ok();
    }

    /**
     * 내 블로그 목록 조회(our-log)
     */
    @GetMapping("/blog/our-log")
    public ApiResponse<List<OurLogResponseDto>> selectOurLog() {
        List<OurLogResponseDto> blogs = blogFacadeService.selectOurLog();
        return ApiResponse.ok(blogs);
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
     * 시리즈 등록
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') || @blogService.hasPermissionToBlog(#seriesRequestDto.blogId, 'BLOG', 'ADMIN', authentication)")
    @PostMapping("/series")
    public ApiResponse<Map<String, Long>> createSeries(@RequestPart("requestDto") @Valid SeriesRequestDto seriesRequestDto,
                                                       @RequestPart(value = "img", required = false) MultipartFile img) throws IOException {
        Long seriesId = blogFacadeService.createSeries(seriesRequestDto, img);
        return ApiResponse.ok(Map.of("seriesId", seriesId));
    }

    /**
     * 시리즈 수정
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') || @blogService.hasPermissionToBlog(#seriesId, 'SERIES', 'ADMIN', authentication)")
    @PutMapping("/series/{seriesId}")
    public ApiResponse<Map<String, Long>> updateSeries(@PathVariable("seriesId") Long seriesId,
                                                       @RequestPart("requestDto") @Valid SeriesRequestDto seriesRequestDto,
                                                       @RequestPart(value = "img", required = false) MultipartFile img,
                                                       @RequestParam(value = "deleteImg", required = false, defaultValue = "false") Boolean deleteImg) throws IOException {
        blogFacadeService.updateSeries(seriesId, seriesRequestDto, img, deleteImg);
        return ApiResponse.ok(Map.of("seriesId", seriesId));
    }

    /**
     * 시리즈 삭제
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') || @blogService.hasPermissionToBlog(#seriesId, 'SERIES', 'ADMIN', authentication)")
    @DeleteMapping("/series/{seriesId}")
    public ApiResponse<String> deleteSeries(@PathVariable("seriesId") Long seriesId) throws IOException {
        blogFacadeService.deleteSeries(seriesId);
        return ApiResponse.ok();
    }

    /**
     * 시리즈 순서 일괄 수정
     */
    @PostMapping("/series/order")
    public ApiResponse<String> updateSeriesOrder(@RequestBody @Valid SeriesOrderRequestDto seriesOrderRequestDto) throws IOException {
        blogFacadeService.updateSeriesOrder(seriesOrderRequestDto);
        return ApiResponse.ok();
    }

    /**
     * 구독 블로그 조회
     */
    @GetMapping("/follow")
    public ApiResponse<Page<FollowResponseDto>> selectFollow(Pageable pageable) {
        Page<FollowResponseDto> follows = blogFacadeService.selectFollow(pageable);

        return ApiResponse.ok(follows);
    }

    /**
     * 구독 등록
     */
    @PostMapping("/follow")
    public ApiResponse<Map<String, Long>> createFollow(@RequestParam("blogId") Long blogId) {
        blogFacadeService.createFollow(blogId);

        return ApiResponse.ok(Map.of("blogId", blogId));
    }

    /**
     * 구독 취소
     */
    @DeleteMapping("/follow")
    public ApiResponse<String> deleteFollow(@RequestParam("followId") Long followId) {
        blogFacadeService.deleteFollow(followId);

        return ApiResponse.ok();
    }

    /**
     * 구독자 조회
     */
    @GetMapping("/follower")
    public ApiResponse<Page<FollowerResponseDto>> selectFollower(@RequestParam("blogId") Long blogId,
                                                                 Pageable pageable) {
        Page<FollowerResponseDto> followers = blogFacadeService.selectFollower(blogId, pageable);

        return ApiResponse.ok(followers);
    }

    /**
     * 구독자 삭제
     */
    @DeleteMapping("/follower")
    public ApiResponse<String> deleteFollower(@RequestParam("followId") Long followId) {
        blogFacadeService.deleteFollower(followId);
        return ApiResponse.ok();
    }

}