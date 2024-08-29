package com.logus.blog.entity;

import com.logus.admin.entity.Status;
import com.logus.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter

public class Post {

    @Id
    @GeneratedValue
    @Column(name = "post_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Blog blog;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "series_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Series series;

    @Column(length = 100)
    private String title;

    @Column(length = 10000)
    private String content;

    private Long count;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, columnDefinition = "varchar(20)")
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
