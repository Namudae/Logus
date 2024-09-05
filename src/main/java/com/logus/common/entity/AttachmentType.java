package com.logus.common.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum AttachmentType {
    IMAGE("사진"),
    VIDEO("영상"),
    DOCUMENT("파일"),
    PROFILE("프로필 사진");

    private final String title;
}
