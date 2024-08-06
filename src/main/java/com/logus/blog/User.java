package com.logus.blog;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter @Setter
public class User {

    @Id @GeneratedValue
    private Long id;
    private String login_id;
    private String password;
    private String nickname;
    private String email;
    private String introduction;
    private String role;
    private String profile_pic;
    private String del_yn;
    private String created_date;
}
