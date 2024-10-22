package com.logus.blog.entity;

import com.logus.blog.dto.BlogRequestDto;
import com.logus.common.entity.BaseCreateTime;
import com.logus.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BlogMember extends BaseCreateTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_member_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Blog blog;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, columnDefinition = "varchar(20)")
    private BlogAuth blogAuth;


    //==비즈니스 로직==//
    //블로그 정보 수정
    public void updateBlogAuth(BlogAuth blogAuth) {
        this.blogAuth = blogAuth;
    }

}
