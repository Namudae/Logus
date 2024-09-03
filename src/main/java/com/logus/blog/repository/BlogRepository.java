package com.logus.blog.repository;

import com.logus.blog.entity.Blog;
import com.logus.blog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogRepository extends JpaRepository<Blog, Long> {
}
