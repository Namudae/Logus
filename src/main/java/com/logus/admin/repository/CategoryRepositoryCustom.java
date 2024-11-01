package com.logus.admin.repository;

import com.logus.admin.dto.CategoryResponseDto;

import java.util.List;

public interface CategoryRepositoryCustom {
    List<CategoryResponseDto> selectCategory(Long parentId);
}
