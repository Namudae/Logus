package com.logus.blog.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum BlogAuth {
    OWNER("소유자"),
    ADMIN("관리자"),
    EDITOR("편집자");

    private final String title;
}
