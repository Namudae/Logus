package com.logus.admin.repository;

import com.logus.admin.dto.ReportListResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportRepositoryCustom {
    ReportListResponseDto selectReports(String reportKind, Pageable pageable);
}
