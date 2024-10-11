package com.logus.common.security;

import com.logus.blog.repository.PostRepository;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.PermissionEvaluator;
import org.springframework.security.core.Authentication;

import java.io.Serializable;

@Slf4j
@RequiredArgsConstructor
public class LogusPermissionEvaluator implements PermissionEvaluator {

    private final PostRepository postRepository;

    @Override
    public boolean hasPermission(Authentication authentication, Object targetDomainObject, Object permission) {
        return false;
    }

    @Override
    public boolean hasPermission(Authentication authentication, Serializable targetId, String targetType, Object permission) {
        var userPrincipal = (UserPrincipal) authentication.getPrincipal();

        var post = postRepository.findById((Long) targetId)
                .orElseThrow(() -> new CustomException(ErrorCode.POST_NOT_FOUND));

        if (!post.getMember().getId().equals(userPrincipal.getMemberId())) {
            log.error("[인가실패] 해당 사용자가 작성한 글이 아닙니다. targetId={}", targetId);
//            new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
            return false;
        }

        return true;
    }
}
