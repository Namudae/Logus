package com.logus.blog.dto;

import lombok.*;

import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class SeriesOrderRequestDto {

    private List<SeriesDto> seriesList;

    // Getter and Setter
    public List<SeriesDto> getSeriesList() {
        return seriesList;
    }

    public void setSeriesList(List<SeriesDto> seriesList) {
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
