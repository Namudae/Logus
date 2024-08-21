package com.logus.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
