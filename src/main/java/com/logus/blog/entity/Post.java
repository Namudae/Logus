package com.logus.blog.entity;

import com.logus.admin.entity.ReportStatus;
import com.logus.common.entity.Attachment;
import com.logus.common.entity.BaseTime;
import com.logus.member.entity.Member;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Post extends BaseTime {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    private Long views;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, columnDefinition = "varchar(20)")
    private ReportStatus reportStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, columnDefinition = "varchar(20)")
    private Status status;

    public Post(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member; //양방향 연관관계X
    }

    //양방향 연관관계
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostTag> postTags = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Attachment> attachments = new ArrayList<>();

//    @OneToMany(mappedBy = "post")
//    private List<Likey> likeys = new ArrayList<>();
//
//    @OneToMany(mappedBy = "post")
//    private List<Alarm> alarms = new ArrayList<>();

}
