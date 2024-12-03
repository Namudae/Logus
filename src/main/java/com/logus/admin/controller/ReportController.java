package com.logus.admin.controller;

import com.logus.admin.dto.CategoryRequestDto;
import com.logus.admin.dto.ReportListResponseDto;
import com.logus.admin.dto.ReportRequest;
import com.logus.admin.service.ReportService;
import com.logus.common.controller.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 게시글 신고 조회
     */
    @GetMapping("/report/posts")
    public ApiResponse<ReportListResponseDto> selectPostReports(Pageable pageable) {
        ReportListResponseDto dto = reportService.selectPostReports(pageable);

        return ApiResponse.ok(dto);
    }

    /**
     * 댓글 신고 조회
     * + 페이징 추가
     */
    @GetMapping("/report/comments")
    public ApiResponse<ReportListResponseDto> selectCommentReports(Pageable pageable) {
        ReportListResponseDto dto = reportService.selectCommentReports(pageable);

        return ApiResponse.ok(dto);
    }

    /**
     * 신고 등록
     */
    @PostMapping("/report")
    public ApiResponse<Map<String, Long>> insertReport(@RequestBody @Valid ReportRequest reportRequest) {
        Long reportId = reportService.createReport(reportRequest);

        return ApiResponse.ok(Map.of("reportId", reportId));
    }

}
