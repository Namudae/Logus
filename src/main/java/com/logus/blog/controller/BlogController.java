package com.logus.blog.controller;

import com.logus.blog.dto.*;
import com.logus.blog.service.BlogFacadeService;
import com.logus.blog.service.BlogService;
import com.logus.common.controller.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
    public ApiResponse<String> updateSeriesOrder(@RequestPart("requestDto") @Valid SeriesOrderRequestDto seriesOrderRequestDto) throws IOException {
        blogFacadeService.updateSeriesOrder(seriesOrderRequestDto);
        return ApiResponse.ok();
    }

}