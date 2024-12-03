package com.logus.admin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logus.admin.entity.ReportStatus;
import com.logus.admin.entity.ReportType;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class ReportDto {

    private Long reportId;
    private Long reportCount;
    @JsonFormat(shape= JsonFormat.Shape.STRING, pattern="yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createDate;
    private ReportType reportType;
    private Long postId;
    private String postTitle;
    private Long commentId;
    private String commentContent;
    private ReportStatus reportStatus;
//    private Long pendingCount;
//    private Long handledCount;

}
