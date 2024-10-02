package com.logus.blog.repository;

import com.logus.blog.dto.PostListResponseDto;
import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.logus.blog.entity.QCategory.category;
import static com.logus.blog.entity.QComment.comment;
import static com.logus.blog.entity.QLikey.likey;
import static com.logus.blog.entity.QPost.*;
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
    public Page<PostListResponseDto> selectAllBlogPosts(Long blogId, Pageable pageable) {
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
//                        blogAddressEq(blogAddress)
                        post.blog.id.eq(blogId)
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

    //여기서 postTag까지 조회

    private BooleanExpression blogAddressEq(String blogAddress) {
        return hasText(blogAddress) ? post.blog.blogAddress.eq(blogAddress) : null;
    }


}
