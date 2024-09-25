package com.logus.blog.repository;

import com.logus.blog.dto.PostResponseDto;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
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
import static com.logus.blog.entity.QPost.post;
import static com.logus.blog.entity.QPostTag.postTag;
import static com.logus.blog.entity.QTag.tag;
import static com.logus.member.entity.QMember.member;

public class PostTagRepositoryImpl implements PostTagRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public PostTagRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    //게시글 내 태그 조회
    public List<String> selectPostTag(Long postId) {
        JPAQuery<String> query = jpaQueryFactory
                .select(tag.tagName)
                .from(postTag)
                .join(postTag.tag, tag)
                .where(postTag.post.id.eq(postId));
        return query.fetch();
    }


}
