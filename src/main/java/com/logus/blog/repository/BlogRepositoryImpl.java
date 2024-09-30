package com.logus.blog.repository;

import com.logus.blog.dto.BlogResponseDto;
import com.logus.blog.dto.PostResponseDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import static com.logus.blog.entity.QBlog.blog;
import static com.logus.blog.entity.QBlogMember.blogMember;
import static com.logus.blog.entity.QCategory.category;
import static com.logus.blog.entity.QComment.comment;
import static com.logus.blog.entity.QLikey.likey;
import static com.logus.blog.entity.QPost.post;
import static com.logus.blog.entity.QSeries.series;
import static com.logus.member.entity.QMember.member;

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
}
