package com.logus.admin.dto;

import com.logus.admin.entity.ReportStatus;
import com.logus.admin.entity.ReportType;
import lombok.*;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class ReportListResponseDto {

    private Page<ReportDto> reportList;
    //미처리, 처리 신고 개수
    private Long pendingCount;
    private Long handledCount;


}
