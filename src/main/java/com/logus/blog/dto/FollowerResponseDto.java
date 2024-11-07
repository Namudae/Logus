package com.logus.blog.dto;

import lombok.*;

import java.util.List;

import static com.logus.common.service.S3Service.CLOUD_FRONT_DOMAIN_NAME;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class FollowerResponseDto {

    private Long followId;
    private Long memberId;
    private String nickname;
    private String imgUrl;
    //블로그 기본, 참여n개 조회... shareYn, blogId, blogName, blogAdderss
    private List<BlogDto> blogList;

    @Getter
    @Setter
    public static class BlogDto {
        private Long blogId;
        private String blogName;
        private String blogAddress;
        private String shareYn;
    }

    // imgUrl 가공 메서드
    public void processImgUrl() {
        if (this.imgUrl != null) {
            this.imgUrl = CLOUD_FRONT_DOMAIN_NAME + "/" + this.imgUrl;
        }
    }

}
