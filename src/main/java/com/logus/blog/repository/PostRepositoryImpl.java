package com.logus.blog.repository;

import com.logus.blog.dto.PostDto;
import com.logus.blog.entity.QBlog;
import com.logus.blog.entity.QPost;
import com.logus.member.entity.QMember;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;

import java.util.List;

import static com.logus.blog.entity.QPost.*;
import static com.logus.member.entity.QMember.*;
import static org.springframework.util.StringUtils.hasText;

//@RequiredArgsConstructor //생성자 자동 주입
public class PostRepositoryImpl implements PostRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public PostRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    //2. Projection.fields()
    public List<PostDto.PostResponse> getAllPosts(String blogAddress) {
        return jpaQueryFactory
                .select(Projections.fields(PostDto.PostResponse.class,
                        member.id.as("memberId"),
                        member.nickname,
                        post.id.as("postId"),
                        post.title,
                        post.content,
                        post.count,
                        post.createDate))
                .from(post)
                .join(post.member, member)
                .where(
                        blogAddressEq(blogAddress)
                )
                .fetch();

    //1. @QuryProjection
//    public List<PostDto> getAllPosts(String blogAddress) {
//        return jpaQueryFactory
//                .select(new QPostDto(
//                        member.id.as("memberId"),
//                        member.nickname,
//                        post.id.as("postId"),
//                        post.title,
//                        post.content,
//                        post.createDate))
//                .from(post)
//                .join(post.member, member)
//                .where(
//                        blogAddressEq(blogAddress)
//                )
//                .fetch();



//        return null;
    }

    private BooleanExpression blogAddressEq(String blogAddress) {
        return hasText(blogAddress) ? post.blog.blogAddress.eq(blogAddress) : null;
    }


}
