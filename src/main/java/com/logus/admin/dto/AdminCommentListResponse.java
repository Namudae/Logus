package com.logus.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class AdminCommentListResponse {

    private Long commentId;
    private String content;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    private Long postId;
    private String title;
    private Long memberId;
    private String loginId;
    private String nickname;
    private Long blogId;
    private String blogName;
    private String blogAddress;

}
