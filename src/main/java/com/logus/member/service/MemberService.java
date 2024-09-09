package com.logus.member.service;

import com.logus.blog.entity.Category;
import com.logus.member.entity.Member;
import com.logus.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    public Member getById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("USER NOT FOUND"));
        return member;
    }

    public Member getReferenceById(Long memberId) {
        return memberId == null ? null :
                memberRepository.getReferenceById(memberId);
    }

}
