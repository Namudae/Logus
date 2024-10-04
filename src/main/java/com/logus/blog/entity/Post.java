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

    private String imgUrl;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, columnDefinition = "varchar(20)")
    private ReportStatus reportStatus;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, columnDefinition = "varchar(20)")
    private Status status;

    //양방향 연관관계
    @OneToMany(mappedBy = "post")
    private List<Comment> comments = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<PostTag> postTags = new ArrayList<>();

    @OneToMany(mappedBy = "post")
    private List<Attachment> attachments = new ArrayList<>();
    //영속성 전이(X)
//    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
//    private List<Attachment> attachments = new ArrayList<>();

    //default
    @PrePersist
    public void prePersist() {
        if (this.views == null) {
            this.views = 0L;
        }
    }

    //임시
    public Post(String title, String content, Member member) {
        this.title = title;
        this.content = content;
        this.member = member; //양방향 연관관계X
    }

    //==비즈니스 로직==//
    //게시글 수정
    public void updatePost(Category category, Series series, String title, String content, Status status) {
        this.category = category;
        this.series = series;
        this.title = title;
        this.content = content;
        this.status = status;
    }

    //게시글 삭제
    public void deletePost() {
        this.status = Status.DELETE;
    }

    public void deleteImgUrl() {
        this.imgUrl = null;
    }

    public void changeImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    //조회수 증가
    public void addViews(Long views) {
        this.views = views;
    }

    //    @OneToMany(mappedBy = "post")
//    private List<Likey> likeys = new ArrayList<>();
//
//    @OneToMany(mappedBy = "post")
//    private List<Alarm> alarms = new ArrayList<>();

}
