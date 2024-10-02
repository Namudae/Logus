package com.logus.blog.dto;

import com.logus.blog.entity.Blog;
import lombok.*;

import java.util.List;

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
    private List<String> series;
    private List<BlogMemberResponseDto> blogMembers;

//    public toEntity(Blog blog, List<String> series)

}
