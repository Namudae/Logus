package com.logus.blog.repository;

import com.logus.blog.entity.Post;
import com.logus.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Long> {
    boolean existsByTagName(String tagName);

    Tag findByTagName(String tagName);
}
