package com.logus.member.dto;

import com.logus.member.entity.Member;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class MemberSearchResponse {

    private Long memberId;
    private String nickname;
    private String imgUrl;

}
