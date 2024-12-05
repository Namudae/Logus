package com.logus.blog.controller;

import com.logus.blog.dto.CommentListDto;
import com.logus.blog.dto.CommentRequestDto;
import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.service.BlogService;
import com.logus.blog.service.CommentService;
import com.logus.common.controller.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;
    private final BlogService blogService;

    /**
     * 댓글 등록
     */
    @PostMapping("/comments")
    public ApiResponse<Map<String, Long>> createPost(@RequestBody @Valid CommentRequestDto commentRequestDto) {
        Long commentId = commentService.createComment(commentRequestDto);
        return ApiResponse.ok(Map.of("commentId", commentId));
    }

    /**
     * 댓글 수정
     * - 본인만 가능
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') || @commentService.hasPermissionToMyComment(#commentId)")
    @PutMapping("/comments/{commentId}")
    public ApiResponse<Map<String, Long>> updateComment(@PathVariable("commentId") Long commentId,
                                                     @RequestBody @Valid CommentRequestDto commentRequestDto) {
        commentService.updateComment(commentId, commentRequestDto);
        return ApiResponse.ok(Map.of("commentId", commentId));
    }

    /**
     * 댓글 삭제
     * - 글 작성자 or 블로그 관리자(ADMIN)까지 가능
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') || @commentService.hasPermissionToCommentToPost(#commentId) || @blogService.hasPermissionToBlog(#commentId, 'COMMENT', 'ADMIN', authentication)")
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<String> deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ApiResponse.ok();
    }

    /**
     * 댓글 다중 삭제(여러건) > 폐기
     * - blogId로 권한 체크, 댓글이 blogId와 일치하는 건만 삭제
     */
//    @PreAuthorize("hasRole('ROLE_ADMIN') || @blogService.hasPermissionToBlog(#blogId, 'BLOG', 'ADMIN', authentication)")
//    @DeleteMapping("/blog/comments")
//    public ApiResponse<String> deleteComments(@RequestParam("blogId") Long blogId,
//                                              @RequestBody List<Long> commentIds) {
//        commentService.deleteComments(commentIds, blogId);
//        return ApiResponse.ok();
//    }

    /**
     * 내 블로그 댓글 전체조회
     * - 내용 검색
     * - 내 댓글만 / 내 댓글 제외
     */
    @PreAuthorize("hasRole('ROLE_ADMIN') || @blogService.hasPermissionToBlog(#blogId, 'BLOG', 'EDITOR', authentication)")
    @GetMapping("/blog/comments")
    public ApiResponse<Page<CommentListDto>> selectBlogComments(@RequestParam("blogId") Long blogId,
                                                               @RequestParam(value="keyword", required = false) String keyword,
                                                               @RequestParam(defaultValue = "ALL") String condition,
                                                               Pageable pageable) {
        Page<CommentListDto> commentListDto = commentService.selectBlogComments(blogId, keyword, condition, pageable);
        return ApiResponse.ok(commentListDto);
    }

}
