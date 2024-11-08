package com.logus.blog.dto;

import jakarta.annotation.PostConstruct;
import lombok.*;

import java.util.List;

import static com.logus.common.service.S3Service.CLOUD_FRONT_DOMAIN_NAME;

@AllArgsConstructor
@RequiredArgsConstructor
@Data
@Builder
public class MainGridResponse {

    private Long categoryId;
    private String categoryName;
    private List<PostDto> postList;

    @Getter
    @Setter
    public static class PostDto {
        private Long postId;
//        private Long blogId;
        private String imgUrl;
        private String title;
        private String content; //50자
        private Long views;
        private Long commentCount;
        private Long likeCount;

        // imgUrl을 처리하는 메서드
        public void processImgUrl() {
            if (this.imgUrl != null) {
                this.imgUrl = CLOUD_FRONT_DOMAIN_NAME + "/" + this.imgUrl;
            }
        }
    }
}
