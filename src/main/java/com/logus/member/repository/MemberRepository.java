package com.logus.member.repository;


import com.logus.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

//@Repository
//@RequiredArgsConstructor
public interface MemberRepository extends JpaRepository<Member, Long> {
    Optional<Member> findByLoginId(String loginId);
}
