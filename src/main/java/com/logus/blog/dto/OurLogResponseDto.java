package com.logus.blog.dto;

import lombok.*;

import static com.logus.common.service.S3Service.CLOUD_FRONT_DOMAIN_NAME;

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

    public void changeImgUrl() {
        if (this.imgUrl != null) {
            this.imgUrl = CLOUD_FRONT_DOMAIN_NAME + "/" + this.imgUrl;
        }
    }
}
