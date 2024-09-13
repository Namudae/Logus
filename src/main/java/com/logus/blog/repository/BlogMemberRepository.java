package com.logus.blog.repository;

import com.logus.blog.entity.Blog;
import com.logus.blog.entity.BlogMember;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlogMemberRepository extends JpaRepository<BlogMember, Long> {
}
