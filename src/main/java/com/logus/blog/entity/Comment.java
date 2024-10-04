package com.logus.blog.entity;

import com.logus.admin.entity.ReportStatus;
import com.logus.common.entity.BaseTime;
import com.logus.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Comment extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Post post;

    //계층형
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Comment parent;

    @OneToMany(mappedBy = "parent")
    private List<Comment> child = new ArrayList<>();

    private Byte depth;

    @Column(length = 500)
    private String content;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, columnDefinition = "varchar(20)")
    private Status status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, columnDefinition = "varchar(20)")
    private ReportStatus reportStatus;

    //==비즈니스 로직==//
    public void delComment() {
        this.status = Status.DELETE;
    }

}
