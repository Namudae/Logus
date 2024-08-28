package com.logus.blog.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Notice {

    @Id
    @GeneratedValue
    @Column(name = "notice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(length = 100)
    private String title;

    @Column(length = 10000)
    private String content;

    private Long count;

    @Column(length = 1)
    private String delYn;

    private LocalDateTime createDate;

    private LocalDateTime updateDate;

}
