package com.logus.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Member {

    @Id @GeneratedValue
    @Column(name = "member_id")
    private Long id;

    private String socialType;

    private String accessToken;

    private String loginId;

    private String password;

    private String nickname;

    private String email;

    private String introduce;

    @Enumerated(EnumType.STRING)
    private Role role;

    private String profilePic;

    @Column(length = 1)
    private String delYn;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

//    @OneToMany(mappedBy = "member")
//    private List<BlogMember> blogMembers = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    private List<Visit> visits = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    private List<Follow> follows = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    private List<Post> posts = new ArrayList<>();
//
//    @OneToMany(mappedBy = "member")
//    private List<Comment> comments = new ArrayList<>();
}
