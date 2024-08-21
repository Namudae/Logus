package com.logus.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Tag {

    @Id
    @GeneratedValue
    @Column(name = "tag_id")
    private Long id;

    private String tagName;

//    @OneToMany(mappedBy = "tag")
//    private List<PostTag> postTags = new ArrayList<>();
}
