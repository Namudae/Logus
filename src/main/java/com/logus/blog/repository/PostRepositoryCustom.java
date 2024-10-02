package com.logus.blog.repository;

import com.logus.blog.dto.PostListResponseDto;
import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
    Page<PostListResponseDto> selectAllBlogPosts(Long blogId, Long seriesId, Pageable pageable);

    Page<PostListResponseDto> searchBlogPosts(Long blogId, String keyword, Pageable pageable);

    PostResponseDto selectPost(Long postId);
}
