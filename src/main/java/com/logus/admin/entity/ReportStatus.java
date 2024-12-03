package com.logus.admin.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReportStatus {

    //처리 전
    PENDING("미처리"),
    BLIND("임시 숨김 처리"),

    //처리 후
    BLOCK("차단"),
    DELETE("삭제"),
    RETURN("반려");

    private final String title;
}
