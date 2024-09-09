package com.logus.blog.service;

import com.logus.blog.entity.Category;
import com.logus.blog.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category getReferenceById(Long categoryId) {
        return categoryId == null ? null :
                categoryRepository.getReferenceById(categoryId);
    }
}
