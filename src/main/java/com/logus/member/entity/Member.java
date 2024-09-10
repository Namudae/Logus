package com.logus.member.entity;

import com.logus.common.entity.Attachment;
import com.logus.common.entity.BaseTime;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(length = 20)
    private String socialType;

    private String accessToken;

    @Column(length = 20)
    private String loginId;

    @Column(length = 100)
    private String password;

    @Column(length = 30)
    private String nickname;

    @Column(length = 50)
    private String email;

    @Column(length = 100)
    private String introduce;

    private String imgUrl;

    @Column(length = 1)
    private String delYn;

    public Member(String loginId) {
        this.loginId = loginId;
    }

}
