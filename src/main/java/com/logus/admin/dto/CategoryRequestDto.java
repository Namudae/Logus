package com.logus.admin.dto;

import com.logus.admin.entity.Category;
import com.logus.blog.entity.Blog;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Builder
public class CategoryRequestDto {

    private Long parentId;
    private Integer orderSeq;
    @NotBlank(message = "카테고리명을 입력하세요.")
    @Size(max=30, message = "카테고리명을 30자 이내로 작성해 주세요.")
    private String categoryName;


    /*
    Dto -> toEntity
     */
    public Category toEntity(Category parent) {
        return Category.builder()
                .parent(parent)
                .orderSeq(orderSeq)
                .categoryName(categoryName)
                .build();
    }

}
