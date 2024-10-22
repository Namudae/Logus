package com.logus.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logus.admin.entity.ReportStatus;
import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Post;
import com.logus.blog.entity.Status;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class CommentResponseDto {

    private Long commentId;
    private Long memberId;
    private String nickname;
    private Long parentId;
    private Byte depth;
    private String content;
    private Status status;
    private ReportStatus reportStatus;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;

    /* Entity -> Dto */
    public CommentResponseDto(Comment comment) {
        this.commentId = comment.getId();
        this.memberId = comment.getMember().getId();
        this.nickname = comment.getMember().getNickname();
        this.parentId = (comment.getParent() != null) ? comment.getParent().getId() : null; //null처리 여기서?
        this.depth = comment.getDepth();
        this.content = comment.getContent();
        this.status = comment.getStatus();
        this.reportStatus = comment.getReportStatus();
        this.createDate = comment.getCreateDate();
    }

    //비밀글 처리
    public void secretComment() {
        this.memberId = null;
        this.nickname = null;
        this.content = null;
    }

}
