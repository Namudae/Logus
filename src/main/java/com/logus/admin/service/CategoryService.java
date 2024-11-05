package com.logus.admin.service;

import com.logus.admin.dto.CategoryRequestDto;
import com.logus.admin.dto.CategoryResponseDto;
import com.logus.admin.entity.Category;
import com.logus.admin.repository.CategoryRepository;
import com.logus.common.exception.CustomException;
import com.logus.common.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category getById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new CustomException(ErrorCode.CATEGORY_NOT_FOUND));
    }

    public Category getReferenceById(Long categoryId) {
        return categoryId == null ? null :
                categoryRepository.getReferenceById(categoryId);
    }

    public List<CategoryResponseDto> selectCategory(Long parentId) {
        return categoryRepository.selectCategory(parentId);
    }

    @Transactional
    public Long createCategory(CategoryRequestDto categoryRequestDto) {
        Category parent = null;
        if (categoryRequestDto.getParentId() != null) {
            parent = getById(categoryRequestDto.getParentId());
        }

        Category category = categoryRequestDto.toEntity(parent);

        // 부모 카테고리가 있을 경우 자식 카테고리 추가
        if (parent != null) {
            parent.addChildCategory(category);
        }

        categoryRepository.save(category);

        return category.getId();
    }

    @Transactional
    public void updateCategory(Long categoryId, CategoryRequestDto categoryRequestDto) {
        Category category = getById(categoryId);
        //카테고리명, 순서 update
        category.updateCategory(categoryRequestDto);
    }

    public void deleteCategory(Long categoryId) {
        Category category = getById(categoryId);
        categoryRepository.delete(category);
    }
}
