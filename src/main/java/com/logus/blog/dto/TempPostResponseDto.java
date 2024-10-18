package com.logus.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logus.admin.entity.ReportStatus;
import com.logus.blog.entity.Status;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class TempPostResponseDto {

    private Long postId;
    private Long memberId;
    private Long categoryId;
    private String categoryName;
    private Long seriesId;
    private Long seriesName;
    private String title;
    private String content;
    private Status status;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    private List<String> tags;

}
