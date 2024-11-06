package com.logus.member.repository;

import com.logus.blog.dto.FollowerResponseDto;
import com.logus.blog.dto.PostListResponseDto;
import com.logus.blog.entity.BlogAuth;
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
     * member, blog 따로 조회했을때 문제: blog로 검색하면 필터된 블로그까지 검색됨..
     */
    @Override
    public Page<MemberListResponse> searchMembers(String loginId, String nickname, String blogName, String blogAddress, Pageable pageable) {
//        JPAQuery<MemberListResponse> query = jpaQueryFactory
//                .select(Projections.fields(MemberListResponse.class,
//                        member.id.as("memberId"),
//                        member.nickname,
//                        blog.blogName,
//                        blog.blogAddress
//                ))
//                .from(member)
//                .leftJoin(blogMember)
//                .on(blogMember.member.eq(member)
//                        .and(blogMember.blogAuth.eq(BlogAuth.OWNER))
////                        .and(blogMember.blog.shareYn.eq("N"))
//                )
//                .leftJoin(blogMember.blog, blog)
//                .where(
//                    member.role.eq("USER"),
//                    containLoginId(loginId),
//                    containNickname(nickname),
//                    containBlogName(blogName),
//                    containBlogAddress(blogAddress)
//                )
//                .orderBy(member.loginId.asc());
//
//        // 총 결과 수 조회
//        long total = query.fetchCount();
//
//        // 페이지에 맞는 결과 조회
//        List<MemberListResponse> results = query
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize())
//                .fetch();
//
//        // 결과 필터링 (예시: 조건에 맞는 9개로 필터링)
//        List<MemberListResponse> filteredResults = results.stream()
//                .filter(result -> )
//                .collect(Collectors.toList());
//
//        // 필터링된 결과의 총 개수 (전체 데이터에서 필터링된 결과의 개수)
//        long filteredTotal = filteredResults.size();
//
//        // 새로운 Page 객체 생성 및 반환 (필터링된 결과와 새로운 total을 사용)
//        return new PageImpl<>(filteredResults, pageable, filteredTotal);

//        // Page 객체 생성 및 반환
//        return new PageImpl<>(results, pageable, total);
        return null;
    }

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
