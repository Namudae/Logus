package com.logus.member.entity;

import com.logus.blog.entity.File;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    //프로필 사진
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "file_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private File file;

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

    @Column(length = 1)
    private String delYn;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

    public Member(String loginId) {
        this.loginId = loginId;
    }

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
//
//    @OneToMany(mappedBy = "member")
//    private List<File> files = new ArrayList<>();
}
