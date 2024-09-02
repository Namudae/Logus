package com.logus.member.repository;


import com.logus.member.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

//@Repository
//@RequiredArgsConstructor
public interface MemberRepository extends JpaRepository<Member, Long> {

}
