package com.logus.member.repository;


import com.logus.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long>, MemberRepositoryCustom {
    Optional<Member> findByLoginId(String loginId);

    boolean existsByLoginId(String loginId);

    Optional<Member> findByEmail(String email);
}
