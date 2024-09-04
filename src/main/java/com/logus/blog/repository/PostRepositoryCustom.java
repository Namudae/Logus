package com.logus.blog.repository;

import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;

import java.util.List;

public interface PostRepositoryCustom {
    List<PostResponseDto> selectAllBlogPosts(String blogAddress);

//    Long createPost(PostRequestDto postRequestDto);
}
