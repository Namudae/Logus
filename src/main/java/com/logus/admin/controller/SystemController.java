package com.logus.admin.controller;

import com.logus.admin.dto.BlogListResponseDto;
import com.logus.blog.dto.BlogMemberResponseDto;
import com.logus.blog.dto.BlogResponseDto;
import com.logus.blog.service.BlogService;
import com.logus.common.controller.ApiResponse;
import com.logus.member.dto.MemberListResponse;
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SystemController {

    private final MemberService memberService;
    private final BlogService blogService;

    /**
     * 회원 정보 검색
     */
    @GetMapping("/system/user")
    public ApiResponse<Page<MemberListResponse>> searchMembers(@RequestParam(value = "loginId", required = false) String loginId,
                                                               @RequestParam(value = "nickname", required = false) String nickname,
                                                               @RequestParam(value = "blogName", required = false) String blogName,
                                                               @RequestParam(value = "blogAddress", required = false) String blogAddress,
                                                               Pageable pageable) {
        Page<MemberListResponse> memberLists = memberService.searchMembers(loginId, nickname, blogName, blogAddress, pageable);

        return ApiResponse.ok(memberLists);
    }

    /**
     * 블로그 정보 검색
     */
    @GetMapping("/system/blog")
    public ApiResponse<Page<BlogListResponseDto>> searchBlogs(@RequestParam(value = "loginId", required = false) String loginId,
                                                              @RequestParam(value = "nickname", required = false) String nickname,
                                                              @RequestParam(value = "blogName", required = false) String blogName,
                                                              @RequestParam(value = "blogAddress", required = false) String blogAddress,
                                                              Pageable pageable) {
        Page<BlogListResponseDto> blogLists = blogService.searchBlogs(loginId, nickname, blogName, blogAddress, pageable);

        return ApiResponse.ok(blogLists);
    }

    /**
     * 블로그 정보 검색 - 참여멤버
     */
    @GetMapping("/system/blog/blogmembers")
    public ApiResponse<List<BlogMemberResponseDto>> searchBlogMembers(@RequestParam("blogId") Long blogId) {
        List<BlogMemberResponseDto> blogLists = blogService.selectBlogAuth(blogId);

        return ApiResponse.ok(blogLists);
    }

}
