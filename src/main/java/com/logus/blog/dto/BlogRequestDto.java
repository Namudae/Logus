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

//    @NotBlank(message = "블로그명을 입력하세요.")
//    @Size(max=20, message = "블로그명을 20자 이내로 작성해 주세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s-_]{1,20}$", message = "한글, 영문, 숫자, 특수문자(-, _)를 사용하여 1~20자로 입력해 주세요.")
    private String blogName;

//    @NotBlank(message = "블로그 주소를 입력하세요.")
//    @Size(min=4, max=30, message = "블로그 주소 4-32자를 입력해 주세요.")
    @Pattern(regexp = "^[a-z0-9-]{4,32}$", message = "영문 소문자, 숫자, 특수문자(-)를 사용하여 4~32자로 입력해 주세요.")
    private String blogAddress;

    @Size(max=600, message = "블로그 소개를 100자 이내로 작성해 주세요.")
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
