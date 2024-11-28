package com.logus.blog.dto;

import com.logus.blog.entity.Series;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class SeriesListResponseDto {
    private Long seriesId;
    private String seriesName;
    private Byte seriesOrder;
    private String imgUrl;

    public SeriesListResponseDto(Series series, String imgUrl) {
        this.seriesId = series.getId();
        this.seriesName = series.getSeriesName();
        this.seriesOrder = series.getSeriesOrder();
        this.imgUrl = imgUrl;
    }

    public SeriesListResponseDto(Long seriesId, String imgUrl) {
        this.seriesId = seriesId;
        this.imgUrl = imgUrl;
    }
}
