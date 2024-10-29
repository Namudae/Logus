package com.logus.blog.dto;

import lombok.*;

import java.time.LocalDateTime;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class FollowResponseDto {

    private Long followId;
    private Long blogId;
    private String blogName;
    private String blogAddress;
    private String introduce;

}
