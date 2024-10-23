package com.logus.common.security;

import com.logus.member.entity.Member;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.ArrayList;
import java.util.List;

public class UserPrincipal extends User {

    private final Long memberId;

    public UserPrincipal(Member member) {
        super(member.getLoginId(), member.getPassword(), getAuthorities(member)); // 동적 권한 설정
        this.memberId = member.getId();
    }

    // 권한 목록을 생성하는 정적 메서드
    private static List<SimpleGrantedAuthority> getAuthorities(Member member) {
        List<SimpleGrantedAuthority> authorities = new ArrayList<>(); // 권한 목록 초기화

        // 기본 사용자 권한 추가
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));

        // 사용자의 role 필드가 "ADMIN"인지 확인하여 관리자 권한 추가
        if ("ADMIN".equals(member.getRole())) {
            authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN")); // 관리자 권한 추가
        }

        return authorities; // 설정된 권한 목록 반환
    }

    public Long getMemberId() {
        return memberId;
    }
}

}
