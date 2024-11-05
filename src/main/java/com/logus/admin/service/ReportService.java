package com.logus.admin.service;

import com.logus.admin.dto.ReportListResponseDto;
import com.logus.admin.entity.Report;
import com.logus.admin.repository.ReportRepository;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final ReportRepository reportRepository;

    public Report getById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new CustomException(ErrorCode.REPORT_NOT_FOUND));
    }

    public Report getReferenceById(Long reportId) {
        return reportId == null ? null :
                reportRepository.getReferenceById(reportId);
    }

    public ReportListResponseDto selectReports(String reportKind, Pageable pageable) {
        return reportRepository.selectReports(reportKind, pageable);
    }

}
