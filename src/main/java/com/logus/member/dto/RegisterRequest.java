package com.logus.member.dto;

import com.logus.blog.dto.BlogMemberRequestDto;
import com.logus.blog.dto.BlogRequestDto;
import com.logus.member.entity.Member;
import com.logus.member.entity.SocialType;
import jakarta.validation.Valid;
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
public class RegisterRequest {

//    @NotBlank(message = "아이디를 입력하세요.")
//    @Size(min=5, max=20, message = "아이디를 5~20자로 입력해주세요.")
//    @Pattern(regexp = "^[가-힣a-z0-9\\s-_]+$", message = "아이디를 영문 소문자, 숫자와 특수기호(-),(_)만 사용하여 입력해주세요.")
    @Pattern(regexp = "^[a-z0-9\\s-_]{5,20}$", message = "영문 소문자, 숫자와 특수기호(-),(_)만 사용하여 5~20자로 입력해 주세요.")
    private String loginId;

//    @NotBlank(message = "닉네임을 입력하세요.")
    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s-_]{1,20}$", message = "한글, 영문, 숫자, 특수기호(-),(_)를 사용하여 1~20자로 입력해 주세요.")
    private String nickname;

    private String imgUrl;

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d|.*[^A-Za-z\\d])[A-Za-z\\d!@#$%^&*()-_=+|{};:,<.>?]{8,16}$",
            message = "비밀번호는 영문 대/소문자, 숫자, 특수문자 중 두 가지 이상을 조합하여 8~16자로 입력해 주세요."
    )
    private String password;

    private String email;

    private String socialType;

    @Valid
    private BlogRequestDto blogRequestDto;

    public Member toEntity(String imgUrl) {
        return Member.builder()
                .loginId(loginId)
                .nickname(nickname)
                .email(email)
                .imgUrl(imgUrl)
                .password(password)
                .role("USER")
                .socialType(socialType)
                .build();
    }

}
