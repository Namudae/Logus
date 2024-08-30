package com.logus.blog.entity;

import com.logus.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Getter
public class BlogMember {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Blog blog;

    @Column(length = 1)
    private String masterYn;

}
