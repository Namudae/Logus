package com.logus.blog.repository;

import com.logus.blog.dto.CommentResponseDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Status;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

import java.util.List;

import static com.logus.blog.entity.QComment.comment;
import static com.logus.member.entity.QMember.member;

public class CommentRepositoryImpl implements CommentRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public CommentRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

//    @Override
//    public List<CommentResponseDto> selectComments(Long postId) {
//        return jpaQueryFactory
//                .select(Projections.fields(CommentResponseDto.class,
//                        comment.id.as("commentId"),
//                        comment.member.id.as("memberId"),
//                        comment.member.nickname,
//                        comment.parent.id.as("parentId"),
//                        comment.depth,
//                        comment.content,
//                        comment.status,
//                        comment.reportStatus,
//                        comment.createDate))
//                .where(
//                        comment.post.id.eq(postId),
//                        comment.status.eq(Status.PUBLIC)
//                )
//                .fetch();
//    }
}
