package com.logus.member.entity;

import com.logus.common.entity.Attachment;
import com.logus.common.entity.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 20)
    private String socialType;

    private String accessToken;

    @Column(length = 20, unique = true, nullable = false) // 아이디는 유니크해야 함
    private String loginId;

    @Column(length = 100, nullable = false) // 비밀번호는 필수
    private String password;

    @Column(length = 30)
    private String nickname;

    @Column(length = 50, unique = true, nullable = false) // 이메일도 유니크해야 함
    private String email;

    @Column(length = 100)
    private String introduce;

    private String imgUrl;

    private String role; //ADMIN, USER

    @Column(length = 1)
    private String delYn;

    @Column(length = 15)
    private String phone; // 전화번호(관리자 전용)

    public Member(String loginId) {
        this.loginId = loginId;
    }

    public Member(String loginId, String password) {
        this.loginId = loginId;
        this.password = password;
    }

    public void encodePassword(String password) {
        this.password = password;
    }

    // 중복 체크 메서드
    public boolean isLoginIdDuplicate(String loginId) {
        return this.loginId.equals(loginId);
    }

    public boolean isEmailDuplicate(String email) {
        return this.email.equals(email);
    }
}
