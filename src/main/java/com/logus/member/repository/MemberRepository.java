package com.logus.member.repository;


import com.logus.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    @Query("SELECT m FROM Member m WHERE m.email = :email AND m.role = 'USER'")
    Optional<Member> findByEmail(String email);
}
