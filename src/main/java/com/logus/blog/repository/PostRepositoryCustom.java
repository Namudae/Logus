package com.logus.blog.repository;

import com.logus.admin.dto.AdminCommentListResponse;
import com.logus.admin.dto.AdminPostListResponse;
import com.logus.admin.entity.Category;
import com.logus.blog.dto.*;
import com.logus.blog.entity.Post;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostRepositoryCustom {
    Page<PostListResponseDto> selectAllBlogPosts(Long blogId, Long seriesId, Pageable pageable, Long requestId);

    Page<PostListResponseDto> searchBlogPosts(Long blogId, String keyword, String condition, Long memberId, Pageable pageable);

    PostResponseDto selectPost(Long postId);

    Page<PostListResponseDto> searchBlogPostsByTag(Long blogId, Long tagId, Long memberId, Pageable pageable);

    PostResponseDto selectPrePost(Post post);
    PostResponseDto selectNextPost(Post post);

    Optional<TempPostResponseDto> selectTemp(Long blogId, Long memberId);

    Page<MainGridResponse> selectMainPosts(MainGridCondition condition, Pageable pageable);

    Page<MainGridResponse> selectMainPostsCategory(MainGridCondition condition, Category category, Pageable pageable);

    Page<PostListResponseDto> searchPostsMain(String keyword, Pageable pageable);

    Page<PostListResponseDto> searchBlogPostsByMember(Long blogId, String keyword, String condition, Long memberId, Pageable pageable);

    Page<AdminPostListResponse> searchPostsByAdmin(String keyword, String condition, Pageable pageable);

    Page<AdminCommentListResponse> searchCommentsByAdmin(String keyword, String condition, Pageable pageable);
}
