package com.logus.blog.repository;

import com.logus.blog.entity.BlogMember;
import com.logus.blog.entity.Likey;
import com.logus.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface LikeyRepository extends JpaRepository<Likey, Long> {

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Likey l WHERE l.post.id = :postId")
    void bulkDeleteByPostId(Long postId);

    Optional<Likey> findByMemberIdAndPostId(Long memberId, Long postId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("DELETE FROM Likey l WHERE l.member.id = :memberId")
    void bulkDeleteByMemberId(Long memberId);
}
