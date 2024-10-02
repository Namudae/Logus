package com.logus.blog.dto;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class SeriesResponseDto {
    private Long seriesId;
    private String seriesName;
}
