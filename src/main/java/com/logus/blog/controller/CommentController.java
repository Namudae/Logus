package com.logus.blog.controller;

import com.logus.blog.dto.CommentRequestDto;
import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.service.CommentService;
import com.logus.common.controller.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 등록
     */
    @PostMapping("/{blogAddress}/{postId}/comment")
    public ApiResponse<Map<String, Long>> createPost(@PathVariable("blogAddress") String blogAddress,
                                        @PathVariable("postId") Long postId,
                                        @RequestBody CommentRequestDto commentRequestDto) {
        Long commentId = commentService.createComment(commentRequestDto);
        return ApiResponse.ok(Map.of("commentId", commentId));
    }

}
