package com.logus.blog.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommentResponseDto {
    private List<ParentCommentDto> parents; // 부모 댓글 리스트
    private List<ChildCommentDto> childComments; // 자식 댓글 리스트
}