package com.logus.blog.dto;

import com.logus.blog.entity.BlogAuth;
import com.logus.blog.entity.BlogMember;
import com.logus.member.entity.Member;
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
    private String imgUrl;
    private String myLogAddress;

}
