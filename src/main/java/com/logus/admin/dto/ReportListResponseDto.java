package com.logus.admin.dto;

import lombok.*;
import org.springframework.data.domain.Page;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class ReportListResponseDto {

    private Page<ReportListDto> reportList;
    //미처리, 처리 신고 개수
    private Long pendingCount;
    private Long handledCount;


}
