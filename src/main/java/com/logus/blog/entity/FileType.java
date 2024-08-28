package com.logus.blog.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum FileType {
    PHOTO("사진"),
    VIDEO("영상"),
    DOCUMENT("파일"),
    PROFILE("프로필 사진");

    private final String title;
}
