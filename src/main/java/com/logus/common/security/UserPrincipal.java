package com.logus.common.security;

import com.logus.member.entity.Member;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.List;

public class UserPrincipal extends User {

    private final Long memberId;

    public UserPrincipal(Member member) {
        super(member.getLoginId(), member.getPassword(),
                List.of(
                        new SimpleGrantedAuthority("ROLE_USER")
                ));
        this.memberId = member.getId();
    }

    public Long getMemberId() {
        return memberId;
    }
}
