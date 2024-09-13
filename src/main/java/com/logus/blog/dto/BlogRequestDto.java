package com.logus.blog.dto;

import com.logus.blog.entity.*;
import com.logus.member.entity.Member;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BlogRequestDto {

    private String blogName;
    private String blogAddress;
    private String introduce;
    private String shareYn;
    private List<BlogMemberRequestDto> blogMembers;

    /*
    Dto -> toEntity
     */
    public Blog toEntity() {
        return Blog.builder()
                .blogName(blogName)
                .blogAddress(blogAddress)
                .introduce(introduce)
                .shareYn(shareYn)
                .build();
    }

}
