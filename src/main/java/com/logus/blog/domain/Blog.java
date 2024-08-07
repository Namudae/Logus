package com.logus.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
public class Blog {

    @Id @GeneratedValue
    @Column(name = "blog_id")
    private Long id;

    private String blogName;
    private String blogAddress;
    private String introduce;
    private LocalDateTime createdDate;
    private String delYn;

    @OneToMany(mappedBy = "blog")
    private List<BlogMember> blogMembers = new ArrayList<>();
}
