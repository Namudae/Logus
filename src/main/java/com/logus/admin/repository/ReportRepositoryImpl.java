package com.logus.admin.repository;

import com.logus.admin.dto.CategoryResponseDto;
import com.logus.admin.dto.ReportDto;
import com.logus.admin.dto.ReportListResponseDto;
import com.logus.admin.dto.ReportResponseDto;
import com.logus.admin.entity.QReport;
import com.logus.admin.entity.ReportStatus;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.logus.admin.entity.QCategory.category;
import static com.logus.admin.entity.QReport.report;
import static com.logus.blog.entity.QComment.comment;
import static com.logus.blog.entity.QPost.post;

public class ReportRepositoryImpl implements ReportRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public ReportRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public ReportListResponseDto selectPostReports(Pageable pageable) {
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
                ))
                .from(report)
                .leftJoin(report.post)
                .leftJoin(report.comment)
                .where(report.comment.isNull())
                //순서 > 1.미처리, 2.번호순, 날짜순, 신고수(정렬기준 물어봐야됨)
                .orderBy(
                        // String 컬럼을 사용하여 사용자 정의 순서대로 정렬
                        getReportStatusOrder(report).asc(),
                        report.createDate.asc()
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // 총 개수 계산
        Long totalCount = jpaQueryFactory
                .select(report.count())
                .from(report)
                .where(report.comment.isNull())
                .leftJoin(report.post)
                .leftJoin(report.comment)
                .fetchOne();

        Long pendingCount = jpaQueryFactory
                .select(report.count())
                .from(report)
                .where(report.comment.isNull(),
                        wherePending(report))
                .fetchOne();

        Long handledCount = jpaQueryFactory
                .select(report.count())
                .from(report)
                .where(report.comment.isNull(),
                        whereHandling(report))
                .fetchOne();

        // Page 생성
        Page<ReportDto> reportPage = new PageImpl<>(reportResponses, pageable, totalCount);


        // 최종 DTO 생성
        return ReportListResponseDto.builder()
                .pendingCount(pendingCount)
                .handledCount(handledCount)
                .reportList(reportPage)
                .build();

    }

    @Override
    public ReportListResponseDto selectCommentReports(Pageable pageable) {
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
                ))
                .from(report)
                .leftJoin(report.post)
                .leftJoin(report.comment)
                .where(report.comment.isNotNull())
                //순서 > 1.미처리, 2.번호순, 날짜순, 신고수(정렬기준 물어봐야됨)
                .orderBy(
                        getReportStatusOrder(report).asc(),
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
                .where(report.comment.isNotNull())
                .fetchOne();

        Long pendingCount = jpaQueryFactory
                .select(report.count())
                .from(report)
                .where(report.comment.isNotNull(),
                        wherePending(report))
                .fetchOne();

        Long handledCount = jpaQueryFactory
                .select(report.count())
                .from(report)
                .where(report.comment.isNotNull(),
                        whereHandling(report))
                .fetchOne();


        // Page 생성
        Page<ReportDto> reportPage = new PageImpl<>(reportResponses, pageable, totalCount);


        // 최종 DTO 생성
        return ReportListResponseDto.builder()
                .pendingCount(pendingCount)
                .handledCount(handledCount)
                .reportList(reportPage)
                .build();

    }

    public static BooleanExpression wherePending(QReport report) {
        return report.reportStatus.eq(ReportStatus.PENDING)
                .or(report.reportStatus.eq(ReportStatus.BLIND));
    }

    public static BooleanExpression whereHandling(QReport report) {
        return report.reportStatus.eq(ReportStatus.BLOCK)
                .or(report.reportStatus.eq(ReportStatus.DELETE))
                        .or(report.reportStatus.eq(ReportStatus.RETURN));
    }

    public static NumberExpression<Integer> getReportStatusOrder(QReport report) {
        return new CaseBuilder()
                .when(report.reportStatus.eq(ReportStatus.PENDING)).then(1)
                .when(report.reportStatus.eq(ReportStatus.BLIND)).then(2)
                .when(report.reportStatus.eq(ReportStatus.BLOCK)).then(3)
                .when(report.reportStatus.eq(ReportStatus.DELETE)).then(4)
                .when(report.reportStatus.eq(ReportStatus.RETURN)).then(5)
                .otherwise(6); // 기본값
    }

}
