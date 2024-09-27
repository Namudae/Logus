package com.logus.blog.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
public class Series {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "series_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT))
    private Blog blog;

    @Column(length = 100)
    private String seriesName;

    private Byte seriesOrder;

    private String imgUrl;

//    @OneToMany(mappedBy = "series")
//    private List<Post> posts = new ArrayList<>();


}
