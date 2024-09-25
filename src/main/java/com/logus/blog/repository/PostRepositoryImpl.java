package com.logus.blog.repository;

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

import static com.logus.blog.entity.QComment.comment;
import static com.logus.blog.entity.QLikey.likey;
import static com.logus.blog.entity.QPost.*;
import static com.logus.member.entity.QMember.*;
import static org.springframework.util.StringUtils.hasText;

//@RequiredArgsConstructor //생성자 자동 주입
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    /**
     * 블로그 내 모든 포스트 조회
     */
    public Page<PostResponseDto> selectAllBlogPosts(String blogAddress, Pageable pageable) {
        JPAQuery<PostResponseDto> query = jpaQueryFactory
                .select(Projections.fields(PostResponseDto.class,
                        member.id.as("memberId"),
                        member.nickname,
                        post.id.as("postId"),
                        post.title,
                        post.content,
                        post.views,
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
                        blogAddressEq(blogAddress)
                );

        // 총 결과 수 조회
        long total = query.fetchCount();

        // 페이지에 맞는 결과 조회
        List<PostResponseDto> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Page 객체 생성 및 반환
        return new PageImpl<>(results, pageable, total);
    }


    /**
     * 블로그 내 검색
     */
    public Page<PostResponseDto> searchBlogPosts(String blogAddress, String keyword, Pageable pageable) {
        JPAQuery<PostResponseDto> query = jpaQueryFactory
                .select(Projections.fields(PostResponseDto.class,
                        member.id.as("memberId"),
                        member.nickname,
                        post.id.as("postId"),
                        post.title,
                        post.content,
                        post.views,
                        post.createDate))
                .from(post)
                .join(post.member, member)
                .where(
                        blogAddressEq(blogAddress)
                        .and(post.title.contains(keyword)
                            .or(post.content.contains(keyword))
                        )
                );

        // 총 결과 수 조회
        long total = query.fetchCount();

        // 페이지에 맞는 결과 조회
        List<PostResponseDto> results = query
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
