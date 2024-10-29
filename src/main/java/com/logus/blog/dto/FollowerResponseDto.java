package com.logus.blog.dto;

import lombok.*;

import java.util.List;

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
}
