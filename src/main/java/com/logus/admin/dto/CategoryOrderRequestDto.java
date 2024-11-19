package com.logus.admin.dto;

import com.logus.blog.dto.SeriesOrderRequestDto;
import lombok.*;

import java.util.List;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class CategoryOrderRequestDto {
    private Long categoryId;
    private Integer orderSeq;

    // Getter and Setter
    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public Integer getOrderSeq() {
        return orderSeq;
    }

    public void setCategoryOrder(Integer orderSeq) {
        this.orderSeq = orderSeq;
    }
}
