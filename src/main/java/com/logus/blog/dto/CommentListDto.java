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
    private Long memberId;
    private String nickname;
    private String content;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    private String title;

}
