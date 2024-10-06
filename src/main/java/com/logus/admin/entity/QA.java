package com.logus.admin.entity;

import com.logus.common.entity.BaseTime;
import com.logus.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QA extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "qa_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 질문한 회원 정보

    @Column(length = 100)
    private String title; // 질문 제목

    @Column(length = 500)
    private String content; // 질문 or 답변 내용

    private int views; // 조회수

    @Column(length = 1)
    private String status; // 질문 상태

    @Column(length = 20)
    private String adminName; // 답변한 관리자 이름

    @Column(length = 1)
    private String qOrA; // Q or A
}
