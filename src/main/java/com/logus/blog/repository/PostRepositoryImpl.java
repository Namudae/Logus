package com.logus.blog.repository;

import com.logus.blog.dto.PostListResponseDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.QPost;
import com.logus.blog.entity.Status;
import com.querydsl.core.types.ExpressionUtils;
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
import java.util.List;

import static com.logus.blog.entity.QBlogMember.blogMember;
import static com.logus.blog.entity.QCategory.category;
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
        return jpaQueryFactory
                .select(Projections.fields(PostResponseDto.class,
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
                .where(post.id.eq(postId))
                .fetchOne();
    }

    /**
     * 블로그 내 모든 포스트 조회
     */
    public Page<PostListResponseDto> selectAllBlogPosts(Long blogId, Long seriesId, Pageable pageable, Long requestId) {

        // Author 확인
        BooleanExpression isAuthor = post.member.id.eq(requestId);

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
                        checkPublic(blogId, requestId, post)
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
    public PostResponseDto selectPrePost(Long blogId, LocalDateTime createDate) {

        return jpaQueryFactory
                .select(Projections.fields(PostResponseDto.class,
                        post.id.as("preId"),
                        post.title.as("preTitle"))
                )
                .from(post)
                .where(
                        post.blog.id.eq(blogId),
                        dateLoe(createDate), //이전 게시글
                        post.status.eq(Status.PUBLIC)
                )
                .orderBy(post.createDate.desc())
                .limit(1)
                .fetchOne();

    }

    @Override
    public PostResponseDto selectNextPost(Long blogId, LocalDateTime createDate) {

        return jpaQueryFactory
                .select(Projections.fields(PostResponseDto.class,
                        post.id.as("nextId"),
                        post.title.as("nextTitle"))
                )
                .from(post)
                .where(
                        post.blog.id.eq(blogId),
                        dateGoe(createDate), //이이후 게시글
                        post.status.eq(Status.PUBLIC)
                )
                .orderBy(post.createDate.desc())
                .limit(1)
                .fetchOne();
    }

    /**
     * 블로그 내 검색
     */
    public Page<PostListResponseDto> searchBlogPosts(Long blogId, String keyword, Pageable pageable) {
        JPAQuery<PostListResponseDto> query = jpaQueryFactory
                .select(Projections.fields(PostListResponseDto.class,
                        member.id.as("memberId"),
                        member.nickname,
                        post.id.as("postId"),
                        post.title,
                        post.content,
                        post.imgUrl,
                        post.views,
                        post.createDate))
                .from(post)
                .join(post.member, member)
                .where(
                        post.blog.id.eq(blogId)
                        .and(post.title.contains(keyword)
                            .or(post.content.contains(keyword))
                        )
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
    public Page<PostListResponseDto> searchBlogPostsByTag(Long blogId, Long tagId, Pageable pageable) {
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
                .join(post.category, category)
                .join(post.series, series)
                .where(
                        post.blog.id.eq(blogId),
                        postTag.tag.id.eq(tagId)
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





}
