package com.logus.blog.repository;

import com.logus.blog.entity.Category;
import com.logus.blog.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoryRepository extends JpaRepository<Category, Long> {
}
