package com.logus.blog.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.logus.blog.entity.BlogAuth;
import com.logus.blog.entity.BlogMember;
import lombok.*;

import java.time.LocalDateTime;

import static com.logus.common.service.S3Service.CLOUD_FRONT_DOMAIN_NAME;

@RequiredArgsConstructor
@Getter
public class BlogMemberShortResponse {

    private Long memberId;
    private String nickname;
    private String imgUrl;

    @Builder
    public BlogMemberShortResponse(BlogMember blogMember) {
        this.memberId = blogMember.getMember().getId();
        this.nickname = blogMember.getMember().getNickname();
        this.imgUrl = blogMember.getMember().getImgUrl();
        if (this.imgUrl != null) {
            this.imgUrl = CLOUD_FRONT_DOMAIN_NAME + "/" + this.imgUrl;
        }
    }
}
