package com.logus.admin.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReportType {
    COMMERCIAL("영리목적/홍보"),
    ILLEGAL("불법"),
    OBSCENE("음란성/선정성"),
    PERSONAL_INF("개인정보 노출"),
    SPAM("도배"),
    OTHER("기타");

    private final String title;

}
