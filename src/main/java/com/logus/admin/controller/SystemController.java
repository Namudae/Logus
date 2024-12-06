package com.logus.admin.controller;

import com.logus.admin.dto.AdminCommentListResponse;
import com.logus.admin.dto.BlogListResponseDto;
import com.logus.admin.dto.AdminPostListResponse;
import com.logus.blog.dto.BlogMemberResponseDto;
import com.logus.blog.service.BlogFacadeService;
import com.logus.blog.service.BlogService;
import com.logus.blog.service.CommentService;
import com.logus.blog.service.PostService;
import com.logus.common.controller.ApiResponse;
import com.logus.member.dto.MemberListResponse;
import com.logus.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class SystemController {

    private final MemberService memberService;
    private final BlogService blogService;
    private final PostService postService;
    private final CommentService commentService;
    private final BlogFacadeService blogFacadeService;

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

    /**
     * 게시글 관리 - 조회
     * 검색: 제목, 내용, 아이디, 닉네임, 블로그명, 블로그 주소
     */
    @GetMapping("/system/posts")
    public ApiResponse<Page<AdminPostListResponse>> searchPostsByAdmin(@RequestParam(value="keyword", required = false) String keyword,
                                                                       @RequestParam(defaultValue = "ALL") String condition,
                                                                       Pageable pageable) {
        Page<AdminPostListResponse> pagePosts = postService.searchPostsByAdmin(keyword, condition, pageable);

        return ApiResponse.ok(pagePosts);
    }

    /**
     * 댓글 관리 - 조회
     * 검색: 게시글 제목, 댓글 내용, 아이디, 닉네임, 블로그명, 블로그 주소
     */
    @GetMapping("/system/comments")
    public ApiResponse<Page<AdminCommentListResponse>> searchCommentsByAdmin(@RequestParam(value="keyword", required = false) String keyword,
                                                                             @RequestParam(defaultValue = "ALL") String condition,
                                                                             Pageable pageable) {
        Page<AdminCommentListResponse> pagePosts = postService.searchCommentsByAdmin(keyword, condition, pageable);

        return ApiResponse.ok(pagePosts);
    }

    /**
     * 댓글 다중 삭제(관리자)
     */
    @DeleteMapping("/system/comments")
    public ApiResponse<String> deleteCommentsForAdmin(@RequestBody List<Long> commentIds) {
        commentService.deleteCommentsForAdmin(commentIds);
        return ApiResponse.ok();
    }

    /**
     * 글 다중 삭제(관리자)
     */
    @DeleteMapping("/system/posts")
    public ApiResponse<String> deletePostsForAdmin(@RequestBody List<Long> postIds) {
        postService.deletePostsForAdmin(postIds);
        return ApiResponse.ok();
    }

    /**
     * 회원 다중 탈퇴
     */
    @DeleteMapping("/system/users")
    public ApiResponse<String> deleteMembersForAdmin(@RequestBody List<Long> memberIds) throws IOException {
        blogFacadeService.deleteMembersForAdmin(memberIds);
        return ApiResponse.ok();
    }


}
