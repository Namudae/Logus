package com.logus.blog.repository;

import com.logus.blog.dto.BlogMemberResponseDto;
import com.logus.blog.entity.Blog;
import com.logus.blog.entity.BlogMember;
import com.logus.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BlogMemberRepository extends JpaRepository<BlogMember, Long> {
    List<BlogMember> findByBlogId(Long blogId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM BlogMember bm WHERE bm.blog.id = :blogId")
    void bulkDeleteByBlogId(Long blogId);

    BlogMember findByBlogAndMember(Blog blog, Member member);

}
