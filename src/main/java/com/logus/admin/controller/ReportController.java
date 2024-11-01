package com.logus.admin.controller;

import com.logus.admin.dto.ReportListResponseDto;
import com.logus.admin.service.ReportService;
import com.logus.common.controller.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 신고 조회
     * + 페이징 추가
     */
    @GetMapping("/report")
    public ApiResponse<ReportListResponseDto> selectReports(@RequestParam(required = false) String reportKind, Pageable pageable) {
        ReportListResponseDto dto = reportService.selectReports(reportKind, pageable);

        return ApiResponse.ok(dto);
    }

}
