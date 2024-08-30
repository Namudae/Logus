package com.logus.blog.repository;

import com.logus.blog.dto.PostDto;

import java.util.List;

public interface PostRepositoryCustom {
    List<PostDto.PostResponse> getAllPosts(String blogAddress);
}
