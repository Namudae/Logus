package com.logus.common.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AttachmentType {
    IMAGE("사진", "images"),
    THUMB("썸네일", "thumbnail"),
    SERIES("시리즈", "series"),
    TEMP("사진임시저장", "temporary"),
    VIDEO("영상", "video"),
    DOCUMENT("파일", "document"),
    PROFILE("프로필 사진", "profile"),
    OTHER("기타", "generals");

    private final String title;
    private final String path;
}
