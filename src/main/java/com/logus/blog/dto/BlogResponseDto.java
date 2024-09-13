package com.logus.blog.dto;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class BlogResponseDto {

    private String blogName;
    private String blogAddress;
    private String introduce;
    private String shareYn;

}
