package com.logus.blog.entity;

import com.logus.blog.dto.CommentRequestDto;
import com.logus.blog.dto.SeriesOrderRequestDto;
import com.logus.blog.dto.SeriesRequestDto;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    //=====비즈니스 로직
    public void deleteImgUrl() {
        this.imgUrl = null;
    }

    public void updateSeries(SeriesRequestDto seriesRequestDto, String imgUrl) {
        this.seriesName = seriesRequestDto.getSeriesName();
        this.seriesOrder = seriesRequestDto.getSeriesOrder();
        this.imgUrl = imgUrl;
    }

    public void updateSeries(SeriesOrderRequestDto.SeriesDto seriesRequestDto) {
        this.id = seriesRequestDto.getSeriesId();
        this.seriesOrder = seriesRequestDto.getSeriesOrder();
    }


}
