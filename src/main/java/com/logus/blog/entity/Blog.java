package com.logus.blog.entity;

import com.logus.common.entity.BaseCreateTime;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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

    public void setDelYn(String delYn) {
        this.delYn = delYn;
    }

    @PrePersist
    public void prePersist() {
        if (this.delYn == null) {
            this.delYn = "N";
        }
    }

    //    @OneToMany(mappedBy = "blog")
//    private List<BlogMember> blogMembers = new ArrayList<>();
//
//    @OneToMany(mappedBy = "blog")
//    private List<Visit> visits = new ArrayList<>();
//
//    @OneToMany(mappedBy = "blog")
//    private List<Follow> follows = new ArrayList<>();
//
//    @OneToMany(mappedBy = "blog")
//    private List<Series> series = new ArrayList<>();
//
//    @OneToMany(mappedBy = "blog")
//    private List<Post> posts = new ArrayList<>();

}
