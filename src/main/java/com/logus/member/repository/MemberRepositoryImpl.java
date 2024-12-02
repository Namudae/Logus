package com.logus.member.repository;

import com.logus.admin.dto.BlogListResponseDto;
import com.logus.blog.dto.FollowerResponseDto;
import com.logus.blog.dto.PostListResponseDto;
import com.logus.blog.entity.BlogAuth;
import com.logus.blog.entity.QBlogMember;
import com.logus.member.dto.MemberListResponse;
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

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.logus.blog.entity.QBlog.blog;
import static com.logus.blog.entity.QBlogMember.blogMember;
import static com.logus.member.entity.QMember.member;
import static org.springframework.util.StringUtils.hasText;

public class MemberRepositoryImpl implements MemberRepositoryCustom {

    private final JPAQueryFactory jpaQueryFactory;

    public MemberRepositoryImpl(EntityManager em) {
        this.jpaQueryFactory = new JPAQueryFactory(em);
    }

    /**
     * 멤버 검색
     */
    @Override
    public Page<MemberListResponse> searchMembers(String loginId, String nickname, String blogName, String blogAddress, Pageable pageable) {
        // 1. 블로그 관련 조건이 있을 경우, 블로그 조건에 맞는 블로그 조회
        List<MemberListResponse> memberList = new ArrayList<>();
        long total = 0;

        if (blogName == null && blogAddress == null) {
            //멤버 조회
            memberList = jpaQueryFactory
                    .select(Projections.fields(MemberListResponse.class,
                            member.id.as("memberId"),
                            member.nickname,
                            member.loginId
                    ))
                    .from(member)
                    .where(
                            member.role.eq("USER"),
                            containLoginId(loginId),
                            containNickname(nickname)
//                        containBlogName(blogName),
//                        containBlogAddress(blogAddress)
                    )
                    .orderBy(member.createDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            // 2. 각 멤버에 대해 블로그 정보 조회
            for (MemberListResponse member : memberList) {
                // 블로그 정보 조회 (멤버 ID를 기준으로)
                MemberListResponse.BlogResponse blogDto = jpaQueryFactory
                        .select(Projections.fields(MemberListResponse.BlogResponse.class, // BlogResponse를 사용하여 블로그 정보를 받음
                                blog.id.as("blogId"),
                                blog.blogName,
                                blog.blogAddress
                        ))
                        .from(blogMember)
                        .leftJoin(blogMember.blog, blog)
                        .where(
                                blogMember.member.id.eq(member.getMemberId())
                                        .and(blogMember.blogAuth.eq(BlogAuth.OWNER))
                                        .and(blog.shareYn.eq("N")),
                                containBlogName(blogName),
                                containBlogAddress(blogAddress)
                        )
                        .orderBy(blog.createDate.asc()) // 날짜가 빠른 블로그가 먼저 오도록 정렬
                        .limit(1)
                        .fetchFirst();

                // 블로그 정보를 멤버에 추가
                member.setBlogResponse(blogDto);  // 블로그 정보 리스트를 멤버 객체에 추가
            }

            // 3. 전체 멤버 수 계산
            total = jpaQueryFactory
                    .select(member.count())
                    .from(member)
                    .where(
                            member.role.eq("USER"),
                            containLoginId(loginId),
                            containNickname(nickname)
                    )
                    .fetchOne(); // 전체 멤버 수를 계산

        } else {
            //블로그 조회
            memberList = jpaQueryFactory
                    .select(Projections.fields(MemberListResponse.class,
                            member.id.as("memberId"),
                            member.loginId,
                            member.nickname,
                            Projections.fields(MemberListResponse.BlogResponse.class,
                                    blog.id.as("blogId"),
                                    blog.blogName,
                                    blog.blogAddress).as("blogResponse")
                            ))
                    .from(member)  // 'member'를 기준으로 조회
                    .leftJoin(blogMember).on(blogMember.member.id.eq(member.id))  // member와 blogMember를 연결
                    .leftJoin(blog).on(blog.id.eq(blogMember.blog.id))  // blog와 blogMember를 연결
                    .where(
                            blog.shareYn.eq("N"),
                            blogMember.blogAuth.eq(BlogAuth.OWNER),
                            containBlogName(blogName),
                            containBlogAddress(blogAddress)
                    )
                    .orderBy(member.createDate.desc())
                    .offset(pageable.getOffset())
                    .limit(pageable.getPageSize())
                    .fetch();

            // 2. 전체 멤버 수 계산
            total = jpaQueryFactory
                    .select(member.count())
                    .from(member)
                    .leftJoin(blogMember).on(blogMember.member.id.eq(member.id))  // blogMember와 member를 조인
                    .leftJoin(blog).on(blog.id.eq(blogMember.blog.id))  // blog과 blogMember를 조인
                    .where(
                            blog.shareYn.eq("N"),
                            blogMember.blogAuth.eq(BlogAuth.OWNER),
                            containBlogName(blogName),
                            containBlogAddress(blogAddress)
                    )
                    .fetchOne();  // 전체 결과 수를 반환

        }

        // 4. Page 객체 생성 및 반환
        return new PageImpl<>(memberList, pageable, total);

    }

    /**
     * 멤버 검색2 (블로그 없으면 조회x)
     */
//    @Override
//    public Page<MemberListResponse> searchMembers(String loginId, String nickname, String blogName, String blogAddress, Pageable pageable) {
//        List<MemberListResponse> content = jpaQueryFactory
//                .select(Projections.fields(MemberListResponse.class,
//                        member.id.as("memberId"),
//                        member.nickname,
//                        member.loginId,
//                        Projections.fields(MemberListResponse.BlogResponse.class, // BlogResponse 부분 매핑
//                                blog.id.as("blogId"),
//                                blog.blogName.as("blogName"),
//                                blog.blogAddress.as("blogAddress")
//                        ).as("blogResponse")
//                ))
//                .from(blog)
//                .leftJoin(blog.blogMembers, blogMember)
//                .leftJoin(blogMember.member, member)
//                .where(
//                        blogMember.member.id.eq(member.id),
//                        blog.shareYn.eq("N"),
//                        member.role.eq("USER"),
//                        containLoginId(loginId),
//                        containNickname(nickname),
//                        containBlogName(blogName),
//                        containBlogAddress(blogAddress)
//                )
//                .orderBy(member.createDate.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        // Total count 쿼리
//        long total = jpaQueryFactory
//                .select(member.count())
//                .from(blog)
//                .leftJoin(blog.blogMembers, blogMember)
//                .leftJoin(blogMember.member, member)
//                .where(
//                        blogMember.member.id.eq(member.id),
//                        blog.shareYn.eq("N")
//                )
//                .fetchOne();
//
//        // Page 반환
//        return new PageImpl<>(content, pageable, total);
//    }

    private BooleanExpression containLoginId(String loginId) {
        return hasText(loginId) ? member.loginId.contains(loginId) : null;
    }

    private BooleanExpression containNickname(String nickname) {
        return hasText(nickname) ? member.nickname.contains(nickname) : null;
    }

    private BooleanExpression containBlogName(String blogName) {
        return hasText(blogName) ? blogMember.blog.blogName.contains(blogName) : null;
    }

    private BooleanExpression containBlogAddress(String blogAddress) {
        return hasText(blogAddress) ? blogMember.blog.blogAddress.contains(blogAddress) : null;
    }

}
