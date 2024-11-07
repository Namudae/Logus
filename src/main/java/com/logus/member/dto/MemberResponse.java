package com.logus.member.dto;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class MemberResponse {

    private Long memberId;
    private String loginId;
    private String nickname;
    private String imgUrl;
    private String blogAddress;
    private String email;
    private String jwtToken;
}
