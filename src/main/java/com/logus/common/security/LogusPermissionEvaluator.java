package com.logus.common.security;

import com.logus.blog.repository.PostRepository;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import com.logus.member.entity.Member;
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
            throw new CustomException(ErrorCode.UNAUTHORIZED_REQUEST);
        }

        return true;
    }
}
