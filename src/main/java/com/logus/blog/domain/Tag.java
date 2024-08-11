package com.logus.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

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
