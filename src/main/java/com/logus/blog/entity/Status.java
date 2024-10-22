package com.logus.blog.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Status {

    PUBLIC("공개"),
    SECRET("비밀글"),
    TEMPORARY("임시저장");

    private final String title;
}
