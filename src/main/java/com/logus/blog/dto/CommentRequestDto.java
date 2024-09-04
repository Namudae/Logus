package com.logus.blog.dto;

import com.logus.admin.entity.ReportStatus;
import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Post;
import com.logus.blog.entity.Status;
import com.logus.member.entity.Member;
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
    private Comment parent;
    private Byte depth;
    private String content;
    private Status status;

    /*
    Dto -> toEntity
     */
    public Comment toEntity(Member member, Post post) {
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
