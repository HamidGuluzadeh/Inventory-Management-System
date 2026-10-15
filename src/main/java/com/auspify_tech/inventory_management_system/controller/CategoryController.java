package com.auspify_tech.inventory_management_system.controller;

import com.auspify_tech.inventory_management_system.dto.SuccessDto;
import com.auspify_tech.inventory_management_system.dto.request.CategoryRequestDto;
import com.auspify_tech.inventory_management_system.dto.response.CategoryResponseDto;
import com.auspify_tech.inventory_management_system.model.SuccessStatus;
import com.auspify_tech.inventory_management_system.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Tag(name = "Category Controller", description = "APIs for creating, updating, retrieving, and deleting product categories")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/all")
    @Operation(summary = "Get all categories", description = "Retrieves a paginated list of all registered product categories")
    public ResponseEntity<SuccessDto<Page<CategoryResponseDto>>> getAllCategories(@RequestParam(defaultValue = "0") int page,
                                                                                  @RequestParam(defaultValue = "20") int size) {
        Page<CategoryResponseDto> response = categoryService.getAllCategories(page, size);
        SuccessDto<Page<CategoryResponseDto>> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    @Operation(summary = "Get category by ID", description = "Retrieves detailed information of a specific product category by its unique ID")
    public ResponseEntity<SuccessDto<CategoryResponseDto>> getCategoryById(@PathVariable String categoryId) {
        CategoryResponseDto response = categoryService.getCategoryById(categoryId);
        SuccessDto<CategoryResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @PostMapping("/new")
    @Operation(summary = "Create category", description = "Creates a new product category in the system")
    public ResponseEntity<SuccessDto<CategoryResponseDto>> addCategory(@RequestBody @Valid CategoryRequestDto request) {
        CategoryResponseDto response = categoryService.addCategory(request);
        SuccessDto<CategoryResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.CREATED);
    }

    @PutMapping("/{categoryId}")
    @Operation(summary = "Update category", description = "Updates the details of an existing product category by its unique ID.")
    public ResponseEntity<SuccessDto<CategoryResponseDto>> updateCategory(@PathVariable String categoryId,
                                                                          @RequestBody @Valid CategoryRequestDto request) {
        CategoryResponseDto response = categoryService.updateCategory(categoryId, request);
        SuccessDto<CategoryResponseDto> successDto = new SuccessDto<>(SuccessStatus.SUCCESS, response);
        return new ResponseEntity<>(successDto, HttpStatus.OK);
    }

    @DeleteMapping("/{categoryId}")
    @Operation(summary = "Delete category", description = "Removes a product category from the system by its unique ID.")
    public ResponseEntity<Void> deleteCategory(@PathVariable String categoryId) {
        categoryService.deleteCategory(categoryId);
        return ResponseEntity.noContent().build();
    }
}
