package com.logus.blog.dto;

import com.logus.blog.entity.Blog;
import com.logus.blog.entity.Series;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class SeriesRequestDto {
    private Long blogId;
    private Long seriesId;

    @NotBlank(message = "시리즈명을 입력하세요.")
    @Size(max=30, message = "시리즈명을 30자 이내로 작성해 주세요.")
    private String seriesName;

    private Byte seriesOrder;
    private String imgUrl;

    private List<SeriesRequestDto.SeriesDto> seriesList;

    public SeriesRequestDto(Series series) {
        this.seriesId = series.getId();
        this.seriesName = series.getSeriesName();
    }

    public Series toEntity(Blog blog, String imgUrl) {
        return Series.builder()
                .blog(blog)
                .seriesName(seriesName)
                .seriesOrder(seriesOrder)
                .imgUrl(imgUrl)
                .build();
    }

    // Getter and Setter
    public List<SeriesRequestDto.SeriesDto> getSeriesList() {
        return seriesList;
    }

    public void setSeriesList(List<SeriesRequestDto.SeriesDto> seriesList) {
        this.seriesList = seriesList;
    }

    // 내부 DTO 클래스
    public static class SeriesDto {
        private Long seriesId;
        private Byte seriesOrder;

        // Getter and Setter
        public Long getSeriesId() {
            return seriesId;
        }

        public void setSeriesId(Long seriesId) {
            this.seriesId = seriesId;
        }

        public Byte getSeriesOrder() {
            return seriesOrder;
        }

        public void setSeriesOrder(Byte seriesOrder) {
            this.seriesOrder = seriesOrder;
        }
    }
}
