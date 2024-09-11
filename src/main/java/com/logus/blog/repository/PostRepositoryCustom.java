package com.logus.blog.repository;

import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;
import com.logus.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface PostRepositoryCustom {
    Page<PostResponseDto> selectAllBlogPosts(String blogAddress, Pageable pageable);

    Page<PostResponseDto> searchBlogPosts(String blogAddress, String keyword, Pageable pageable);
//    List<Post> findByContentContaining(Long id);

//    Long createPost(PostRequestDto postRequestDto);
}
