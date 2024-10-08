package com.logus.admin.entity;


import com.logus.member.entity.Member;
import jakarta.persistence.*;
import lombok.Getter;

import java.time.LocalDateTime;

@Entity
@Getter
public class Notice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notice_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
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
