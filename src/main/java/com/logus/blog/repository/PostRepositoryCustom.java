package com.logus.blog.repository;

import com.logus.admin.entity.Category;
import com.logus.blog.dto.*;
import com.logus.blog.entity.Post;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepositoryCustom {
    Page<PostListResponseDto> selectAllBlogPosts(Long blogId, Long seriesId, Pageable pageable, Long requestId);

    Page<PostListResponseDto> searchBlogPosts(Long blogId, String keyword, Pageable pageable);

    PostResponseDto selectPost(Long postId);

    Page<PostListResponseDto> searchBlogPostsByTag(Long blogId, Long tagId, Pageable pageable, Long requestId);

    PostResponseDto selectPrePost(Post post);
    PostResponseDto selectNextPost(Post post);

    TempPostResponseDto selectTemp(Long blogId, Long memberId);

    Page<MainGridResponse> selectMainPosts(MainGridCondition condition, Pageable pageable);

    Page<MainGridResponse> selectMainPostsCategory(MainGridCondition condition, Category category, Pageable pageable);
}
