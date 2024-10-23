package com.logus.blog.repository;

import com.logus.blog.entity.BlogMember;
import com.logus.blog.entity.Likey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface LikeyRepository extends JpaRepository<Likey, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Likey l WHERE l.post.id = :postId")
    void bulkDeleteByPostId(Long postId);
}
