package com.logus.blog.dto;

import com.logus.blog.entity.Series;
import lombok.*;

import static com.logus.common.service.S3Service.CLOUD_FRONT_DOMAIN_NAME;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class SeriesResponseDto {
    private Long seriesId;
    private String seriesName;
    private Byte seriesOrder;
    private String imgUrl;

    public SeriesResponseDto(Series series, String imgUrl) {
        this.seriesId = series.getId();
        this.seriesName = series.getSeriesName();
        this.seriesOrder = series.getSeriesOrder();
        this.imgUrl = imgUrl;
    }
}
