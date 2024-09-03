package com.logus.blog.repository;

import com.logus.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Post, Long> {
    boolean existsByTagName(String tagName);
}
