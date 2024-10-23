package com.logus.blog.repository;

import com.logus.blog.entity.Follow;
import com.logus.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface FollowRepository extends JpaRepository<Follow, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Follow f WHERE f.blog.id = :blogId")
    void bulkDeleteByBlogId(Long blogId);
}
