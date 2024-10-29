package com.logus.blog.repository;

import com.logus.blog.dto.BlogResponseDto;
import com.logus.blog.dto.FollowResponseDto;
import com.logus.blog.dto.FollowerResponseDto;
import com.logus.blog.dto.PostListResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlogRepositoryCustom {
    BlogResponseDto selectBlogInfo(String blogAddress);

    String findMyLogAddress(Long memberId);

    Page<FollowResponseDto> selectFollows(Long memberId, Pageable pageable);

    Page<FollowerResponseDto> selectFollowers(Long blogId, Pageable pageable);
}
