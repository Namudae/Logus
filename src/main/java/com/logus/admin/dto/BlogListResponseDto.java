package com.logus.admin.dto;

import com.logus.blog.dto.BlogMemberResponseDto;
import com.logus.blog.entity.Blog;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class BlogListResponseDto {

    private Long blogId;
    private String blogName;
    private String blogAddress;
    private String loginId;

//    private List<BlogMemberResponseDto> members;

    public BlogListResponseDto(Blog blog) {
        this.blogName = blog.getBlogName();
        this.blogAddress = blog.getBlogAddress();
    }
}
