package com.logus.admin.dto;

import lombok.*;

@AllArgsConstructor
@RequiredArgsConstructor
@Getter
@Data
@Builder
public class CategoryResponseDto {

    private Long categoryId;
    private Long parentId;
    private Integer orderSeq;
    private String categoryName;
}
