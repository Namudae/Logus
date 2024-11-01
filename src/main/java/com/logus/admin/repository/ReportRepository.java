package com.logus.admin.repository;

import com.logus.admin.dto.ReportListResponseDto;
import com.logus.admin.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {
}
