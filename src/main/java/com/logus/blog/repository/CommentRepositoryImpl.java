package com.logus.blog.repository;

import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;

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
