package com.logus.blog.dto;

import com.logus.blog.entity.BlogAuth;
import com.logus.blog.entity.BlogMember;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class BlogMemberResponseDto {

    private Long memberId;
    private String nickname;
    private BlogAuth blogAuth;

    public static BlogMemberResponseDto fromEntity(BlogMember blogMember) {
        return BlogMemberResponseDto.builder()
                .memberId(blogMember.getMember().getId())
                .nickname(blogMember.getMember().getNickname())
                .blogAuth(blogMember.getBlogAuth())
                .build();
    }

}
