package com.logus.admin.repository;

import com.logus.admin.entity.Report;
import com.logus.admin.entity.ReportStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

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

    Integer countByCommentId(Long commentId);

    @Query("SELECT COUNT(r) FROM Report r WHERE r.post.id = :postId AND r.comment IS NULL")
    Integer countByPostId(Long postId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Report r SET r.reportStatus = :reportStatus WHERE r.post.id = :postId AND r.comment IS NULL")
    void bulkUpdateReportByPostId(Long postId, ReportStatus reportStatus);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("UPDATE Report r SET r.reportStatus = :reportStatus WHERE r.comment.id = :commentId")
    void bulkUpdateReportByCommentId(Long commentId, ReportStatus reportStatus);

}
