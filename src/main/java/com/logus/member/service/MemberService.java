package com.logus.member.service;

import com.logus.blog.entity.Category;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.common.security.JwtService;
import com.logus.member.entity.Member;
import com.logus.member.repository.MemberRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberService {

    @Autowired
    private JwtService jwtService;

    private final MemberRepository memberRepository;

    public Member getById(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return member;
    }

    public Member getReferenceById(Long memberId) {
        return memberId == null ? null :
                memberRepository.getReferenceById(memberId);
    }

    public Long findIdByLoginId(String loginId) {
        if (loginId == null) {
            return null;
        }
        Member member = memberRepository.findByLoginId(loginId)
                .orElseThrow(() -> new CustomException(ErrorCode.MEMBER_NOT_FOUND));
        return member.getId();
    }

    public Long getMemberIdFromJwt(HttpServletRequest request) {
        return findIdByLoginId(jwtService.extractUsername(jwtService.getJwt(request)));
    }

    public boolean isAuthor(Long loginId, Long authorId) {
        return loginId.equals(authorId);
    }

}
