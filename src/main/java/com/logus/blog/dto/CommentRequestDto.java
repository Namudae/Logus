package com.logus.blog.dto;

import com.logus.admin.entity.ReportStatus;
import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Post;
import com.logus.blog.entity.Status;
import com.logus.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CommentRequestDto {

    private Long commentId;
    private Long memberId;
    private Long postId;
    private Long parentId;
    private Byte depth;

    @NotBlank(message = "댓글을 입력하세요.")
    @Size(max=500, message = "댓글의 최대 글자수는 500자입니다.")
    private String content;

    private Status status;

    /*
    Dto -> toEntity
     */
    public Comment toEntity(Member member, Post post, Comment parent) {
        return Comment.builder()
                .id(commentId)
                .member(member)
                .post(post)
                .parent(parent)
                .depth(depth)
                .content(content)
                .status(status)
                .build();
    }

}
