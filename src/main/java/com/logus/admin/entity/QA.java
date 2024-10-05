package com.logus.admin.entity;

import com.logus.common.entity.BaseTime;
import com.logus.member.entity.Member;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
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
    private String content; // 질문 내용

    private int views; // 조회수

    @Column(length = 1)
    private String status; // 질문 상태 (예: P - 공개, H - 비공개)

    @Embedded
    private Comment comment; // 답변 정보

    @Embeddable
    @Getter
    @Setter
    public static class Comment {
        @Column(length = 20)
        private String adminName; // 답변한 관리자 이름

        @Column(length = 500)
        private String content; // 답변 내용
    }
}
