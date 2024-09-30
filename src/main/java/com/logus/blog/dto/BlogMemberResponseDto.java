package com.logus.blog.dto;

import com.logus.blog.entity.BlogAuth;
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

}
