package com.logus.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter @Setter
public class Blog {

    @Id @GeneratedValue
    @Column(name = "blog_id")
    private Long id;

    private String blogName;

    private String blogAddress;

    private String introduce;

    private LocalDateTime createDate;

    @Column(length = 1)
    private String shareYn;

    @Column(length = 1)
    private String delYn;

//    @OneToMany(mappedBy = "blog")
//    private List<BlogMember> blogMembers = new ArrayList<>();
//
//    @OneToMany(mappedBy = "blog")
//    private List<Visit> visits = new ArrayList<>();
//
//    @OneToMany(mappedBy = "blog")
//    private List<Follow> follows = new ArrayList<>();
//
//    @OneToMany(mappedBy = "blog")
//    private List<Series> series = new ArrayList<>();
//
//    @OneToMany(mappedBy = "blog")
//    private List<Post> posts = new ArrayList<>();

}
