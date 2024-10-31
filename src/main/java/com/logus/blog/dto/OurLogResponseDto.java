package com.logus.blog.dto;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class OurLogResponseDto {

    private Long blogId;
    private String blogName;
    private String blogAddress;
    private String introduce;
    private String shareYn;
    private Long memberId;
    private String nickname;
    private String imgUrl;
}
