package com.logus.admin.controller;

import com.logus.admin.dto.CategoryRequestDto;
import com.logus.admin.dto.ReportDto;
import com.logus.admin.dto.ReportListResponseDto;
import com.logus.admin.dto.ReportRequest;
import com.logus.admin.entity.ReportStatus;
import com.logus.admin.service.ReportService;
import com.logus.common.controller.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * 게시글 신고 목록 조회
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/report/posts")
    public ApiResponse<ReportListResponseDto> selectPostReports(Pageable pageable) {
        ReportListResponseDto dto = reportService.selectPostReports(pageable);

        return ApiResponse.ok(dto);
    }

    /**
     * 댓글 신고 목록 조회
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/report/comments")
    public ApiResponse<ReportListResponseDto> selectCommentReports(Pageable pageable) {
        ReportListResponseDto dto = reportService.selectCommentReports(pageable);

        return ApiResponse.ok(dto);
    }

    /**
     * 게시글 신고 단건 조회
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/report/post")
    public ApiResponse<ReportDto> selectPostReport(@RequestParam("reportId") Long reportId) {
        ReportDto dto = reportService.selectReportPost(reportId);

        return ApiResponse.ok(dto);
    }


    /**
     * 댓글 신고 단건 조회
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/report/comment")
    public ApiResponse<ReportDto> selectCommentReport(@RequestParam("reportId") Long reportId) {
        ReportDto dto = reportService.selectReportComment(reportId);

        return ApiResponse.ok(dto);
    }

    /**
     * 신고 등록
     */
    @PostMapping("/report/post")
    public ApiResponse<Map<String, Long>> insertReportPost(@RequestBody @Valid ReportRequest reportRequest) {
        Long reportId = reportService.createReportPost(reportRequest);

        return ApiResponse.ok(Map.of("reportId", reportId));
    }

    /**
     * 신고 등록
     */
    @PostMapping("/report/comment")
    public ApiResponse<Map<String, Long>> insertReportComment(@RequestBody @Valid ReportRequest reportRequest) {
        Long reportId = reportService.createReportComment(reportRequest);

        return ApiResponse.ok(Map.of("reportId", reportId));
    }

    /**
     * 게시글 신고 처리
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/report/post")
    public ApiResponse<Map<String, Long>> handlePostReport(@RequestParam("reportId") Long reportId,
                                                       @RequestParam(value = "reportStatus") ReportStatus reportStatus) {
        reportService.handlePostReport(reportId, reportStatus);
        return ApiResponse.ok(Map.of("reportId", reportId));
    }

    /**
     * 댓글 신고 처리
     */
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @PutMapping("/report/comment")
    public ApiResponse<Map<String, Long>> handleCommentReport(@RequestParam("reportId") Long reportId,
                                                       @RequestParam(value = "reportStatus") ReportStatus reportStatus) {
        reportService.handleCommentReport(reportId, reportStatus);
        return ApiResponse.ok(Map.of("reportId", reportId));
    }

}
