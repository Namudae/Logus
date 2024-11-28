package com.logus.blog.repository;

import com.logus.blog.dto.CommentListDto;
import com.logus.blog.dto.CommentResponseDto;
import com.logus.blog.dto.PostListResponseDto;
import com.logus.blog.entity.Status;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

import static com.logus.blog.entity.QComment.comment;
import static com.logus.blog.entity.QPost.post;
import static com.logus.member.entity.QMember.member;

public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public CommentRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    @Override
    public Page<CommentListDto> selectBlogComments(Long blogId, Long memberId, String keyword, String condition, Pageable pageable) {
        JPAQuery<CommentListDto> query = jpaQueryFactory
                .select(Projections.fields(CommentListDto.class,
                        comment.id.as("commentId"),
                        comment.member.id.as("memberId"),
                        comment.member.nickname,
                        comment.content,
                        comment.createDate,
                        comment.post.title))
                .from(comment)
                .leftJoin(comment.member, member)
                .leftJoin(comment.post, post)
                .where(
                        comment.post.blog.id.eq(blogId),
                        keywordContains(keyword), // keyword 조건 추가
                        commentCondition(condition, memberId) // condition 조건 추가
                )
                .orderBy(comment.createDate.desc());

        // 총 결과 수 조회
        long total = query.fetchCount();

        // 페이지에 맞는 결과 조회
        List<CommentListDto> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Page 객체 생성 및 반환
        return new PageImpl<>(results, pageable, total);
    }

    private BooleanExpression keywordContains(String keyword) {
        return keyword != null ? comment.content.contains(keyword) : null;
    }

    private BooleanExpression commentCondition(String condition, Long memberId) {
        if ("MY".equalsIgnoreCase(condition)) {
            // 내 댓글만 조회
            return comment.member.id.eq(memberId);
        } else if ("NOTMY".equalsIgnoreCase(condition)) {
            // 내 댓글 제외
            return comment.member.id.ne(memberId);
        }
        // "ALL"인 경우 또는 null/다른 값인 경우 모든 데이터 조회
        return null;
    }

}
