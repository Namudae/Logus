package com.logus.blog.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Long id;

    @Column(length = 30)
    private String tagName;

//    @OneToMany(mappedBy = "tag")
//    private List<PostTag> postTags = new ArrayList<>();
}
