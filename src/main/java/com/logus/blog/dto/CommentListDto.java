package com.logus.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logus.admin.entity.ReportStatus;
import com.logus.blog.entity.Status;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Setter
@Data
@Builder
public class CommentListDto {

    private Long commentId;
    private Byte depth;
    private Long memberId;
    private String nickname;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    private ReportStatus reportStatus;
    private Long postId;
    private String title;

    public void blindComment() {
        this.content = "임시 숨김 처리된 댓글입니다.";
    }

    public void blockComment() {
        this.content = "차단된 댓글입니다.";
    }
}
