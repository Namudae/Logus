package com.logus.blog.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Status {
    //최대 20자
    //처리로인한 숨김처리, 운영자에 의한 처리(삭제or삭제x),
    BLIND("임시 숨김 처리"),
    BLOCKED("차단"),
    RETURNED("반려");

    private final String title;
}
