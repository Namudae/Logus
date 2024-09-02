package com.logus.admin.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReportStatus {

    BLIND("임시 숨김 처리"),
    BLOCK("차단"),
    RETURN("반려");

    private final String title;
}
