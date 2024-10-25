package com.logus.blog.repository;

import com.logus.blog.entity.BlogMember;
import com.logus.blog.entity.Visit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface VisitRepository extends JpaRepository<Visit, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Visit v WHERE v.blog.id = :blogId")
    void bulkDeleteByBlogId(Long blogId);
}
