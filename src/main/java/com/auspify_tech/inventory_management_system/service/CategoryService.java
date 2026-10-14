package com.auspify_tech.inventory_management_system.service;

import com.auspify_tech.inventory_management_system.dto.request.CategoryRequestDto;
import com.auspify_tech.inventory_management_system.dto.response.CategoryResponseDto;
import org.springframework.data.domain.Page;

public interface CategoryService {

    Page<CategoryResponseDto> getAllCategories(int page, int size);

    CategoryResponseDto getCategoryById(String categoryId);

    CategoryResponseDto addCategory(CategoryRequestDto request);

    CategoryResponseDto updateCategory(String categoryId, CategoryRequestDto request);

    void deleteCategory(String categoryId);

}
