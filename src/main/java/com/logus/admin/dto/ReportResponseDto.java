package com.logus.admin.dto;

import com.logus.admin.entity.ReportStatus;
import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class ReportResponseDto {

    private Long reporterMemberId;
    private Long postId;
    private Long commentId;
    private Long reportedMemberId;
    private String reportType;
    private String reason;
    private ReportStatus reportStatus;
    private LocalDateTime createDate;
    private LocalDateTime updateDate;
}
