package com.logus.blog.repository;

import com.logus.blog.dto.OurLogResponseDto;
import com.logus.blog.dto.VisitBlogDto;
import com.logus.blog.entity.BlogAuth;
import com.logus.blog.entity.QVisit;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.logus.blog.entity.QBlog.blog;
import static com.logus.blog.entity.QBlogMember.blogMember;
import static com.logus.blog.entity.QVisit.visit;

public class VisitRepositoryImpl implements VisitRepositoryCustom {
    private final JPAQueryFactory jpaQueryFactory;

    public VisitRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public List<VisitBlogDto.BlogVisitorDto> selectBlogMonthVisit(Long blogId) {
        // 1. 한 달간의 날짜 리스트 생성
        List<LocalDate> datesOfLastMonth = getDatesOfLastMonth();

        // 2. Querydsl로 날짜별 방문 횟수 조회
        QVisit visit = QVisit.visit;
        List<Tuple> results = jpaQueryFactory
                .select(visit.createDate, visit.id.count())
                .from(visit)
                .where(visit.blog.id.eq(blogId)
                        .and(visit.createDate.between(
                                datesOfLastMonth.get(0), datesOfLastMonth.get(datesOfLastMonth.size() - 1)
                        )))
                .groupBy(visit.createDate)
                .orderBy(visit.createDate.asc())
                .fetch();

        // 3. 결과 매핑 및 날짜 보강
        Map<LocalDate, Long> resultMap = results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(visit.createDate),
                        tuple -> tuple.get(visit.id.count())
                ));

        // 4. 누락된 날짜를 0으로 채우기
        return datesOfLastMonth.stream()
                .map(date -> VisitBlogDto.BlogVisitorDto.builder()
                        .date(date)
                        .visitCount(resultMap.getOrDefault(date, 0L).intValue())
                        .build())
                .collect(Collectors.toList());

    }

    public List<LocalDate> getDatesOfLastMonth() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(30); // 31일 전부터 시작
        return startDate.datesUntil(today.plusDays(1)) // 오늘 포함
                .collect(Collectors.toList());
    }

    @Override
    public VisitBlogDto selectTodayYesterdayTotal(Long blogId) {
        // 오늘과 어제의 날짜를 구함
        LocalDate todayDate = LocalDate.now();
        LocalDate yesterdayDate = todayDate.minusDays(1);

        Long today = jpaQueryFactory
                .select(visit.count())
                .from(visit)
                .leftJoin(visit.blog, blog)
                .where(blog.id.eq(blogId)
                        .and(visit.createDate.eq(todayDate)))
                .fetchOne();

        Long yesterday = jpaQueryFactory
                .select(visit.count())
                .from(visit)
                .leftJoin(visit.blog, blog)
                .where(blog.id.eq(blogId)
                        .and(visit.createDate.eq(yesterdayDate)))
                .fetchOne();

        Long total = jpaQueryFactory
                .select(visit.count())
                .from(visit)
                .leftJoin(visit.blog, blog)
                .where(blog.id.eq(blogId))
                .fetchOne();

        return VisitBlogDto.builder()
                .today(today)
                .yesterday(yesterday)
                .total(total)
                .build();
    }
}
