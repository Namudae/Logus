package com.logus.member.repository;

import com.logus.member.dto.MemberListResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<MemberListResponse> searchMembers(String loginId, String nickname, String blogName, String blogAddress, Pageable pageable);
}
