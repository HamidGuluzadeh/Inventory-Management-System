package com.auspify_tech.inventory_management_system.service.implement;

import com.auspify_tech.inventory_management_system.dto.request.CategoryRequestDto;
import com.auspify_tech.inventory_management_system.dto.response.CategoryResponseDto;
import com.auspify_tech.inventory_management_system.entity.CategoryEntity;
import com.auspify_tech.inventory_management_system.exception.ConflictException;
import com.auspify_tech.inventory_management_system.exception.ResourceAlreadyExistsException;
import com.auspify_tech.inventory_management_system.exception.ResourceNotFoundException;
import com.auspify_tech.inventory_management_system.mapper.CategoryMapper;
import com.auspify_tech.inventory_management_system.repository.CategoryRepository;
import com.auspify_tech.inventory_management_system.repository.ProductRepository;
import com.auspify_tech.inventory_management_system.service.CategoryService;
import jakarta.transaction.Transactional;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class CategoryServiceImpl implements CategoryService {
    CategoryRepository categoryRepository;
    ProductRepository productRepository;
    CategoryMapper categoryMapper;

    @Override
    public Page<CategoryResponseDto> getAllCategories(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);

        Page<CategoryEntity> categoryEntities = categoryRepository.findAll(pageable);

        return categoryEntities.map(categoryMapper::mapEntityToResponse);
    }

    @Override
    public CategoryResponseDto getCategoryById(String categoryId) {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        return categoryMapper.mapEntityToResponse(categoryEntity);
    }

    @Override
    @Transactional
    public CategoryResponseDto addCategory(CategoryRequestDto request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException(request.name() + " category already exists!");
        }

        CategoryEntity parentCategory = null;

        if (!Objects.isNull(request.parentId())) {
            parentCategory = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
        }

        CategoryEntity categoryEntity = categoryMapper.mapRequestToEntity(request);
        categoryEntity.setCreatedAt(Instant.now());

        CategoryEntity savedEntity = categoryRepository.save(categoryEntity);

        return categoryMapper.mapEntityToResponse(savedEntity);
    }

    @Override
    @Transactional
    public CategoryResponseDto updateCategory(String categoryId, CategoryRequestDto request) {
        CategoryEntity categoryEntity = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));

        if (categoryEntity.getName().equalsIgnoreCase(request.name())
                && categoryRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException(request.name() + " category already exists!");
        }

        CategoryEntity parentCategory = null;

        if (!Objects.isNull(request.parentId())) {
            if (categoryId.equals(request.parentId())) {
                throw new ConflictException("Category cannot be own parent!");
            }

            parentCategory = categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Category not found!"));
        }

        categoryEntity = categoryMapper.mapRequestToEntity(request);

        CategoryEntity savedEntity = categoryRepository.save(categoryEntity);

        return categoryMapper.mapEntityToResponse(savedEntity);
    }

    @Override
    @Transactional
    public void deleteCategory(String categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ResourceNotFoundException("Category not found!");
        }

        if (categoryRepository.existsByParentId(categoryId)) {
            throw new ConflictException("Category cannot be deleted due to having subcategories!");
        }

        if (productRepository.existsByCategoryId(categoryId)) {
            throw new ConflictException("Category cannot be deleted due to having its products!");
        }

        categoryRepository.deleteById(categoryId);
    }
}
