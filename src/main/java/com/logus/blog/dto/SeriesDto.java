package com.logus.blog.dto;

import com.logus.blog.entity.Series;
import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class SeriesDto {
    private Long seriesId;
    private String seriesName;

    public SeriesDto(Series series) {
        this.seriesId = series.getId();
        this.seriesName = series.getSeriesName();
    }
}
