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
    private List<BlogMemberResponseDto> members;
    private List<SeriesResponseDto> series;

    public BlogResponseDto(Blog blog, List<BlogMemberResponseDto> blogMembers, List<SeriesResponseDto> series) {
        this.blogName = blog.getBlogName();
        this.blogAddress = blog.getBlogAddress();
        this.introduce = blog.getIntroduce();
        this.shareYn = blog.getShareYn();
        this.members = blogMembers;
        this.series = series;
    }

}
