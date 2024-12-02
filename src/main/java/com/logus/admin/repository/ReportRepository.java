package com.logus.admin.repository;

import com.logus.admin.dto.ReportListResponseDto;
import com.logus.admin.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ReportRepository extends JpaRepository<Report, Long>, ReportRepositoryCustom {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Report r WHERE r.reporter.id = :memberId")
    void bulkDeleteReporterByMemberId(Long memberId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Report r WHERE r.reported.id = :memberId")
    void bulkDeleteReportedByMemberId(Long memberId);

    Long countByReporterIdAndCommentId(Long reporterMemberId, Long commentId);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.reporter.id = :reporterId AND r.post.id = :postId AND r.comment IS NULL")
    Long countByReporterIdAndPostId(Long reporterId, Long postId);
}
