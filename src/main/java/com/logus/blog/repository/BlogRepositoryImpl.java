package com.logus.blog.repository;

import com.logus.admin.dto.BlogListResponseDto;
import com.logus.blog.dto.*;
import com.logus.blog.entity.*;
import com.logus.member.dto.MemberListResponse;
import com.logus.member.entity.Member;
import com.logus.member.entity.QMember;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTemplate;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.JPQLQuery;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.logus.blog.entity.QBlog.blog;
import static com.logus.blog.entity.QBlogMember.blogMember;
import static com.logus.admin.entity.QCategory.category;
import static com.logus.blog.entity.QComment.comment;
import static com.logus.blog.entity.QFollow.follow;
import static com.logus.blog.entity.QLikey.likey;
import static com.logus.blog.entity.QPost.post;
import static com.logus.blog.entity.QSeries.series;
import static com.logus.blog.entity.QVisit.visit;
import static com.logus.member.entity.QMember.member;
import static org.springframework.util.StringUtils.hasText;

public class BlogRepositoryImpl implements BlogRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public BlogRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    /**
     * 블로그 정보 조회
     */
    @Override
    public BlogResponseDto selectBlogInfo(String blogAddress) {
        return jpaQueryFactory
                .select(Projections.fields(BlogResponseDto.class,
                        blog.blogName,
                        blog.blogAddress,
                        blog.introduce,
                        blog.shareYn,
                        member.id.as("memberId"),
                        member.nickname,
                        blogMember.blogAuth))
                .from(blog)
//                .join(blog.blogMember, member)
//                .leftJoin(post.category, category)
//                .leftJoin(post.series, series)
//                .where(post.id.eq(postId))
                .fetchOne();
    }

    /**
     * My-Log 검색
     */
    @Override
    public String findMyLogAddress(Long memberId) {
        return jpaQueryFactory
                .select(blog.blogAddress)
                .from(blog)
                .leftJoin(blog.blogMembers, blogMember)
                .where(
                        blogMember.member.id.eq(memberId),
                        blog.shareYn.eq("N")
                )
                .orderBy(blog.createDate.asc())
                .fetchFirst();
    }


    /**
     * My-Log 검색
     */
    @Override
    public Long findMyLogId(Long memberId) {
        return jpaQueryFactory
                .select(blog.id)
                .from(blog)
                .leftJoin(blog.blogMembers, blogMember)
                .where(
                        blogMember.member.id.eq(memberId),
                        blog.shareYn.eq("N")
                )
                .orderBy(blog.createDate.asc())
                .fetchFirst();
    }

    /**
     * Our-Log 조회
     */
    @Override
    public List<OurLogResponseDto> findByMemberId(Long memberId) {
        QBlogMember ownerBlogMember = new QBlogMember("ownerBlogMember");

        return jpaQueryFactory
                .select(Projections.fields(OurLogResponseDto.class,
                        blog.id.as("blogId"),
                        blog.blogName,
                        blog.blogAddress,
                        blog.introduce,
                        blog.shareYn,
                        ownerBlogMember.member.id.as("memberId"),
                        ownerBlogMember.member.nickname.as("nickname"),
                        ownerBlogMember.member.imgUrl.as("imgUrl")
                ))
                .from(blogMember)
                .join(blogMember.blog, blog)
                .join(ownerBlogMember).on(ownerBlogMember.blog.eq(blog)
                        .and(ownerBlogMember.blogAuth.eq(BlogAuth.OWNER)))
                .where(blogMember.member.id.eq(memberId))
                .fetch();
    }

    /**
     * 팔로우 조회
     */
    @Override
    public Page<FollowResponseDto> selectFollows(Long memberId, Pageable pageable) {

        JPAQuery<FollowResponseDto> query = jpaQueryFactory
                .select(Projections.fields(FollowResponseDto.class,
                        follow.id.as("followId"),
                        follow.blog.id.as("blogId"),
                        follow.blog.blogName,
                        follow.blog.blogAddress,
                        follow.blog.introduce))
                .from(follow)
                .leftJoin(follow.blog, blog)
                .where(
                        follow.member.id.eq(memberId)
                )
                .orderBy(follow.createDate.desc()); //최근 팔로우부터

        // 총 결과 수 조회
        long total = query.fetchCount();

        // 페이지에 맞는 결과 조회
        List<FollowResponseDto> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Page 객체 생성 및 반환
        return new PageImpl<>(results, pageable, total);
    }

    /**
     * 팔로워 조회
     */
    @Override
    public Page<FollowerResponseDto> selectFollowers(Long blogId, Pageable pageable) {
        List<FollowerResponseDto> followerList = jpaQueryFactory
                .select(Projections.fields(FollowerResponseDto.class,
                        follow.id.as("followId"),
                        follow.member.id.as("memberId"),
                        follow.member.nickname,
                        follow.member.imgUrl))
                .from(follow)
                .leftJoin(follow.member, member)
                .leftJoin(follow.blog, blog)
                .where(
                        follow.blog.id.eq(blogId)
                )
                .orderBy(follow.createDate.asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(); //최근 팔로우부터

        // 팔로워의 블로그 목록 조회
        for (FollowerResponseDto follower : followerList) {
            List<FollowerResponseDto.BlogDto> blogDtos = jpaQueryFactory
                    .select(Projections.fields(FollowerResponseDto.BlogDto.class,
                            blog.id.as("blogId"),
                            blog.blogName,
                            blog.blogAddress))
                    .from(blogMember)
                    .leftJoin(blogMember.blog, blog)
                    .leftJoin(blogMember.member, member)
                    .where(blogMember.member.id.eq(follower.getMemberId())) // 블로그 주인 ID로 필터링
                    .fetch();
            follower.setBlogList(blogDtos); // 블로그 목록 추가
        }

        long total = Optional.ofNullable(
                jpaQueryFactory
                        .select(follow.count())
                        .from(follow)
                        .where(follow.blog.id.eq(blogId))
                        .fetchOne()
        ).orElse(0L);

        // Page 객체 생성 및 반환
        return new PageImpl<>(followerList, pageable, total);
    }

    @Override
    public Page<BlogListResponseDto> searchBlogs(String loginId, String nickname, String blogName, String blogAddress, Pageable pageable) {
        QMember ownerMember = new QMember("ownerMember"); // OWNER를 별도로 매핑

        // Content 쿼리
        List<BlogListResponseDto> content = jpaQueryFactory
                .select(Projections.fields(BlogListResponseDto.class,
                        blog.id.as("blogId"),
                        blog.blogName,
                        blog.blogAddress,
                        ownerMember.loginId.as("loginId") // OWNER의 loginId 매핑)
                ))
                .from(blog)
                .leftJoin(blog.blogMembers, blogMember)
                .leftJoin(blogMember.member, member)
                .leftJoin(blogMember.member, ownerMember) // OWNER의 Member와 조인
                .where(
                        blogMember.blogAuth.eq(BlogAuth.OWNER),
                        containLoginId(loginId),
                        containNickname(nickname),
                        containBlogName(blogName),
                        containBlogAddress(blogAddress)
                )
                .orderBy(blog.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Total count 쿼리
        long total = jpaQueryFactory
                .select(blog.count())
                .from(blog)
                .leftJoin(blog.blogMembers, blogMember)
                .leftJoin(blogMember.member, member)
                .leftJoin(blogMember.member, ownerMember) // OWNER의 Member와 조인
                .where(
                        blogMember.blogAuth.eq(BlogAuth.OWNER),
                        containLoginId(loginId),
                        containNickname(nickname),
                        containBlogName(blogName),
                        containBlogAddress(blogAddress)
                )
                .fetchOne();

        // Page 반환
        return new PageImpl<>(content, pageable, total);
    }

    @Override
    public List<Blog> ownedBlogs(Member member) {
        return jpaQueryFactory
                .select(blog)
                .from(blog)
                .leftJoin(blog.blogMembers, blogMember)
                .where(
                        blogMember.member.eq(member),
                        blogMember.blogAuth.eq(BlogAuth.OWNER)
                )
                .fetch();
    }

    private BooleanExpression containLoginId(String loginId) {
        return hasText(loginId) ? member.loginId.contains(loginId) : null;
    }

    private BooleanExpression containNickname(String nickname) {
        return hasText(nickname) ? member.nickname.contains(nickname) : null;
    }

    private BooleanExpression containBlogName(String blogName) {
        return hasText(blogName) ? blogMember.blog.blogName.contains(blogName) : null;
    }

    private BooleanExpression containBlogAddress(String blogAddress) {
        return hasText(blogAddress) ? blogMember.blog.blogAddress.contains(blogAddress) : null;
    }

    @Override
    public List<StatisticsMemberPostDto.MemberPostDto> blogPostStatistics(Long blogId, Long memberId) {
        // 한 달간의 날짜 리스트 생성
        List<LocalDate> datesOfLastMonth = getDatesOfLastMonth();
        DateTemplate<java.sql.Date> groupByDate = Expressions.dateTemplate(java.sql.Date.class, "DATE({0})", post.createDate);

        // Querydsl로 날짜별 포스트 회수 조회
        List<Tuple> results = jpaQueryFactory
                .select(groupByDate, post.count())
                .from(post)
                .leftJoin(post.blog, blog)
                .leftJoin(post.member, member)
                .where(post.blog.id.eq(blogId)
                        .and(post.member.id.eq(memberId))
                        .and(post.status.ne(Status.TEMPORARY))
                        .and(post.createDate.between(
                                datesOfLastMonth.get(0).atStartOfDay(),
                                datesOfLastMonth.get(datesOfLastMonth.size() - 1).atTime(23, 59, 59)
                        )))
                .groupBy(groupByDate) // 날짜만 그룹화
                .orderBy(groupByDate.asc())
                .fetch();

        // 3. 결과 매핑 및 날짜 보강
        Map<LocalDate, Long> resultMap = results.stream()
                .collect(Collectors.toMap(
                        tuple -> tuple.get(groupByDate).toLocalDate(),
                        tuple -> tuple.get(post.count())
                ));

        // 4. 누락된 날짜를 0으로 채우기
        return datesOfLastMonth.stream()
                .map(date -> {
                    Long count = resultMap.getOrDefault(date, 0L); // 기본값 처리
                    return StatisticsMemberPostDto.MemberPostDto.builder()
                            .date(date)
                            .postCount(count != null ? count.intValue() : 0) // null 체크 추가
                            .build();
                })
                .collect(Collectors.toList());
    }

    public List<LocalDate> getDatesOfLastMonth() {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(30); // 31일 전부터 시작
        return startDate.datesUntil(today.plusDays(1)) // 오늘 포함
                .collect(Collectors.toList());
    }

    @Override
    public StatisticsMemberPostDto blogPostStatisticsTodayTotal(Long blogId, Long memberId) {
        LocalDate todayDate = LocalDate.now();

        Long today = jpaQueryFactory
                .select(post.count())
                .from(post)
                .leftJoin(post.blog, blog)
                .leftJoin(post.member, member)
                .where(blog.id.eq(blogId)
                        .and(member.id.eq(memberId))
                        .and(post.status.ne(Status.TEMPORARY))
                        .and(post.createDate.between(
                                        todayDate.atStartOfDay(),
                                        todayDate.atTime(23, 59, 59)
                                )))
                .fetchOne();

        Long total = jpaQueryFactory
                .select(post.count())
                .from(post)
                .leftJoin(post.blog, blog)
                .leftJoin(post.member, member)
                .where(blog.id.eq(blogId)
                        .and(member.id.eq(memberId)
                        .and(post.status.ne(Status.TEMPORARY))))
                .fetchOne();

        return StatisticsMemberPostDto.builder()
                .today(today)
                .total(total)
                .build();

    }
}
