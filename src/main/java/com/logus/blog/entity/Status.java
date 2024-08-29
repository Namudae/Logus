package com.logus.blog.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Status {
    /**
     * Public: 공개
     * Secret: 비밀글
     * NotSaved: 임시저장
     * Delete: 삭제된 글
     */

    P,
    S,
    N,
    D;
    
}
