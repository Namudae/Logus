package com.logus.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter

public class Post {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id")
    private Series series;

    @Column(length = 100)
    private String title;

    @Column(length = 10000)
    private String content;

    private Long count;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private Status status;

    @Column(length = 1)
    private String saveYn;

    @Column(length = 1)
    private String secretYn;

    @Column(length = 1)
    private String delYn;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

//    @OneToMany(mappedBy = "post")
//    private List<Comment> comments = new ArrayList<>();
//
//    @OneToMany(mappedBy = "post")
//    private List<PostTag> postTags = new ArrayList<>();
//
//    @OneToMany(mappedBy = "post")
//    private List<Likey> likeys = new ArrayList<>();
//
//    @OneToMany(mappedBy = "post")
//    private List<Alarm> alarms = new ArrayList<>();

}
