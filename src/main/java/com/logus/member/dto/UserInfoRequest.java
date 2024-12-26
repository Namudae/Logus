package com.logus.member.dto;

import com.logus.member.entity.Member;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class UserInfoRequest {

    private String loginId;
    private String password;

    private String email;

    @Pattern(regexp = "^[가-힣a-zA-Z0-9\\s-_]{1,20}$", message = "한글, 영문, 숫자, 특수기호(-),(_)를 사용하여 1~20자로 입력해 주세요.")
    private String nickname;

    @Pattern(
            regexp = "^(?=.*[A-Za-z])(?=.*\\d|.*[^A-Za-z\\d])[A-Za-z\\d!@#$%^&*()-_=+|{};:,<.>?]{8,16}$",
            message = "비밀번호는 영문 대/소문자, 숫자, 특수문자 중 두 가지 이상을 조합하여 8~16자로 입력해 주세요."
    )
    private String newPassword;
}
