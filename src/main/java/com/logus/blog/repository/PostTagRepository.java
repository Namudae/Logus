package com.logus.blog.repository;

import com.logus.blog.entity.PostTag;
import com.logus.blog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostTagRepository extends JpaRepository<PostTag, Long> {
}
