package com.logus.admin.repository;

import com.logus.admin.dto.ReportListResponseDto;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReportRepositoryCustom {
    ReportListResponseDto selectPostReports(Pageable pageable);
    ReportListResponseDto selectCommentReports(Pageable pageable);
}
