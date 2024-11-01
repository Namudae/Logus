package com.logus.admin.dto;

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
    private LocalDateTime createDate;
    private ReportType reportType;
    private Long postId;
    private String postTitle;
    private Long commentId;
    private String commentContent;
    private ReportStatus reportStatus;

}
