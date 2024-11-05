package com.logus.member.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum SocialType {
    EMAIL("이메일"),
    NAVER("네이버"),
    GOOGLE("구글"),
    KAKAO("카카오");

    private final String title;
}
