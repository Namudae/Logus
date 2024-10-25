package com.logus.blog.dto;

import com.logus.blog.entity.*;
import com.logus.member.entity.Member;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class BlogRequestDto {

    @NotBlank(message = "블로그명을 입력하세요.")
    @Size(max=20, message = "블로그명을 20자 이내로 작성해 주세요.")
    private String blogName;

    @NotBlank(message = "블로그 주소를 입력하세요.")
    @Size(max=30, message = "블로그 주소를 30자 이내로 작성해 주세요.")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "영어, 숫자, 언더바(_)만 사용 가능합니다.")
    private String blogAddress;

    @Size(max=100, message = "블로그 소개를 100자 이내로 작성해 주세요.")
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
