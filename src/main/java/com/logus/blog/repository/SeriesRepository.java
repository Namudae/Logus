package com.logus.blog.repository;

import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Series;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeriesRepository extends JpaRepository<Series, Long> {
    List<Series> findByBlogId(Long blogId);
}
