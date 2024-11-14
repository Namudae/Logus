package com.logus.blog.repository;

import com.logus.admin.entity.Category;
import com.logus.admin.entity.QCategory;
import com.logus.blog.dto.*;
import com.logus.blog.entity.Post;
import com.logus.blog.entity.QPost;
import com.logus.blog.entity.Status;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.DateTimePath;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.logus.blog.entity.QBlog.blog;
import static com.logus.blog.entity.QBlogMember.blogMember;
import static com.logus.admin.entity.QCategory.category;
import static com.logus.blog.entity.QComment.comment;
import static com.logus.blog.entity.QLikey.likey;
import static com.logus.blog.entity.QPost.*;
import static com.logus.blog.entity.QPostTag.postTag;
import static com.logus.blog.entity.QSeries.series;
import static com.logus.member.entity.QMember.*;
import static org.springframework.util.StringUtils.hasText;

//@RequiredArgsConstructor //생성자 자동 주입
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    /**
     * 단건 조회
     */
    @Override
    public PostResponseDto selectPost(Long postId) {
        QCategory parentCategory = new QCategory("parentCategory");

        return jpaQueryFactory
                .select(Projections.fields(PostResponseDto.class,
                        member.id.as("memberId"),
                        member.nickname,
                        category.id.as("categoryId"),
                        category.categoryName,
                        category.parent.id.as("parentCategoryId"),
                        category.parent.categoryName.as("parentCategoryName"),
                        series.id.as("seriesId"),
                        series.seriesName,
                        post.id.as("postId"),
                        post.title,
                        post.content,
                        post.imgUrl,
                        post.views,
                        post.status,
                        post.reportStatus,
                        post.createDate,
                        ExpressionUtils.as(
                                JPAExpressions.select(comment.count())
                                        .from(comment)
                                        .where(comment.post.eq(post)),
                                "commentCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(likey.count())
                                        .from(likey)
                                        .where(likey.post.eq(post)),
                                "likeCount"
                        )))
                .from(post)
                .join(post.member, member)
                .leftJoin(post.category, category)
                .leftJoin(post.series, series)
                .leftJoin(category.parent, parentCategory)
                .where(post.id.eq(postId))
                .fetchOne();
    }

    /**
     * 블로그 내 모든 포스트 조회
     */
    public Page<PostListResponseDto> selectAllBlogPosts(Long blogId, Long seriesId, Pageable pageable, Long requestId) {

        JPAQuery<PostListResponseDto> query = jpaQueryFactory
                .select(Projections.fields(PostListResponseDto.class,
                        member.id.as("memberId"),
                        member.nickname,
                        category.id.as("categoryId"),
                        category.categoryName,
                        series.id.as("seriesId"),
                        series.seriesName,
                        post.id.as("postId"),
                        post.title,
                        post.content,
                        post.imgUrl,
                        post.views,
                        post.status,
                        post.reportStatus,
                        post.createDate,
                        ExpressionUtils.as(
                                JPAExpressions.select(comment.count())
                                        .from(comment)
                                        .where(comment.post.eq(post)),
                                "commentCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(likey.count())
                                        .from(likey)
                                        .where(likey.post.eq(post)),
                                "likeCount"
                        )))
                .from(post)
                .join(post.member, member)
                .leftJoin(post.category, category)
                .leftJoin(post.series, series)
                .where(
                        post.blog.id.eq(blogId),
                        seriesEq(seriesId),
                        (requestId != null ? checkPublic(blogId, requestId, post) : post.status.eq(Status.PUBLIC))
                )
                .orderBy(post.createDate.desc());  // post.createDate 기준 오름차순 정렬

        // 총 결과 수 조회
        long total = query.fetchCount();

        // 페이지에 맞는 결과 조회
        List<PostListResponseDto> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Page 객체 생성 및 반환
        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public PostResponseDto selectPrePost(Post p) {

        return jpaQueryFactory
                .select(Projections.fields(PostResponseDto.class,
                        post.id.as("preId"),
                        post.title.as("preTitle"))
                )
                .from(post)
                .where(
                        post.blog.id.eq(p.getBlog().getId()),
                        dateLoe(p.getCreateDate()), //이전 게시글
                        post.series.eq(p.getSeries()), //같은 시리즈
                        post.status.eq(Status.PUBLIC)
                )
                .orderBy(post.createDate.desc())
                .limit(1)
                .fetchOne();

    }

    @Override
    public PostResponseDto selectNextPost(Post p) {

        return jpaQueryFactory
                .select(Projections.fields(PostResponseDto.class,
                        post.id.as("nextId"),
                        post.title.as("nextTitle"))
                )
                .from(post)
                .where(
                        post.blog.id.eq(p.getBlog().getId()),
                        dateGoe(p.getCreateDate()), //이후 게시글
                        post.series.eq(p.getSeries()), //같은 시리즈
                        post.status.eq(Status.PUBLIC)
                )
                .orderBy(post.createDate.asc())
                .limit(1)
                .fetchOne();
    }

    /**
     * 블로그 내 검색
     */
    public Page<PostListResponseDto> searchBlogPosts(Long blogId, String keyword, Long memberId, Pageable pageable) {
        JPAQuery<PostListResponseDto> query = jpaQueryFactory
                .select(Projections.fields(PostListResponseDto.class,
                        member.id.as("memberId"),
                        member.nickname,
                        category.id.as("categoryId"),
                        category.categoryName,
                        series.id.as("seriesId"),
                        series.seriesName,
                        post.id.as("postId"),
                        post.title,
                        post.content,
                        post.imgUrl,
                        post.views,
                        post.status,
                        post.reportStatus,
                        post.createDate,
                        ExpressionUtils.as(
                                JPAExpressions.select(comment.count())
                                        .from(comment)
                                        .where(comment.post.eq(post)),
                                "commentCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(likey.count())
                                        .from(likey)
                                        .where(likey.post.eq(post)),
                                "likeCount"
                        )))
                .from(post)
                .join(post.member, member)
                .where(
                        post.blog.id.eq(blogId)
                        .and(post.title.contains(keyword)
                            .or(post.content.contains(keyword))
                        )
                        .and(memberId != null ? checkPublic(blogId, memberId, post) : post.status.eq(Status.PUBLIC))
                );

        // 총 결과 수 조회
        long total = query.fetchCount();

        // 페이지에 맞는 결과 조회
        List<PostListResponseDto> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Page 객체 생성 및 반환
        return new PageImpl<>(results, pageable, total);
    }

    //tag검색
    public Page<PostListResponseDto> searchBlogPostsByTag(Long blogId, Long tagId, Long memberId, Pageable pageable) {
        JPAQuery<PostListResponseDto> query = jpaQueryFactory
                .select(Projections.fields(PostListResponseDto.class,
                        member.id.as("memberId"),
                        member.nickname,
                        category.id.as("categoryId"),
                        category.categoryName,
                        series.id.as("seriesId"),
                        series.seriesName,
                        post.id.as("postId"),
                        post.title,
                        post.content,
                        post.imgUrl,
                        post.views,
                        post.status,
                        post.reportStatus,
                        post.createDate,
                        ExpressionUtils.as(
                                JPAExpressions.select(comment.count())
                                        .from(comment)
                                        .where(comment.post.eq(post)),
                                "commentCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(likey.count())
                                        .from(likey)
                                        .where(likey.post.eq(post)),
                                "likeCount"
                        )))
                .from(postTag)
                .join(postTag.post, post)
                .join(post.member, member)
                .leftJoin(post.category, category)
                .leftJoin(post.series, series)
                .where(
                        post.blog.id.eq(blogId),
                        postTag.tag.id.eq(tagId),
                        (memberId != null ? checkPublic(blogId, memberId, post) : post.status.eq(Status.PUBLIC))
                );

        // 총 결과 수 조회
        long total = query.fetchCount();

        // 페이지에 맞는 결과 조회
        List<PostListResponseDto> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Page 객체 생성 및 반환
        return new PageImpl<>(results, pageable, total);
    }

    /**
     * 임시저장글 조회
     */
    @Override
    public Optional<TempPostResponseDto> selectTemp(Long blogId, Long memberId) {
        TempPostResponseDto result = jpaQueryFactory
                .select(Projections.fields(TempPostResponseDto.class,
                        post.id.as("postId"),
                        post.member.id.as("memberId"),
                        post.blog.id.as("blogId"),
                        post.category.id.as("categoryId"),
                        post.category.categoryName,
                        post.series.id.as("seriesId"),
                        post.title,
                        post.content,
                        post.imgUrl,
                        post.status,
                        post.createDate
                ))
                .from(post)
                .where(
                        post.blog.id.eq(blogId),
                        post.member.id.eq(memberId),
                        post.status.eq(Status.TEMPORARY)
                )
                .join(post.member, member)
                .leftJoin(post.blog, blog)
                .leftJoin(post.category, category)
                .leftJoin(post.series, series)
                .fetchOne();

        return Optional.ofNullable(result);
    }

    private BooleanExpression blogAddressEq(String blogAddress) {
        return hasText(blogAddress) ? post.blog.blogAddress.eq(blogAddress) : null;
    }

    private BooleanExpression seriesEq(Long seriesId) {
        return seriesId != null ? post.series.id.eq(seriesId) : null;
    }

    //작성 날짜가 이전인 게시글
    private BooleanExpression dateLoe(LocalDateTime dateLoe) {
        return dateLoe != null ? post.createDate.lt(dateLoe) : null;
    }

    //작성 날짜가 이후인 게시글
    private BooleanExpression dateGoe(LocalDateTime dateGoe) {
        return dateGoe != null ? post.createDate.gt(dateGoe) : null;
    }

    private BooleanExpression checkPublic(Long blogId, Long requestId, QPost post) {
        return post.status.ne(Status.TEMPORARY) //임시저장글 제외
                //블로그 멤버일 경우 비밀글 조회
                .and(
                        JPAExpressions.selectOne()
                                .from(blogMember)
                                .where(
                                        blogMember.blog.id.eq(blogId)
                                        .and(blogMember.member.id.eq(requestId))
                                )
                                .exists()
                                .and(post.status.eq(Status.SECRET))
                )
                .or(post.status.eq(Status.PUBLIC)); //공개글
    }

    @Override
    public Page<MainGridResponse> selectMainPosts(MainGridCondition condition, Pageable pageable) {

        // 1. Category 조회
        List<Category> categories = jpaQueryFactory
                .select(category)
                .from(category)
                .where(category.parent.isNotNull(),
                        getCategory(condition.getCategoryId()))  // 자식 카테고리만 조회
                .orderBy(category.parent.orderSeq.asc(), category.orderSeq.asc())
                .offset(pageable.getOffset()) // 페이지네이션 offset
                .limit(pageable.getPageSize()) // 페이지네이션 limit
                .fetch();  // 결과를 List<Category>로 가져옴

        // 2. 카테고리의 총 개수 조회 (total elements)
        long total = jpaQueryFactory
                .selectFrom(category)
                .where(category.parent.isNotNull())  // 부모 카테고리만 조회
                .fetchCount();  // 카테고리의 총 개수

        // 2. Category별로 PostList 조회
        List<MainGridResponse> results = categories.stream()
                .map(cat -> {
                    // 각 카테고리마다 해당 카테고리에 속하는 포스트를 조회
                    List<MainGridResponse.PostDto> postDtos = jpaQueryFactory
                            .select(Projections.bean(MainGridResponse.PostDto.class,
                                    post.id.as("postId"),
                                    post.blog.id.as("blogId"),
                                    post.imgUrl,
                                    post.title,
                                    post.content,
                                    post.views,
                                    ExpressionUtils.as(
                                            JPAExpressions.select(comment.count())
                                                    .from(comment)
                                                    .where(comment.post.eq(post)),
                                            "commentCount"
                                    ),
                                    ExpressionUtils.as(
                                            JPAExpressions.select(likey.count())
                                                    .from(likey)
                                                    .where(likey.post.eq(post)),
                                            "likeCount"
                                    )
                            ))
                            .from(post)
                            .leftJoin(post.category, category)
                            .leftJoin(post.blog, blog)
                            .where(
                                    post.category.id.eq(cat.getId())
                                    , getDateCondition(condition.getDate()),
                                    post.status.eq(Status.PUBLIC)
                            )
                            .orderBy(getOrderBy(condition.getGrid()))
                            .offset(0)
                            .limit(6) // 6개 고정
                            .fetch();

                    // MainGridResponse 객체를 생성하여 반환
                    return MainGridResponse.builder()
                            .categoryId(cat.getId())
                            .categoryName(cat.getCategoryName())
                            .postList(postDtos)
                            .build();
                })
                .toList();

        // 4. Page 객체 반환
        return new PageImpl<>(results, pageable, total);
    }

    @Override
    public Page<MainGridResponse> selectMainPostsCategory(MainGridCondition condition, Category cat, Pageable pageable) {

        List<MainGridResponse.PostDto> postDtos = jpaQueryFactory
                .select(Projections.bean(MainGridResponse.PostDto.class,
                        post.id.as("postId"),
                        post.blog.id.as("blogId"),
                        post.imgUrl,
                        post.title,
                        post.content,
                        post.views,
                        ExpressionUtils.as(
                                JPAExpressions.select(comment.count())
                                        .from(comment)
                                        .where(comment.post.eq(post)),
                                "commentCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(likey.count())
                                        .from(likey)
                                        .where(likey.post.eq(post)),
                                "likeCount"
                        )
                ))
                .from(post)
                .leftJoin(post.category, category)
                .leftJoin(post.blog, blog)
                .where(
                        post.category.id.eq(condition.getCategoryId()),  // 특정 카테고리로 필터링
                        getDateCondition(condition.getDate()),
                        post.status.eq(Status.PUBLIC)  // 날짜 조건 추가
                )
                .orderBy(getOrderBy(condition.getGrid()))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize()) // pageable에서 가져온 size로 limit 적용
                .fetch();

        long total = Optional.ofNullable(
                jpaQueryFactory
                        .select(post.count())
                        .from(post)
                        .where(
                                post.category.id.eq(condition.getCategoryId()),  // 특정 카테고리로 필터링
                                getDateCondition(condition.getDate())  // 날짜 조건 추가
                        )
                        .fetchOne()).orElse(0L);

        MainGridResponse response = MainGridResponse.builder()
                .categoryId(cat.getId())
                .categoryName(cat.getCategoryName())
                .postList(postDtos)
                .build();

        return new PageImpl<>(Collections.singletonList(response), pageable, total);
    }

    // 날짜 조건에 맞는 시작 날짜를 반환하는 메서드
    private BooleanExpression getDateCondition(String dateCondition) {
        LocalDateTime now = LocalDateTime.now();

        if (dateCondition == null) {
            dateCondition = "week";  // 기본값 설정: week
        }

        return switch (dateCondition) {
            case "day" -> post.createDate.goe(now.minusDays(1));  // 하루 이내
            case "week" -> post.createDate.goe(now.minusWeeks(1));  // 일주일 이내
            case "month" -> post.createDate.goe(now.minusMonths(1));  // 한 달 이내
            case "year" -> post.createDate.goe(now.minusYears(1));  // 1년 이내
            default -> throw new IllegalArgumentException("Invalid date condition: " + dateCondition);
        };
    }

    private BooleanExpression getCategory(Long categoryId) {
        return categoryId != null ? category.parent.id.eq(categoryId) : null;
    }

    private OrderSpecifier<?> getOrderBy(String grid) {
        if ("trend".equals(grid)) {
            return post.views.desc();  // 조회수 기준 내림차순
        } else if ("new".equals(grid)) {
            return post.createDate.desc();  // 생성일 기준 내림차순
        } else {
            return post.views.desc();  // 기본값으로 조회수 기준 내림차순
        }
    }

    @Override
    public Page<PostListResponseDto> searchPostsMain(String keyword, Pageable pageable) {
        JPAQuery<PostListResponseDto> query = jpaQueryFactory
                .select(Projections.fields(PostListResponseDto.class,
                        member.id.as("memberId"),
                        member.nickname,
                        category.id.as("categoryId"),
                        category.categoryName,
                        series.id.as("seriesId"),
                        series.seriesName,
                        post.id.as("postId"),
                        post.title,
                        post.content,
                        post.imgUrl,
                        post.views,
                        post.status,
                        post.reportStatus,
                        post.createDate,
                        ExpressionUtils.as(
                                JPAExpressions.select(comment.count())
                                        .from(comment)
                                        .where(comment.post.eq(post)),
                                "commentCount"
                        ),
                        ExpressionUtils.as(
                                JPAExpressions.select(likey.count())
                                        .from(likey)
                                        .where(likey.post.eq(post)),
                                "likeCount"
                        )))
                .from(post)
                .join(post.member, member)
                .where(
                        post.status.eq(Status.PUBLIC)
                        .and(post.title.contains(keyword)
                            .or(post.content.contains(keyword)))
                );

        // 총 결과 수 조회
        long total = query.fetchCount();

        // 페이지에 맞는 결과 조회
        List<PostListResponseDto> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Page 객체 생성 및 반환
        return new PageImpl<>(results, pageable, total);
    }
}
