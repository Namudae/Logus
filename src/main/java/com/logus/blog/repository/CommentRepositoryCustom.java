package com.logus.blog.repository;

import com.logus.blog.dto.CommentListDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface CommentRepositoryCustom {
//    List<CommentResponseDto> selectComments(Long postId);
    Page<CommentListDto> selectBlogComments(Long blogId, Long memberId, String keyword, String condition, Pageable pageable);
}
