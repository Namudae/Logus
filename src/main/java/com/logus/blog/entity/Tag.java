package com.logus.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Tag {

    @Id
    @GeneratedValue
    @Column(name = "tag_id")
    private Long id;

    @Column(length = 30)
    private String tagName;

//    @OneToMany(mappedBy = "tag")
//    private List<PostTag> postTags = new ArrayList<>();
}
