package com.logus.admin.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReportStatus {

    PENDING("미처리"), //미처리
    BLIND("임시 숨김 처리"), //미처리
    BLOCK("차단"), //처리
    DELETE("삭제"), //처리
    RETURN("반려"); //처리

    private final String title;
}
