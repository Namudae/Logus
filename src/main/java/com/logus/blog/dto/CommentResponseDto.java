package com.logus.blog.dto;

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

//    public CommentResponseDto(LocalDateTime createDate, ReportStatus reportStatus, Status status, String content, Byte depth, Long parentId, String nickname, Long memberId, Long commentId) {
//        this.createDate = createDate;
//        this.reportStatus = reportStatus;
//        this.status = status;
//        this.content = content;
//        this.depth = depth;
//        this.parentId = parentId;
//        this.nickname = nickname;
//        this.memberId = memberId;
//        this.commentId = commentId;
//    }

}
