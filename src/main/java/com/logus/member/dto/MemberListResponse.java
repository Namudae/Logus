package com.logus.member.dto;

import com.logus.blog.dto.FollowerResponseDto;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class MemberListResponse {

    private Long memberId;
    private String loginId;
    private String nickname;
    private BlogResponse blogResponse;
//    private Long blogId;
//    private String blogName;
//    private String blogAddress;

    @Getter
    @Setter
    public static class BlogResponse {
        private Long blogId;
        private String blogName;
        private String blogAddress;
    }

}
