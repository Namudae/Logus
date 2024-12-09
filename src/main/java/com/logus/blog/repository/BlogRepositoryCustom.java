package com.logus.blog.repository;

import com.logus.admin.dto.BlogListResponseDto;
import com.logus.blog.dto.*;
import com.logus.blog.entity.Blog;
import com.logus.member.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogRepositoryCustom {
    BlogResponseDto selectBlogInfo(String blogAddress);

    String findMyLogAddress(Long memberId);

    List<Blog> ownedBlogs(Member member);

    Page<FollowResponseDto> selectFollows(Long memberId, Pageable pageable);

    Page<FollowerResponseDto> selectFollowers(Long blogId, Pageable pageable);

    List<OurLogResponseDto> findByMemberId(Long memberId);

    Page<BlogListResponseDto> searchBlogs(String loginId, String nickname, String blogName, String blogAddress, Pageable pageable);

    Long findMyLogId(Long memberId);

    List<StatisticsMemberDto.MemberPostDto> blogPostStatistics(Long blogId, Long memberId);

    StatisticsMemberDto blogPostStatisticsTodayTotal(Long blogId, Long memberId);

    List<StatisticsMemberDto.MemberPostDto> blogCommentStatistics(Long blogId, Long memberId);

    StatisticsMemberDto blogCommentStatisticsTodayTotal(Long blogId, Long memberId);
}
