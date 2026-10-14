package com.auspify_tech.inventory_management_system.controller;

import com.auspify_tech.inventory_management_system.dto.SuccessDto;
import com.auspify_tech.inventory_management_system.dto.request.CategoryRequestDto;
import com.auspify_tech.inventory_management_system.dto.response.CategoryResponseDto;
import com.auspify_tech.inventory_management_system.model.SuccessStatus;
import com.auspify_tech.inventory_management_system.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/all")
    public ResponseEntity<SuccessDto<Page<CategoryResponseDto>>> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "20") int size) {
        Page<CategoryResponseDto> response = categoryService.getAllCategories(page, size);
        SuccessDto<Page<CategoryResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<SuccessDto<CategoryResponseDto>> getCategoryById(@PathVariable String categoryId) {
        CategoryResponseDto response = categoryService.getCategoryById(categoryId);
        SuccessDto<CategoryResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<SuccessDto<CategoryResponseDto>> addCategory(@RequestBody @Valid CategoryRequestDto request) {
        CategoryResponseDto response = categoryService.addCategory(request);
        SuccessDto<CategoryResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<SuccessDto<CategoryResponseDto>> updateCategory(@PathVariable String categoryId,
                                                                          @RequestBody @Valid CategoryRequestDto request) {
        CategoryResponseDto response = categoryService.updateCategory(categoryId, request);
        SuccessDto<CategoryResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }


    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
