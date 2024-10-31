package com.logus.blog.repository;

import com.logus.blog.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface BlogRepositoryCustom {
    BlogResponseDto selectBlogInfo(String blogAddress);

    String findMyLogAddress(Long memberId);

    Page<FollowResponseDto> selectFollows(Long memberId, Pageable pageable);

    Page<FollowerResponseDto> selectFollowers(Long blogId, Pageable pageable);

    List<OurLogResponseDto> findByMemberId(Long memberId);
}
