package com.logus.blog.repository;

import com.logus.blog.entity.Comment;
import com.logus.blog.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long>, CommentRepositoryCustom {
    List<Comment> findByPostId(Long postId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Comment c WHERE c.post.id = :postId")
    void bulkDeleteByPostId(Long postId);
}
