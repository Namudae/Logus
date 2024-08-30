package com.logus.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter 
public class Blog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private Long id;

    @Column(length = 20)
    private String blogName;

    private String blogAddress;

    @Column(length = 100)
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
