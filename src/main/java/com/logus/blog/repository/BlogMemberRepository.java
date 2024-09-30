package com.logus.blog.repository;

import com.logus.blog.dto.BlogMemberResponseDto;
import com.logus.blog.entity.Blog;
import com.logus.blog.entity.BlogMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogMemberRepository extends JpaRepository<BlogMember, Long> {
    List<BlogMemberResponseDto> findByBlogId(Long blogId);
}
