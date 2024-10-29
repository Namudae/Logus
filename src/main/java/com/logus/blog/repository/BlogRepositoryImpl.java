package com.logus.blog.repository;

import com.logus.blog.dto.*;
import com.logus.blog.entity.Status;
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

import static com.logus.blog.entity.QBlog.blog;
import static com.logus.blog.entity.QBlogMember.blogMember;
import static com.logus.blog.entity.QCategory.category;
import static com.logus.blog.entity.QComment.comment;
import static com.logus.blog.entity.QFollow.follow;
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

    /**
     * My-Log 검색
     */
    @Override
    public String findMyLogAddress(Long memberId) {
        return jpaQueryFactory
                .select(blog.blogAddress)
                .from(blog)
                .leftJoin(blog.blogMembers, blogMember)
                .where(
                        blogMember.member.id.eq(memberId),
                        blog.shareYn.eq("N")
                )
                .fetchOne();
    }

    /**
     * 팔로우 조회
     */
    @Override
    public Page<FollowResponseDto> selectFollows(Long memberId, Pageable pageable) {

        JPAQuery<FollowResponseDto> query = jpaQueryFactory
                .select(Projections.fields(FollowResponseDto.class,
                        follow.id.as("followId"),
                        follow.blog.id.as("blogId"),
                        follow.blog.blogName,
                        follow.blog.blogAddress,
                        follow.blog.introduce))
                .from(follow)
                .leftJoin(follow.blog, blog)
                .where(
                        follow.member.id.eq(memberId)
                )
                .orderBy(follow.createDate.desc()); //최근 팔로우부터

        // 총 결과 수 조회
        long total = query.fetchCount();

        // 페이지에 맞는 결과 조회
        List<FollowResponseDto> results = query
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        // Page 객체 생성 및 반환
        return new PageImpl<>(results, pageable, total);
    }

    /**
     * 팔로워 조회
     */
    @Override
    public Page<FollowerResponseDto> selectFollowers(Long blogId, Pageable pageable) {
        List<FollowerResponseDto> followerList = jpaQueryFactory
                .select(Projections.fields(FollowerResponseDto.class,
                        follow.id.as("followId"),
                        follow.member.id.as("memberId"),
                        follow.member.nickname,
                        follow.member.imgUrl))
                .from(follow)
                .leftJoin(follow.member, member)
                .leftJoin(follow.blog, blog)
                .where(
                        follow.blog.id.eq(blogId)
                )
                .orderBy(follow.createDate.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch(); //최근 팔로우부터

        // 팔로워의 블로그 목록 조회
        for (FollowerResponseDto follower : followerList) {
            List<FollowerResponseDto.BlogDto> blogDtos = jpaQueryFactory
                    .select(Projections.fields(FollowerResponseDto.BlogDto.class,
                            blog.id.as("blogId"),
                            blog.blogName,
                            blog.blogAddress,
                            blog.shareYn))
                    .from(blogMember)
                    .leftJoin(blogMember.blog, blog)
                    .leftJoin(blogMember.member, member)
                    .where(blogMember.member.id.eq(follower.getMemberId())) // 블로그 주인 ID로 필터링
                    .fetch();
            follower.setBlogList(blogDtos); // 블로그 목록 추가
        }

        long total = followerList.size(); // 총 수 계산

        // Page 객체 생성 및 반환
        return new PageImpl<>(followerList, pageable, total);
    }
}
