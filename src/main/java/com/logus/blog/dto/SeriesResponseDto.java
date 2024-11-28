package com.logus.blog.dto;

import com.logus.blog.entity.Series;
import lombok.*;

@RequiredArgsConstructor
@Getter
@Data
@Builder
public class SeriesResponseDto {
    private Long seriesId;
    private String imgUrl;

    public SeriesResponseDto(Long seriesId, String imgUrl) {
        this.seriesId = seriesId;
        this.imgUrl = imgUrl;
    }
}
