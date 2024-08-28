package com.logus.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class BlogMember {

    @Id @GeneratedValue
    @Column(name = "blog_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @Column(length = 1)
    private String masterYn;


    //==연관관계 편의 메서드==
    public void setMember(Member member) {
        this.member = member;
    }

    public void setBlog(Blog blog) {
        this.blog = blog;
    }

}
