package com.logus.blog.repository;

import java.util.List;

public interface PostTagRepositoryCustom {
    List<String> selectPostTag(Long postId);

}
