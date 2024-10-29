package com.logus.blog.repository;

import com.logus.blog.dto.BlogResponseDto;
import com.logus.blog.entity.Blog;
import com.logus.blog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlogRepository extends JpaRepository<Blog, Long>, BlogRepositoryCustom {
    Optional<Blog> findByBlogAddress(String blogAddress);
    boolean existsByBlogAddress(String blogAddress);

}
