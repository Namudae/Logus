package com.logus.blog.repository;

import com.logus.blog.dto.BlogResponseDto;

public interface BlogRepositoryCustom {
    BlogResponseDto selectBlogInfo(String blogAddress);
}
