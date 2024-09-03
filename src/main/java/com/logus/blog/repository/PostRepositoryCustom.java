package com.logus.blog.repository;

import com.logus.blog.dto.PostRequestDto;
import com.logus.blog.dto.PostResponseDto;

import java.util.List;

public interface PostRepositoryCustom {
    List<PostResponseDto> selectAllPosts(String blogAddress);

//    Long createPost(PostRequestDto postRequestDto);
}
