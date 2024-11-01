package com.logus.admin.controller;

import com.logus.admin.dto.CategoryRequestDto;
import com.logus.admin.dto.CategoryResponseDto;
import com.logus.blog.dto.BlogRequestDto;
import com.logus.blog.dto.BlogResponseDto;
import com.logus.admin.service.CategoryService;
import com.logus.common.controller.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    /**
     * 카테고리 조회
     */
    @GetMapping("/category")
    public ApiResponse<List<CategoryResponseDto>> selectCategory(@RequestParam(required = false) Long parentId) {
        List<CategoryResponseDto> dto = categoryService.selectCategory(parentId);

        return ApiResponse.ok(dto);
    }

    /**
     * 카테고리 등록
     */
    @PostMapping("/category")
    public ApiResponse<Map<String, Long>> createCategory(@RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        Long categoryId = categoryService.createCategory(categoryRequestDto);

        return ApiResponse.ok(Map.of("categoryId", categoryId));
    }


    /**
     * 카테고리 수정
     */
    @PutMapping("/category")
    public ApiResponse<Map<String, Long>> updateCategory(@RequestParam("categoryId") Long categoryId,
                                                         @RequestBody @Valid CategoryRequestDto categoryRequestDto) {
        categoryService.updateCategory(categoryId, categoryRequestDto);

        return ApiResponse.ok(Map.of("categoryId", categoryId));
    }


    /**
     * 카테고리 삭제
     */
    @DeleteMapping("/category")
    public ApiResponse<String> deleteCategory(@RequestParam("categoryId") Long categoryId) {
        categoryService.deleteCategory(categoryId);

        return ApiResponse.ok();
    }


    /**
     * 카테고리 순서(
     */
}
