package com.logus.blog.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
public class Series {

    @Id
    @GeneratedValue
    @Column(name = "series_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    private String seriesName;

    private String sriesOrder;

//    @OneToMany(mappedBy = "series")
//    private List<Post> posts = new ArrayList<>();


}
