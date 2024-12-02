package com.logus.admin.repository;

import com.logus.admin.dto.CategoryResponseDto;
import com.logus.admin.dto.ReportDto;
import com.logus.admin.dto.ReportListResponseDto;
import com.logus.admin.dto.ReportResponseDto;
import com.logus.admin.entity.ReportStatus;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.logus.admin.entity.QCategory.category;
import static com.logus.admin.entity.QReport.report;

public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ReportRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public ReportListResponseDto selectReports(String reportKind, Pageable pageable) {
        List<ReportDto> reportResponses =  jpaQueryFactory
                .select(Projections.fields(ReportDto.class,
                        report.id.as("reportId"),
                        report.reporter.id.as("reporterMemberId"),
                        //reportCount(누적 신고수) 추가
                        report.createDate,
                        report.reportType,
                        report.post.id.as("postId"),
                        report.post.title.as("postTitle"),
                        report.comment.id.as("commentId"),
                        report.comment.content.as("commentContent"),
                        report.reportStatus
                        //미처리 개수 pendingCount
                        //처리 개수 handledCount
                ))
                .from(report)
                .leftJoin(report.post)
                .leftJoin(report.comment)
                .where(getReportCondition(reportKind))
                //순서 > 1.미처리, 2.번호순, 날짜순, 신고수(정렬기준 물어봐야됨)
                .orderBy(
                        report.createDate.desc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 계산
        Long totalCount = jpaQueryFactory
                .select(report.count())
                .from(report)
                .leftJoin(report.post)
                .leftJoin(report.comment)
                .where(getReportCondition(reportKind))
                .fetchOne();

        // Page 생성
        Page<ReportDto> reportPage = new PageImpl<>(reportResponses, pageable, totalCount);


        // 최종 DTO 생성
        return ReportListResponseDto.builder()
                .pendingCount(null)
                .handledCount(null)
                .reportList(reportPage)
                .build();

    }

    private BooleanExpression getReportCondition(String reportKind) {
        if ("post".equals(reportKind)) {
            return report.comment.id.isNull();
        } else if ("comment".equals(reportKind)) {
            return report.comment.id.isNotNull();
        }
        return null; // 또는 기본값을 반환할 수 있음
    }

}
