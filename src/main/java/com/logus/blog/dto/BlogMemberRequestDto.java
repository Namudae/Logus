package com.logus.blog.dto;

import com.logus.blog.entity.Blog;
import com.logus.blog.entity.BlogAuth;
import com.logus.blog.entity.BlogMember;
import com.logus.member.entity.Member;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BlogMemberRequestDto {

    private Long memberId;
    private BlogAuth blogAuth;

    public BlogMember toEntity(Member member, Blog blog) {
        return BlogMember.builder()
                .member(member)
                .blog(blog)
                .blogAuth(blogAuth)
                .build();
    }
}
