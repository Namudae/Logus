package com.logus.blog.entity;

import com.logus.blog.dto.BlogRequestDto;
import com.logus.common.entity.BaseCreateTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Blog extends BaseCreateTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "blog_id")
    private Long id;

    @Column(length = 20)
    private String blogName;

    private String blogAddress;

    @Column(length = 100)
    private String introduce;

    @Column(length = 1)
    private String shareYn;

    @Column(length = 1)
    private String delYn;

    @OneToMany(mappedBy = "blog")
    private List<Series> series = new ArrayList<>();

    @OneToMany(mappedBy = "blog")
    private List<BlogMember> blogMembers = new ArrayList<>();

    public void setDelYn(String delYn) {
        this.delYn = delYn;
    }

    @PrePersist
    public void prePersist() {
        if (this.delYn == null) {
            this.delYn = "N";
        }
    }

    //==비즈니스 로직==//
    //블로그 정보 수정
    public void updateBlogInfo(BlogRequestDto blogRequestDto) {
        this.blogName = blogRequestDto.getBlogName();
        this.blogAddress = blogRequestDto.getBlogAddress();
        this.introduce = blogRequestDto.getIntroduce();
//        this.blogMembers = blogMembers;
        // 기존 멤버 삭제 및 새로운 멤버 추가
//        this.blogMembers.clear(); // 기존 멤버 삭제
//        this.blogMembers.addAll(blogMembers); // 새로운 멤버 추가
    }


//    @OneToMany(mappedBy = "blog")
//    private List<Visit> visits = new ArrayList<>();
//
//    @OneToMany(mappedBy = "blog")
//    private List<Follow> follows = new ArrayList<>();
//
//    @OneToMany(mappedBy = "blog")
//    private List<Post> posts = new ArrayList<>();

}
