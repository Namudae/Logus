package com.logus.admin.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ReportStatus {

    //HIDE
    H("임시 숨김 처리"),
    //BLOCK
    B("차단"),
    //RETURN
    R("반려");


    private final String title;
}
