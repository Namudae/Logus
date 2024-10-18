package com.logus.blog.controller;

import com.logus.blog.dto.CommentRequestDto;
import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.service.CommentService;
import com.logus.common.controller.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 등록
     */
    @PostMapping("/comments")
    public ApiResponse<Map<String, Long>> createPost(@RequestPart("requestDto") @Valid CommentRequestDto commentRequestDto) {
        Long commentId = commentService.createComment(commentRequestDto);
        return ApiResponse.ok(Map.of("commentId", commentId));
    }

    /**
     * 댓글 수정
     */
    @PreAuthorize("@commentService.hasPermissionToComment(#commentId, authentication)")
    @PutMapping("/comments/{commentId}")
    public ApiResponse<Map<String, Long>> updateComment(@PathVariable("commentId") Long commentId,
                                                     @RequestPart("requestDto") @Valid CommentRequestDto commentRequestDto) {
        commentService.updateComment(commentId, commentRequestDto);
        return ApiResponse.ok(Map.of("commentId", commentId));
    }

    /**
     * 댓글 삭제
     */
    @PreAuthorize("@commentService.hasPermissionToComment(#commentId, authentication)")
    @DeleteMapping("/comments/{commentId}")
    public ApiResponse<String> deleteComment(@PathVariable("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ApiResponse.ok();
    }

}
